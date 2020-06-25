/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell

import com.boydti.fawe.bukkit.wrapper.AsyncWorld
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import com.sk89q.worldedit.Vector
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.bukkit.generator.EmptyChunkGenerator
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.cell.permission.CellPermission
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
object CellHandler : PluginHandler {

    private var gridIndex = 0
    private val grid: HashMap<Int, Cell> = hashMapOf()
    private val nameToCell: HashMap<String, Cell> = hashMapOf()

    /**
     * The `joinable cell` cache.
     *
     * For quick lookup, we cache what cells a player can join.
     */
    private val joinableCache: HashMap<UUID, HashSet<Cell>> = hashMapOf()

    /**
     * The `visiting cell` cache.
     *
     * For quick lookup, we cache what cell a player is currently visiting.
     */
    private val visitingCache: HashMap<UUID, Cell> = hashMapOf()

    /**
     * The cached asynchronous grid world.
     */
    var asyncWorld: AsyncWorld? = null

    override fun getModule(): PluginModule {
        return CellsModule
    }

    override fun getInternalDataFile(): File {
        val worldFolder = getGridWorld().worldFolder
        return File(worldFolder, "cells.json")
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
            for (cell in grid.values) {
                cell.updateCachedCellValue()
            }
        }
    }

    private fun loadWorld() {
        WorldCreator(CellsModule.getGridWorldName())
                .generator(EmptyChunkGenerator())
                .generateStructures(false)
                .createWorld()

        asyncWorld = getAsyncGridWorld()
    }

    /**
     * Gets the [World] that hosts the grid.
     */
    fun getGridWorld(): World {
        return Bukkit.getWorld(CellsModule.getGridWorldName())
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
     * Gets all of the [Cell]s in the grid.
     */
    fun getAllCells(): Set<Cell> {
        return HashSet(grid.values)
    }

    /**
     * Gets the [Cell] with the given [name].
     */
    fun getCellByName(name: String): Cell? {
        return nameToCell[name.toLowerCase()]
    }

    /**
     * Gets the [Cell] with the given [uuid].
     */
    fun getCellByUuid(uuid: UUID): Cell? {
        for (cell in getAllCells()) {
            if (cell.uuid == uuid) {
                return cell
            }
        }
        return null
    }

    /**
     * Gets the [Cell] that the given [location] belongs to.
     */
    fun getCellByLocation(location: Location): Cell? {
        for (cell in getAllCells()) {
            if (cell.cuboid.contains(location)) {
                return cell
            }
        }
        return null
    }

    /**
     * Gets the [Cell] a player is currently at.
     */
    fun getVisitingCell(player: Player): Cell? {
        return visitingCache[player.uniqueId]
    }

    /**
     * Updates the `visiting cell` cache to the given [Cell] for the given [player].
     */
    fun updateVisitingCell(player: Player, cell: Cell?) {
        if (cell == null) {
            visitingCache.remove(player.uniqueId)
        } else {
            visitingCache[player.uniqueId] = cell
        }
    }

    /**
     * Returns a set of [Cell]s that the given [player] has been invited to.
     */
    fun getCellsInvitedTo(player: Player): Set<Cell> {
        return getAllCells().filter { it.isInvited(player.uniqueId) }.toSet()
    }

    /**
     * Returns a set of [Cell]s that the given [playerUuid] can join.
     */
    fun getJoinableCells(playerUuid: UUID): Set<Cell> {
        return joinableCache.getOrDefault(playerUuid, hashSetOf()).sortedBy {
            return@sortedBy if (it.owner == playerUuid) {
                1
            } else {
                0
            }
        }.toSet()
    }

    fun updateJoinableCache(uuid: UUID, cell: Cell, joinable: Boolean) {
        joinableCache.putIfAbsent(uuid, HashSet())

        if (joinable) {
            joinableCache[uuid]!!.add(cell)
        } else {
            joinableCache[uuid]!!.remove(cell)
        }
    }

    /**
     * Returns a set of [Cell]s that the given [playerUuid] owns.
     */
    fun getOwnedCells(playerUuid: UUID): Set<Cell> {
        return joinableCache.getOrDefault(playerUuid, hashSetOf()).filter { it.owner == playerUuid }.toSet()
    }

    /**
     * Returns the assumed [Cell] for the given [playerUuid].
     */
    fun getAssumedCell(playerUuid: UUID): Cell? {
        val joinableCells = getJoinableCells(playerUuid)
        return if (joinableCells.size == 1) {
            joinableCells.first()
        } else {
            null
        }
    }

    fun fetchPreviousCell(uuid: UUID): Cell? {
        try {
            val redisValue = Cubed.instance.redis.runRedisCommand { redis -> redis.get("Cells:LastCell:player.$uuid") }

            if (redisValue == null || redisValue.isBlank()) {
                return null
            }

            val cellUuid = UUID.fromString(redisValue)

            for (cell in getJoinableCells(uuid)) {
                if (cell.uuid == cellUuid) {
                    return cell
                }
            }

            return null
        } catch (e: Exception) {
            return null
        }
    }

    fun updatePreviousCell(uuid: UUID, cell: Cell?) {
        Cubed.instance.redis.runRedisCommand<Unit> { redis ->
            if (cell == null) {
                redis.del("Cells:LastCell:player.$uuid")
            } else {
                redis.set("Cells:LastCell:player.$uuid", cell.uuid.toString())
            }
        }
    }

    fun hasBypass(player: Player): Boolean {
        return player.hasPermission(Permissions.CELLS_ADMIN) && player.gameMode == GameMode.CREATIVE && RegionBypass.hasBypass(player)
    }

    fun attemptJoinSession(player: Player, cell: Cell) {
        val hasBypass = hasBypass(player)
        if (hasBypass) {
            if (!RegionBypass.hasReceivedNotification(player)) {
                RegionBypass.sendNotification(player)
            }

            successfulJoinSession(player, cell)
        } else {
            if (cell.owner != player.uniqueId) {
                if (cell.getActivePlayers().size >= 50) {
                    player.sendMessage("${ChatColor.RED}There are too many players playing that cell right now to join.")
                    return
                }
            }

            if (!cell.testPermission(player, CellPermission.ALLOW_VISITORS)) {
                return
            }

            successfulJoinSession(player, cell)
        }
    }

    private fun successfulJoinSession(player: Player, cell: Cell) {
        updateVisitingCell(player, cell)

        player.teleport(cell.homeLocation)
        player.allowFlight = true
        player.isFlying = true

        cell.joinSession(player)
        cell.sendBorderUpdate(player)
    }

    private fun synchronizeCaches(cell: Cell) {
        grid[cell.gridIndex] = cell
        nameToCell[cell.name.toLowerCase()] = cell

        for (player in cell.getMembers()) {
            joinableCache.putIfAbsent(player, HashSet())
            joinableCache[player]!!.add(cell)
        }
    }

    fun forgetCell(cell: Cell) {
        grid.remove(cell.gridIndex)
        nameToCell.remove(cell.name.toLowerCase())
    }

    fun renameCell(cell: Cell, name: String) {
        nameToCell.remove(cell.name.toLowerCase())
        cell.name = name
        synchronizeCaches(cell)
    }

    /**
     * Creates and inserts a [Cell] into the grid.
     *
     * This method should always be called asynchronously.
     */
    @Throws(IllegalStateException::class)
    fun createNewCell(owner: UUID, name: String, onFinish: (Cell) -> Unit) {
        assert(!Bukkit.isPrimaryThread()) { "Cannot generate new cell on primary thread" }

        val schematicFile = CellsModule.getCellSchematicFile()
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

            val cell = Cell(gridIndex, name, owner, scanResults.playerSpawnLocation!!, scanResults.jerrySpawnLocation!!, schematicData.cuboid)
            cell.initializeData()

            RegionsModule.updateBlockCache(cell)

            synchronizeCaches(cell)
            saveData()

            onFinish.invoke(cell)
        }
    }

    /**
     * Creates and inserts a [Cell] into the grid.
     *
     * This method should always be called asynchronously.
     */
    @Throws(IllegalStateException::class)
    fun resetCell(cell: Cell, onFinish: () -> Unit) {
        assert(!Bukkit.isPrimaryThread()) { "Cannot reset cell on primary thread" }

        val schematicFile = CellsModule.getCellSchematicFile()
        if (!schematicFile.exists()) {
            throw IllegalStateException("Schematic file doesn't exist: ${schematicFile.name}")
        }

        val schematicData = GridSchematicData.of(cell.gridIndex, schematicFile)

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

            cell.homeLocation = scanResults.playerSpawnLocation!!
            cell.guideNpc.updateLocation(scanResults.jerrySpawnLocation!!)
            cell.cuboid = schematicData.cuboid

            onFinish.invoke()
        }
    }

    @Throws(IllegalStateException::class)
    fun pasteSchematic(schematicData: GridSchematicData, onFinish: (Boolean) -> Unit) {
        val schematicFile = CellsModule.getCellSchematicFile()
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
                    WorldEditUtils.paste(schematicFile, schematicData.pasteLocation)
                    println("Pasted schematic at vector ${schematicData.pasteLocation.x}, ${schematicData.pasteLocation.z}")

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
        val schematicSize = WorldEditUtils.readSchematicSize(CellsModule.getCellSchematicFile())

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
                val gridType = object : TypeToken<List<Cell>>() {}.type
                val gridList = Cubed.gson.fromJson(reader, gridType) as List<Cell>

                for (cell in gridList) {
                    // initialize transient data
                    cell.initializeData()

                    // synchronize this cell data into our caches
                    synchronizeCaches(cell)

                    // update region block cache
                    RegionsModule.updateBlockCache(cell)

                    // set highest grid index so we know where to create the next cell
                    if (cell.gridIndex > this.gridIndex) {
                        this.gridIndex = cell.gridIndex
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

        val x = index % CellsModule.getGridColumns()
        val y = index / CellsModule.getGridColumns()

        return Pair(x, y)
    }

    private fun gridToIndex(x: Int, y: Int): Int {
        return x + (CellsModule.getGridColumns() * y)
    }

    private fun gridCoordsToBlockCoords(pos: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(pos.first * CellsModule.getGridGutterWidth(), pos.second * CellsModule.getGridGutterWidth())
    }

    internal val BLOCKED_NAMES = listOf(
        "f+[a4]+g+[o0]+t+".toRegex(),
        "n+[i1l|]+gg+[e3]+r+".toRegex(),
        "b+[e3]+[a4]+n+[e3]+r+".toRegex()
    )

}