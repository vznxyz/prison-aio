/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.logging.ErrorHandler
import net.evilblock.cubed.logging.LogFile
import net.evilblock.cubed.logging.LogHandler
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.store.bukkit.UUIDCache
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.user.economy.EconomyProvider
import net.evilblock.prisonaio.module.user.event.UserUnloadEvent
import net.milkbowl.vault.economy.Economy
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicePriority
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object UserHandler : PluginHandler() {

    val NICKNAME_COLORS: List<ChatColor> = listOf(ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, ChatColor.WHITE, ChatColor.GRAY)
    val NICKNAME_STYLES: List<ChatColor> = listOf(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE)

    val MINIMUM_MONEY_BALANCE = BigDecimal(0.0)
    val MINIMUM_TOKEN_BALANCE = BigInteger("0")

    private val JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()

    private val usersCollection: MongoCollection<Document> = PrisonAIO.instance.database.getCollection("users")
    private val usersMap: ConcurrentHashMap<UUID, User> = ConcurrentHashMap()

    val economyLogFile: LogFile = LogFile(File(File(getModule().getPluginFramework().dataFolder, "logs"), "economy.txt"))
    val tokenShopsLogFile: LogFile = LogFile(File(File(getModule().getPluginFramework().dataFolder, "logs"), "token-shops.txt"))

    init {
        usersCollection.createIndex(BasicDBObject("id", 1))
    }

    override fun getModule(): PluginModule {
        return UsersModule
    }

    override fun initialLoad() {
        super.initialLoad()

        LogHandler.trackLogFile(economyLogFile)
        LogHandler.trackLogFile(tokenShopsLogFile)

        getModule().getPluginFramework().server.servicesManager.register(Economy::class.java, EconomyProvider(), getModule().getPluginFramework(), ServicePriority.Highest)

        Tasks.delayed(10L) {
            loadOnlinePlayers()
        }

        Tasks.asyncTimer(20L, 20L) {
            val expired = arrayListOf<User>()

            // find expired users
            for (user in usersMap.values) {
                if (user.getPlayer() == null && user.cacheExpiry != null && System.currentTimeMillis() >= user.cacheExpiry!!) {
                    expired.add(user)
                }
            }

            // save (if needed) and unload expired users
            for (user in expired) {
                val unloadEvent = UserUnloadEvent(user)
                unloadEvent.call()

                if (unloadEvent.isCancelled) {
                    continue
                }

                usersMap.remove(user.uuid)

                if (user.requiresSave) {
                    try {
                        saveUser(user)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        PrisonAIO.instance.logger.severe("Failed to save and unload user ${user.getUsername()}!")
                    }
                }
            }
        }

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        for (user in getUsers()) {
            if (user.requiresSave()) {
                try {
                    saveUser(user)
                } catch (e: Exception) {
                    e.printStackTrace()
                    PrisonAIO.instance.systemLog("Failed to save user data for ${user.getUsername()} (${user.uuid})")
                }
            }
        }
    }

    /**
     * Tries to load all online players' user data into memory.
     */
    private fun loadOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach { player ->
            try {
                val user = loadUser(player.uniqueId)
                user.cacheExpiry = null
                user.statistics.lastPlayTimeSync = System.currentTimeMillis()

                cacheUser(user)
            } catch (exception: Exception) {
                val eventDetails = mapOf(
                    "Player Name" to player.name,
                    "Player UUID" to player.uniqueId.toString(),
                    "Player IP" to player.address.address.hostAddress
                )

                val logId = ErrorHandler.generateErrorLog("loadOnlinePlayer", eventDetails, exception)

                val kickMessage = StringBuilder()
                    .append("${ChatColor.RED}${ChatColor.BOLD}Sorry about that...")
                    .append("\n")
                    .append("${ChatColor.GRAY}We failed to load your user data. Please try again later.")
                    .append("\n")
                    .append("${ChatColor.GRAY}If this error persists, please contact an admin and")
                    .append("\n")
                    .append("${ChatColor.GRAY}provide them this error ID: ${ChatColor.WHITE}$logId")

                player.kickPlayer(kickMessage.toString())
            }
        }
    }

    /**
     * Loads a player's user data.
     */
    fun loadUser(uuid: UUID, fetch: Boolean = true, throws: Boolean = false): User {
        assert(!Bukkit.isPrimaryThread()) { "Cannot load user on primary thread" }

        val document = usersCollection.find(Document("uuid", uuid.toString())).first()
        if (document != null) {
            return Cubed.gson.fromJson(document.toJson(JSON_WRITER_SETTINGS), User::class.java).also {
                it.init()
            }
        }

        if (fetch) {
            val lookupResult = UUIDCache.fetchFromMojang(uuid)
            if (lookupResult.isPresent) {
                val user = User(uuid)
                user.init()

                usersCollection.insertOne(Document.parse(Cubed.gson.toJson(user)))

                return user
            }
        }

        if (throws) {
            throw IllegalStateException("User does not exist in database")
        }

        return User(uuid).also { it.init() }
    }

    fun saveUser(user: User) {
        assert(!Bukkit.isPrimaryThread()) { "Cannot save user on primary thread" }

        val document = Document.parse(Cubed.gson.toJson(user))
        usersCollection.replaceOne(Document("uuid", user.uuid.toString()), document, ReplaceOptions().upsert(true))

        user.requiresSave = false
    }

    /**
     * Gets a copy of the loaded [User]s.
     */
    fun getUsers(): Collection<User> {
        return usersMap.values
    }

    /**
     * Gets a player's cached user data from memory.
     */
    fun getUser(uuid: UUID): User {
        if (!usersMap.containsKey(uuid)) {
            throw IllegalStateException("User $uuid is not cached in memory")
        } else {
            return usersMap[uuid]!!
        }
    }

    fun getUser(player: Player): User {
        return getUser(player.uniqueId)
    }

    fun isUserLoaded(uuid: UUID): Boolean {
        return usersMap.containsKey(uuid)
    }

    fun isUserLoaded(player: Player): Boolean {
        return isUserLoaded(player.uniqueId)
    }

    /**
     * Gets a player's user data from the cache, or by loading and caching.
     */
    fun getOrLoadAndCacheUser(uuid: UUID, lookup: Boolean = true, throws: Boolean = false): User {
        assert(!Bukkit.isPrimaryThread()) { "Cannot load user on primary thread" }

        return if (usersMap.containsKey(uuid)) {
            getUser(uuid)
        } else {
            loadUser(uuid = uuid, fetch = lookup, throws = throws).also { user ->
                user.cacheExpiry = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L)
                usersMap[uuid] = user
            }
        }
    }

    /**
     * Caches a player's user data into memory.
     */
    fun cacheUser(user: User) {
        usersMap[user.uuid] = user
    }

    /**
     * Removes a player's cached user data from memory.
     */
    fun forgetUser(uuid: UUID): User? {
        return usersMap.remove(uuid)
    }

    fun getCollection(): MongoCollection<Document> {
        return usersCollection
    }

}