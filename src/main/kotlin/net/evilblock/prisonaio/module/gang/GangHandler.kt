/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang

import com.boydti.fawe.bukkit.wrapper.AsyncWorld
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import com.sk89q.worldedit.Vector
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.bukkit.generator.EmptyChunkGenerator
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.gang.booster.task.GangBoosterLogic
import net.evilblock.prisonaio.module.gang.permission.GangPermission
import net.evilblock.prisonaio.module.gang.service.GangInvitesExpiryService
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.service.ServiceRegistry
import net.evilblock.prisonaio.util.Permissions
import net.evilblock.source.chat.filter.ChatFilterHandler
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.Skull
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.set

/**
 * A 2-dimensional grid filled in a linear sequence.
 */
object GangHandler : PluginHandler() {

    val CHAT_PREFIX = "${ChatColor.GRAY}[${ChatColor.GOLD}${ChatColor.BOLD}Gangs${ChatColor.GRAY}] "
    val INVITE_EXPIRE_TIME = TimeUnit.HOURS.toMillis(2L)

    private var gridIndex = 0
    private val grid: HashMap<Int, Gang> = hashMapOf()

    private val gangByName: MutableMap<String, Gang> = ConcurrentHashMap()
    private val gangByPlayer: MutableMap<UUID, Gang> = ConcurrentHashMap()

    private val invitesByPlayer: MutableMap<UUID, MutableSet<Gang>> = ConcurrentHashMap()

    private val currentlyVisiting: MutableMap<UUID, Gang> = ConcurrentHashMap()

    var asyncWorld: AsyncWorld? = null

