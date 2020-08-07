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
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.storage.StorageModule
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*
import java.util.concurrent.TimeUnit

object UserHandler : PluginHandler {

    private val JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()

    private val usersCollection: MongoCollection<Document> = StorageModule.database.getCollection("users")
    private val usersMap: MutableMap<UUID, User> = hashMapOf()

    init {
        usersCollection.createIndex(BasicDBObject("id", 1))
    }

    override fun getModule(): PluginModule {
        return UsersModule
    }

    override fun initialLoad() {
        // delay a bit otherwise the method thinks there's no online players
        Tasks.delayed(10L) {
            loadOnlinePlayers()
        }

        Tasks.asyncTimer(20L, 20L) {
            val toRemove = arrayListOf<User>()
            for (user in usersMap.values) {
                if (user.getPlayer() == null && user.cacheExpiry != null && System.currentTimeMillis() >= user.cacheExpiry!!) {
                    toRemove.add(user)
                }
            }

            for (user in toRemove) {
                usersMap.remove(user.uuid)

                if (user.requiresSave) {
                    saveUser(user)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        for (user in getUsers()) {
            if (user.requiresSave()) {
                saveUser(user)
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
    fun loadUser(uuid: UUID, throws: Boolean = false): User {
        assert(!Bukkit.isPrimaryThread()) { "Cannot load user on primary thread" }

        val document = usersCollection.find(Document("uuid", uuid.toString())).first()
        if (document != null) {
            val user = Cubed.gson.fromJson(document.toJson(JSON_WRITER_SETTINGS), User::class.java)
            user.init()
            return user
        }

        if (throws) {
            throw IllegalStateException("User does not exist in database")
        }

        return User(uuid)
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
    fun getUsers(): List<User> {
        return usersMap.values.toList()
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

    fun isUserLoaded(uuid: UUID): Boolean {
        return usersMap.containsKey(uuid)
    }

    /**
     * Gets a player's user data from the cache, or by loading and caching.
     */
    fun getOrLoadAndCacheUser(uuid: UUID): User {
        assert(!Bukkit.isPrimaryThread()) { "Cannot load user on primary thread" }

        return if (!usersMap.containsKey(uuid)) {
            val user = loadUser(uuid = uuid)
            user.cacheExpiry = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L)
            user
        } else {
            getUser(uuid)
        }
    }

    /**
     * Fetches a user's data from the database.
     */
    fun fetchUser(uuid: UUID, throws: Boolean = false): User {
        if (usersMap.containsKey(uuid)) {
            return usersMap[uuid]!!
        }
        return loadUser(uuid, throws)
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