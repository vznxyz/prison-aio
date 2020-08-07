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
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.gang.permission.GangPermission
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.Skull
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.set

/**
 * A 2-dimensional grid filled in a linear sequence.
 */
object GangHandler : PluginHandler {

    private var gridIndex = 0
    private val grid: HashMap<Int, Gang> = hashMapOf()

    private val gangsByName: HashMap<String, Gang> = hashMapOf()
    private val gangAccess: HashMap<UUID, HashSet<Gang>> = hashMapOf()
    private val gangVisiting: HashMap<UUID, Gang> = hashMapOf()

    var asyncWorld: AsyncWorld? = null

    override fun getModule(): PluginModule {
        return GangModule
    }

    override fun getInternalDataFile(): File {
        val worldFolder = getGridWorld().worldFolder
        return File(worldFolder, "gangs.json")
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

        Tasks.asyncTimer(20L * 60L, 20L * 60L) {
            for (gang in grid.values) {
                gang.updateCachedCellValue()
            }
        }
    }

    private fun loadWorld() {
        WorldCreator(GangModule.getGridWorldName())
                .generator(EmptyChunkGenerator())
                .generateStructures(false)
                .createWorld()

        asyncWorld = getAsyncGridWorld()
    }

    /**
     * Gets the [World] that hosts the grid.
     */
    fun getGridWorld(): World {
        return Bukkit.getWorld(GangModule.getGridWorldName())
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
        return gangsByName[name.toLowerCase()]
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
            if (gang.cuboid.contains(location)) {
                return gang
            }
        }
        return null
    }

    /**
     * Gets the [Gang] a player is currently at.
     */
    fun getVisitingGang(player: Player): Gang? {
        return gangVisiting[player.uniqueId]
    }

    /**
     * Updates the `visiting gang` cache to the given [Gang] for the given [player].
     */
    fun updateVisitingGang(player: Player, gang: Gang?) {
        if (gang == null) {
            gangVisiting.remove(player.uniqueId)
        } else {
            gangVisiting[player.uniqueId] = gang
        }
    }

    /**
     * Returns a set of [Gang]s that the given [player] has been invited to.
     */
    fun getGangsInvitedTo(player: Player): Set<Gang> {
        return getAllGangs()
            .filter { it.isInvited(player.uniqueId) }
            .toSet()
    }

    /**
     * Returns a set of [Gang]s that the given [playerUuid] can join.
     */
    fun getAccessibleGangs(playerUuid: UUID): Set<Gang> {
        return gangAccess.getOrDefault(playerUuid, hashSetOf())
            .sortedBy {it.owner == playerUuid }
            .reversed()
            .toSet()
    }

    fun updateGangAccess(uuid: UUID, gang: Gang, joinable: Boolean) {
        gangAccess.putIfAbsent(uuid, HashSet())

        if (joinable) {
            gangAccess[uuid]!!.add(gang)
        } else {
            gangAccess[uuid]!!.remove(gang)
        }
    }

    /**
     * Returns a set of [Gang]s that the given [playerUuid] owns.
     */
    fun getOwnedGangs(playerUuid: UUID): Set<Gang> {
        return gangAccess.getOrDefault(playerUuid, hashSetOf()).filter { it.owner == playerUuid }.toSet()
    }

    /**
     * Returns the assumed [Gang] for the given [playerUuid].
     */
    fun getAssumedGang(playerUuid: UUID): Gang? {
        return getAccessibleGangs(playerUuid).firstOrNull()
    }

    fun fetchPreviousGang(uuid: UUID): Gang? {
        try {
            val redisValue = Cubed.instance.redis.runRedisCommand { redis -> redis.get("Gangs:LastGang:player.$uuid") }

            if (redisValue == null || redisValue.isBlank()) {
                return null
            }

            val gangId = UUID.fromString(redisValue)

            for (gang in getAccessibleGangs(uuid)) {
                if (gang.uuid == gangId) {
                    return gang
                }
            }

            return null
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

    fun hasBypass(player: Player): Boolean {
        return player.hasPermission(Permissions.GANGS_ADMIN) && player.gameMode == GameMode.CREATIVE && RegionBypass.hasBypass(player)
    }

    fun attemptJoinSession(player: Player, gang: Gang) {
        if (hasBypass(player)) {
            RegionBypass.attemptNotify(player)
            successfulJoinSession(player, gang)
        } else {
            if (gang.owner != player.uniqueId) {
                if (gang.getActivePlayers().size >= 50) {
                    player.sendMessage("${ChatColor.RED}There are too many players visiting that gang right now to join.")
                    return
                }
            }

            if (!gang.testPermission(player, GangPermission.ALLOW_VISITORS)) {
                return
            }

            successfulJoinSession(player, gang)
        }
    }

    private fun successfulJoinSession(player: Player, gang: Gang) {
        updateVisitingGang(player, gang)

        player.teleport(gang.homeLocation)
        player.allowFlight = true
        player.isFlying = true

        gang.joinSession(player)
        gang.sendBorderUpdate(player)
    }

    private fun synchronizeCaches(gang: Gang) {
        grid[gang.gridIndex] = gang
        gangsByName[gang.name.toLowerCase()] = gang

        for (player in gang.getMembers()) {
            gangAccess.putIfAbsent(player, HashSet())
            gangAccess[player]!!.add(gang)
        }
    }

    fun forgetGang(gang: Gang) {
        grid.remove(gang.gridIndex)
        gangsByName.remove(gang.name.toLowerCase())
    }

    fun renameGang(gang: Gang, name: String) {
        gangsByName.remove(gang.name.toLowerCase())
        gang.name = name
        synchronizeCaches(gang)
    }

    /**
     * Creates and inserts a [Gang] into the grid.
     *
     * This method should always be called asynchronously.
     */
    @Throws(IllegalStateException::class)
    fun createNewGang(owner: UUID, name: String, onFinish: (Gang) -> Unit) {
        assert(!Bukkit.isPrimaryThread()) { "Cannot generate new gang on primary thread" }

        val schematicFile = GangModule.getCellSchematicFile()
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

            if (scanResults.playerSpawnLocation == null) {
                throw IllegalStateException("Missing player spawn location")
            }

            if (scanResults.jerrySpawnLocation == null) {
                throw IllegalStateException("Missing jerry spawn location")
            }

            Tasks.sync {
                for (block in scanResults.blocks) {
                    block.type = Material.AIR
                    block.state.update()
                }
            }

            val gang = Gang(gridIndex, name, owner, scanResults.playerSpawnLocation!!, scanResults.jerrySpawnLocation!!, schematicData.cuboid)
            gang.initializeData()

            RegionsModule.updateBlockCache(gang)

            synchronizeCaches(gang)
            saveData()

            onFinish.invoke(gang)
        }
    }

    /**
     * Creates and inserts a [Gang] into the grid.
     *
     * This method should always be called asynchronously.
     */
    @Throws(IllegalStateException::class)
    fun resetGang(gang: Gang, onFinish: () -> Unit) {
        assert(!Bukkit.isPrimaryThread()) { "Cannot reset cell on primary thread" }

        val schematicFile = GangModule.getCellSchematicFile()
        if (!schematicFile.exists()) {
            throw IllegalStateException("Schematic file doesn't exist: ${schematicFile.name}")
        }

        val schematicData = GridSchematicData.of(gang.gridIndex, schematicFile)

        pasteSchematic(schematicData) { success ->
            if (!success) {
                throw IllegalStateException("Failed to paste schematic")
            }

            val scanResults = startSchematicScan(schematicData.pasteLocation)

            if (scanResults.playerSpawnLocation == null) {
                throw IllegalStateException("Missing player spawn location")
            }

            if (scanResults.jerrySpawnLocation == null) {
                throw IllegalStateException("Missing jerry spawn location")
            }

            Tasks.sync {
                for (block in scanResults.blocks) {
                    block.type = Material.AIR
                    block.state.update()
                }
            }

            gang.homeLocation = scanResults.playerSpawnLocation!!
            gang.guideNpc.updateLocation(scanResults.jerrySpawnLocation!!)
            gang.cuboid = schematicData.cuboid

            onFinish.invoke()
        }
    }

    @Throws(IllegalStateException::class)
    fun pasteSchematic(schematicData: GridSchematicData, onFinish: (Boolean) -> Unit) {
        val schematicFile = GangModule.getCellSchematicFile()
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
        var playerSpawnLocation: Location? = null,
        var jerrySpawnLocation: Location? = null,
        var blocks: HashSet<Block> = hashSetOf()
    )

    /**
     * Finds the spawn points for a given location.
     */
    private fun startSchematicScan(start: Location): SchematicScanResults {
        val schematicSize = WorldEditUtils.readSchematicSize(GangModule.getCellSchematicFile())

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
                                spawnPointScan.playerSpawnLocation = block.location.add(0.5, 2.0, 0.5)
                                spawnPointScan.playerSpawnLocation!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
                            }
                            SkullType.CREEPER -> {
                                spawnPointScan.jerrySpawnLocation = block.location.add(0.5, 0.0, 0.5)
                                spawnPointScan.jerrySpawnLocation!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
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

    /**
     * Loads the grid data from the file system.
     */
    private fun loadGrid() {
        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val gridType = object : TypeToken<List<Gang>>() {}.type
                val gridList = Cubed.gson.fromJson(reader, gridType) as List<Gang>

                for (gang in gridList) {
                    // initialize transient data
                    gang.initializeData()

                    // synchronize this gang data into our caches
                    synchronizeCaches(gang)

                    // update region block cache
                    RegionsModule.updateBlockCache(gang)

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

    private fun indexToGrid(index: Int): Pair<Int, Int> {
        if (index == 0) {
            return Pair(0, 0)
        }

        val x = index % GangModule.getGridColumns()
        val y = index / GangModule.getGridColumns()

        return Pair(x, y)
    }

    private fun gridToIndex(x: Int, y: Int): Int {
        return x + (GangModule.getGridColumns() * y)
    }

    private fun gridCoordsToBlockCoords(pos: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(pos.first * GangModule.getGridGutterWidth(), pos.second * GangModule.getGridGutterWidth())
    }

    internal val BLOCKED_NAMES = listOf(
        "f+[a4]+g+[o0]+t+".toRegex(),
        "n+[i1l|]+gg+[e3]+r+".toRegex(),
        "b+[e3]+[a4]+n+[e3]+r+".toRegex()
    )

}