    override fun getModule(): PluginModule {
        return GangsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "gangs.json")
    }

    override fun saveData() {
        super.saveData()

        saveGrid()
        getGridWorld().save()
    }

    override fun initialLoad() {
        super.initialLoad()

        loadWorld()
        loadGrid()

        Tasks.asyncTimer(GangBoosterLogic, 20L, 20L)

        Tasks.asyncTimer(0, 20L * 60L) {
            for (gang in grid.values) {
                gang.updateCachedValue()
            }
        }

        ServiceRegistry.register(GangInvitesExpiryService, 20L, 20L * 15L)
    }

    private fun loadWorld() {
        WorldCreator(GangsModule.getGridWorldName())
                .generator(EmptyChunkGenerator())
                .generateStructures(false)
                .createWorld()

        asyncWorld = getAsyncGridWorld()
    }



    /**
     * Loads the grid data from the file system.
     */
    private fun loadGrid() {
        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val gridType = object : TypeToken<ArrayList<Gang>>() {}.type
                val gridList = Cubed.gson.fromJson(reader, gridType) as ArrayList<Gang>

                for (gang in gridList) {
                    // initialize transient data
                    gang.initializeData()

                    // synchronize this gang data into our caches
                    synchronizeCaches(gang)

                    // update region block cache
                    RegionHandler.updateBlockCache(gang)

                    // set highest grid index so we know where to create the next gang
                    if (gang.gridIndex > this.gridIndex) {
                        this.gridIndex = gang.gridIndex
                    }
                }
            }
        }
    }

    fun saveGrid() {
        Files.write(Cubed.gson.toJson(grid.values), getInternalDataFile(), Charsets.UTF_8)
    }

    /**
     * Gets the [World] that hosts the grid.
     */
    fun getGridWorld(): World {
        return Bukkit.getWorld(GangsModule.getGridWorldName())
    }

    /**
     * Gets the asynchronous [World] that hosts the grid.
     */
    fun getAsyncGridWorld(): AsyncWorld {
        if (asyncWorld == null) {
            asyncWorld = AsyncWorld.wrap(getGridWorld())
        }
        return asyncWorld!!
    }

    /**
     * Gets all of the [Gang]s in the grid.
     */
    fun getAllGangs(): Set<Gang> {
        return HashSet(grid.values)
    }

    /**
     * Gets the [Gang] with the given [name].
     */
    fun getGangByName(name: String): Gang? {
        return gangByName[name.toLowerCase()]
    }

    /**
     * Gets the [Gang] with the given [uuid].
     */
    fun getGangById(uuid: UUID): Gang? {
        for (gang in getAllGangs()) {
            if (gang.uuid == uuid) {
                return gang
            }
        }
        return null
    }

    /**
     * Gets the [Gang] that the given [location] belongs to.
     */
    fun getGangByLocation(location: Location): Gang? {
        for (gang in getAllGangs()) {
            if (gang.getCuboid().contains(location)) {
                return gang
            }
        }
        return null
    }

    /**
     * Returns the [Gang] for the given [player].
     */
    fun getGangByPlayer(player: UUID): Gang? {
        return gangByPlayer[player]
    }

    /**
     * Returns the [Gang] for the given [player].
     */
    fun getGangByPlayer(player: Player): Gang? {
        return gangByPlayer[player.uniqueId]
    }

    /**
     * Gets the [Gang] a player is currently at.
     */
    fun getVisitingGang(player: Player): Gang? {
        return currentlyVisiting[player.uniqueId]
    }

    /**
     * Updates the `visiting gang` cache to the given [Gang] for the given [player].
     */
    fun updateVisitingGang(player: Player, gang: Gang?) {
        if (gang == null) {
            currentlyVisiting.remove(player.uniqueId)
        } else {
            currentlyVisiting[player.uniqueId] = gang
        }
    }

    fun fetchPreviousGang(uuid: UUID): Gang? {
        try {
            val redisValue = Cubed.instance.redis.runRedisCommand { redis -> redis.get("Gangs:LastGang:player.$uuid") }

            if (redisValue == null || redisValue.isBlank()) {
                return null
            }

            return getGangById(UUID.fromString(redisValue))
        } catch (e: Exception) {
            return null
        }
    }

    fun updatePreviousGang(uuid: UUID, gang: Gang?) {
        Cubed.instance.redis.runRedisCommand<Unit> { redis ->
            if (gang == null) {
                redis.del("Gangs:LastGang:player.$uuid")
            } else {
                redis.set("Gangs:LastGang:player.$uuid", gang.uuid.toString())
            }
        }
    }

    /**
     * Returns a set of [Gang]s that the given [player] has been invited to.
     */
    fun getGangsInvitedTo(player: Player): Set<Gang> {
        return invitesByPlayer.getOrDefault(player.uniqueId, emptySet())
    }

    fun trackInvited(gang: Gang, player: UUID) {
        if (invitesByPlayer.containsKey(player)) {
            invitesByPlayer[player]!!.add(gang)
        } else {
            invitesByPlayer[player] = hashSetOf(gang)
        }
    }

    fun trackInvited(gang: Gang, player: Player) {
        trackInvited(gang, player.uniqueId)
    }

    fun forgetInvited(gang: Gang, player: UUID) {
        if (invitesByPlayer.containsKey(player)) {
            invitesByPlayer[player]!!.remove(gang)
        }
    }

    fun forgetInvited(gang: Gang, player: Player) {
        forgetInvited(gang, player)
    }

    fun updateGangAccess(uuid: UUID, gang: Gang, joinable: Boolean) {
        if (joinable) {
            gangByPlayer[uuid] = gang
        } else {
            gangByPlayer.remove(uuid)
        }
    }

    fun hasBypass(player: Player): Boolean {
        return player.hasPermission(Permissions.GANGS_ADMIN) && RegionBypass.hasBypass(player)
    }

    fun attemptJoinGang(player: Player, gang: Gang) {
        if (getGangByPlayer(player) != null) {
            player.sendMessage("${ChatColor.RED}You already belong to a gang!")
            return
        }

        if (gang.isMember(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You are already a member of that gang!")
            return
        }

        if (!gang.isInvited(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}You haven't been invited to join that gang!")
            return
        }

        if (gang.getMembers().size >= GangsModule.getMaxMembers()) {
            player.sendMessage("${ChatColor.RED}That gang has the maximum amount of members! Somebody will have to leave or be kicked for you to be able to join.")
            return
        }

        gang.memberJoin(player.uniqueId)

        Tasks.sync {
            attemptJoinSession(player, gang)
        }
    }

    fun attemptJoinSession(player: Player, gang: Gang) {
        if (hasBypass(player)) {
            RegionBypass.attemptNotify(player)
            gang.joinSession(player)
        } else {
            if (gang.leader != player.uniqueId) {
                if (gang.visitors.size >= 50) {
                    player.sendMessage("${ChatColor.RED}There are too many players visiting that gang right now to join.")
                    return
                }
            }

            if (!gang.testPermission(player, GangPermission.ALLOW_VISITORS)) {
                return
            }

            gang.joinSession(player)
        }
    }

    private fun synchronizeCaches(gang: Gang) {
        grid[gang.gridIndex] = gang
        gangByName[gang.name.toLowerCase()] = gang

        for (member in gang.getMembers().values) {
            updateGangAccess(member.uuid, gang, true)
        }
    }

    fun forgetGang(gang: Gang) {
        grid.remove(gang.gridIndex)
        gangByName.remove(gang.name.toLowerCase())
    }

    fun renameGang(gang: Gang, name: String) {
        gangByName.remove(gang.name.toLowerCase())
        gang.name = name
        synchronizeCaches(gang)
    }

    /**
     * Creates and inserts a [Gang] into the grid.
     *
     * This method should always be called asynchronously.
     */
    @Throws(IllegalStateException::class)
    fun createNewGang(player: Player, name: String, onFinish: (Gang) -> Unit) {
        assert(!Bukkit.isPrimaryThread()) { "Cannot generate new gang on primary thread" }

        if (ChatFilterHandler.filterMessage(name) != null) {
            player.sendMessage("${ChatColor.RED}The name you input contains inappropriate content. Please try a different name.")
            return
        }

        if (!name.matches(EzPrompt.IDENTIFIER_REGEX)) {
            player.sendMessage("${ChatColor.RED}The name you input does not match the regex pattern ${EzPrompt.IDENTIFIER_REGEX.pattern}.")
            return
        }

        if (getGangByPlayer(player) != null) {
            player.sendMessage("${ChatColor.RED}You can only have one gang at a time. To create a new gang, delete your old gang and then try again.")
            return
        }

        if (getGangByName(name) != null) {
            player.sendMessage("${ChatColor.RED}The name `$name` is already taken by another gang.")
            return
        }

        if (name.length > GangsModule.getMaxNameLength()) {
            player.sendMessage("${ChatColor.RED}A gang's name can only be ${GangsModule.getMaxNameLength()} characters long. The name you entered was ${name.length} characters.")
            return
        }

        player.sendMessage("${ChatColor.GREEN}Creating your gang...")

        val schematicFile = GangsModule.getIslandSchematicFile()
        if (!schematicFile.exists()) {
            throw IllegalStateException("Schematic file doesn't exist: ${schematicFile.name}")
        }

        val gridIndex = ++gridIndex
        val schematicData = GridSchematicData.of(gridIndex, schematicFile)

        pasteSchematic(schematicData) { success ->
            if (!success) {
                throw IllegalStateException("Failed to paste schematic")
            }

            val scanResults = startSchematicScan(schematicData.pasteLocation)

            if (scanResults.spawnLocation == null) {
                throw IllegalStateException("Missing player spawn location")
            }

            if (scanResults.guideLocation == null) {
                throw IllegalStateException("Missing jerry spawn location")
            }

            Tasks.sync {
                for (block in scanResults.blocks) {
                    block.type = Material.AIR
                    block.state.update()
                }
            }

            val gangLeader = GangMember(player.uniqueId)
            gangLeader.role = GangMember.Role.LEADER

            val gang = Gang(gridIndex, name, player.uniqueId, scanResults.spawnLocation!!, scanResults.guideLocation!!, schematicData.cuboid)
            gang.initializeData()
            gang.addMember(gangLeader)

            RegionHandler.updateBlockCache(gang)

            synchronizeCaches(gang)
            saveData()

            onFinish.invoke(gang)
        }
    }

    fun disbandGang(gang: Gang) {
        gang.sendMessagesToMembers("${ChatColor.YELLOW}The gang has been disbanded by the leader.")

        for (member in gang.getMembers().keys) {
            updateGangAccess(member, gang, false)
        }

        gang.kickVisitors(force = true)

        forgetGang(gang)
        RegionHandler.clearBlockCache(gang)
    }

    /**
     * Creates and inserts a [Gang] into the grid.
     *
     * This method should always be called asynchronously.
     */
    @Throws(IllegalStateException::class)
    fun resetGang(gang: Gang, onFinish: () -> Unit) {
        assert(!Bukkit.isPrimaryThread()) { "Cannot reset gang on primary thread" }

        val schematicFile = GangsModule.getIslandSchematicFile()
        if (!schematicFile.exists()) {
            throw IllegalStateException("Schematic file doesn't exist: ${schematicFile.name}")
        }

        val schematicData = GridSchematicData.of(gang.gridIndex, schematicFile)

        pasteSchematic(schematicData) { success ->
            if (!success) {
                throw IllegalStateException("Failed to paste schematic")
            }

            val scanResults = startSchematicScan(schematicData.pasteLocation)

            if (scanResults.spawnLocation == null) {
                throw IllegalStateException("Missing player spawn location")
            }

            if (scanResults.guideLocation == null) {
                throw IllegalStateException("Missing jerry spawn location")
            }

            Tasks.sync {
                for (block in scanResults.blocks) {
                    block.type = Material.AIR
                    block.state.update()
                }
            }

            RegionHandler.clearBlockCache(gang)

            gang.homeLocation = scanResults.spawnLocation!!
            gang.guideNpc.updateLocation(scanResults.guideLocation!!)
            gang.setCuboid(schematicData.cuboid)

            RegionHandler.updateBlockCache(gang)

            onFinish.invoke()
        }
    }

    @Throws(IllegalStateException::class)
    fun pasteSchematic(schematicData: GridSchematicData, onFinish: (Boolean) -> Unit) {
        val schematicFile = GangsModule.getIslandSchematicFile()
        if (!schematicFile.exists()) {
            throw IllegalStateException("Schematic file doesn't exist: ${schematicFile.name}")
        }

        try {
            Tasks.sync {
                // preload affected chunks
                val chunks = schematicData.cuboid.getChunks()
                for (chunk in chunks) {
                    if (!chunk.isLoaded) {
                        if (!chunk.load(true)) {
                            throw IllegalStateException("Couldn't generate chunks in grid world")
                        }
                    }
                }

                Tasks.async {
                    val pasteVector = Vector(schematicData.pasteLocation.x, schematicData.pasteLocation.y, schematicData.pasteLocation.z)
                    WorldEditUtils.paste(schematicFile, schematicData.pasteLocation.world, pasteVector, true)
                    println("Pasted schematic at vector ${schematicData.pasteLocation.x}, ${schematicData.pasteLocation.y}, ${schematicData.pasteLocation.z}")

                    onFinish.invoke(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onFinish.invoke(false)
        }
    }

    /**
     * Container for storing the data needed to paste a schematic at a given location.
     */
    data class GridSchematicData(
        val blockCoords: Pair<Int, Int>,
        val pasteLocation: Location,
        val schematicSize: Vector,
        val cuboid: Cuboid
    ) {

        companion object {
            @JvmStatic
            fun of(gridIndex: Int, schematicFile: File): GridSchematicData {
                val blockCoords = gridCoordsToBlockCoords(indexToGrid(gridIndex))
                val pasteLocation = Location(getGridWorld(), blockCoords.first.toDouble(), 68.0, blockCoords.second.toDouble())
                val schematicSize = WorldEditUtils.readSchematicSize(schematicFile)

                return GridSchematicData(
                    blockCoords = blockCoords,
                    pasteLocation = pasteLocation,
                    schematicSize = schematicSize,
                    cuboid = Cuboid(pasteLocation, pasteLocation.clone().add(schematicSize.x, schematicSize.y, schematicSize.z))
                )
            }
        }

    }

    /**
     * Container for storing the result of a pasted schematic scan.
     */
    data class SchematicScanResults(
        var spawnLocation: Location? = null,
        var guideLocation: Location? = null,
        var blocks: HashSet<Block> = hashSetOf()
    )

    /**
     * Finds the spawn points for a given location.
     */
    private fun startSchematicScan(start: Location): SchematicScanResults {
        val schematicSize = WorldEditUtils.readSchematicSize(GangsModule.getIslandSchematicFile())

        val minPoint = start.clone()
        val maxPoint = start.clone().add(schematicSize.x, schematicSize.y, schematicSize.z)

        val gridWorld = getGridWorld()
        val spawnPointScan = SchematicScanResults()

        for (x in minPoint.x.toInt()..maxPoint.x.toInt()) {
            for (y in minPoint.y.toInt()..maxPoint.y.toInt()) {
                zLoop@ for (z in minPoint.z.toInt()..maxPoint.z.toInt()) {
                    val block = gridWorld.getBlockAt(x, y, z)

                    if (block.state is Skull) {
                        val skull = block.state as Skull
                        when (skull.skullType) {
                            SkullType.PLAYER -> {
                                spawnPointScan.spawnLocation = block.location.add(0.5, 2.0, 0.5)
                                spawnPointScan.spawnLocation!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
                            }
                            SkullType.CREEPER -> {
                                spawnPointScan.guideLocation = block.location.add(0.5, 0.0, 0.5)
                                spawnPointScan.guideLocation!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
                            }
                            else -> continue@zLoop
                        }

                        spawnPointScan.blocks.add(block)
                    }
                }
            }
        }

        return spawnPointScan
    }

    private fun indexToGrid(index: Int): Pair<Int, Int> {
        if (index == 0) {
            return Pair(0, 0)
        }

        val x = index % GangsModule.getGridColumns()
        val y = index / GangsModule.getGridColumns()

        return Pair(x, y)
    }

    private fun gridToIndex(x: Int, y: Int): Int {
        return x + (GangsModule.getGridColumns() * y)
    }

    private fun gridCoordsToBlockCoords(pos: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(pos.first * GangsModule.getGridGutterWidth(), pos.second * GangsModule.getGridGutterWidth())
    }

}