/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal

import com.boydti.fawe.util.TaskManager
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import com.sk89q.worldedit.Vector
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.bukkit.generator.EmptyChunkGenerator
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.mine.MinesModule
import net.evilblock.prisonaio.module.mine.variant.personal.entity.PrivateMineNpcEntity
import net.evilblock.prisonaio.module.region.RegionHandler
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
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
object PrivateMineHandler : PluginHandler() {

    private val schematicFile: File = File(File(Bukkit.getPluginManager().getPlugin("WorldEdit").dataFolder, "schematics"), "PrivateMine.schematic")

    private var gridIndex = 0
    private val grid: HashMap<Int, PrivateMine> = HashMap()
    private val mineAccess: HashMap<UUID, HashSet<PrivateMine>> = HashMap()
    private val currentlyAt: HashMap<UUID, PrivateMine> = HashMap()

    override fun getModule(): MinesModule {
        return MinesModule
    }

    override fun initialLoad() {
        PrivateMineConfig.load()

        loadWorld()
        loadGrid()
    }

    override fun saveData() {
        super.saveData()

        saveGrid()
    }

    private fun loadWorld() {
        WorldCreator(getGridWorldName())
                .generator(EmptyChunkGenerator())
                .generateStructures(false)
                .createWorld()
    }

    /**
     * Gets the [World] that hosts the mine grid.
     */
    fun getGridWorld(): World {
        return Bukkit.getWorld(getGridWorldName())
    }

    /**
     * Gets all of the [PrivateMine]s in the grid.
     */
    fun getAllMines(): Set<PrivateMine> {
        return HashSet(grid.values)
    }

    /**
     * Gets the [PrivateMine] a player is currently at, or null.
     */
    fun getCurrentMine(player: Player): PrivateMine? {
        return currentlyAt[player.uniqueId]
    }

    /**
     * Updates the [PrivateMine] a player is currently at.
     */
    fun updateCurrentMine(player: Player, mine: PrivateMine?) {
        getCurrentMine(player)?.removeFromActivePlayers(player)

        if (mine == null) {
            currentlyAt.remove(player.uniqueId)
        } else {
            mine.addToActivePlayers(player)
            currentlyAt[player.uniqueId] = mine
        }

        setPreviousMine(player.uniqueId, mine)
    }

    /**
     * Returns a set of [PrivateMine] that the player owns.
     */
    fun getOwnedMines(owner: UUID): Set<PrivateMine> {
        return mineAccess.getOrDefault(owner, hashSetOf()).filter { it.owner == owner }.toSet()
    }

    /**
     * Returns a set of [PrivateMine] that the player has access to.
     */
    fun getAccessibleMines(player: UUID): Set<PrivateMine> {
        return mineAccess.getOrDefault(player, hashSetOf()).sortedBy {
            return@sortedBy if (it.owner == player) {
                1
            } else {
                0
            }
        }.toSet()
    }

    fun addAccessToMine(uuid: UUID, mine: PrivateMine) {
        mineAccess.putIfAbsent(uuid, hashSetOf())
        mineAccess[uuid]!!.add(mine)
    }

    fun removeAccessToMine(uuid: UUID, mine: PrivateMine) {
        mineAccess.getOrDefault(uuid, HashSet()).remove(mine)
    }

    /**
     * Returns a set of [PrivateMine] that are publicly available.
     */
    fun getPublicMines(): Set<PrivateMine> {
        return grid.values.filter { mine -> mine.public }.toSet()
    }

    fun fetchPreviousMine(uuid: UUID): PrivateMine? {
        try {
            val redisValue = Cubed.instance.redis.runRedisCommand { redis -> redis.get("PrivateMines:LastMine.$uuid") }

            if (redisValue == null || redisValue.isBlank()) {
                return null
            }

            val split = redisValue.split(",")
            val owner = UUID.fromString(split[0])

            return getOwnedMines(owner).firstOrNull()
        } catch (e: Exception) {
            return null
        }
    }

    fun setPreviousMine(uuid: UUID, mine: PrivateMine?) {
        Cubed.instance.redis.runRedisCommand { redis ->
            if (mine == null) {
                redis.del("PrivateMines:LastMine.$uuid")
            } else {
                redis.set("PrivateMines:LastMine.$uuid", mine.owner.toString())
            }

            null
        }
    }

    fun attemptJoinMine(mine: PrivateMine, player: Player) {
        // let the owner of the mine always join their mine
        if (mine.owner == player.uniqueId) {
            updateCurrentMine(player, mine)
            return
        }

        if (!mine.public && !mine.getWhitelistedPlayers().contains(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}That mine is currently private and you are not whitelisted.")
            return
        }

        // only allow the player to join if the active player amount is less than the tier player limit
        if (mine.getActivePlayers().size >= PrivateMineConfig.playerLimit) {
            player.sendMessage("${ChatColor.RED}That mine is full of players! Try again later. (Max slots: (${PrivateMineConfig.playerLimit} players)")
            return
        }

        updateCurrentMine(player, mine)
    }

    private fun initialMineSetup(mine: PrivateMine) {
        mine.activePlayers = hashSetOf()
        mine.salesTax = mine.salesTax.coerceAtLeast(1.0)

        grid[mine.gridIndex] = mine

        for (player in mine.whitelistedPlayers) {
            mineAccess.putIfAbsent(player, HashSet())
            mineAccess[player]!!.add(mine)
        }

        mine.resetRegion()

        RegionHandler.updateBlockCache(mine)
    }

    /**
     * Creates and inserts a [PrivateMine] into the [PrivateMineHandler].
     */
    @Throws(IllegalStateException::class)
    fun createMine(owner: UUID) {
        if (!schematicFile.exists()) {
            throw IllegalStateException("Private Mines schematic file doesn't exist: ${schematicFile.name}")
        }

        mineAccess.putIfAbsent(owner, hashSetOf())

        // throw exception if player already owns a private mine
        for (mine in mineAccess[owner]!!) {
            if (mine.owner == owner) {
                throw IllegalStateException("Player already owns a Private Mine")
            }
        }

        val gridIndex = ++gridIndex
        val blockCoords = gridCoordsToBlockCoords(indexToGrid(gridIndex))
        val pasteLocation = Location(getGridWorld(), blockCoords.first.toDouble(), 68.0, blockCoords.second.toDouble())

        // paste schematic
        val pasteVector = Vector(pasteLocation.x, pasteLocation.y, pasteLocation.z)
        WorldEditUtils.paste(schematicFile, pasteLocation.world, pasteVector, true)
        println("Pasted schematic at vector ${pasteLocation.x}, ${pasteLocation.y}, ${pasteLocation.z}")

        // find the breakable region of the pasted schematic
        val cubeStart = Location(getGridWorld(), blockCoords.first.toDouble(), 68.0, blockCoords.second.toDouble())

        Tasks.sync {
            // find the spawn points for player and npcs
            val dataScan = scanSchematic(cubeStart)
            if (!dataScan.isComplete()) {
                throw IllegalStateException("Schematic is missing a scanned data field")
            }

            val schematicSize = WorldEditUtils.readSchematicSize(schematicFile)
            val cuboid = Cuboid(pasteLocation, pasteLocation.clone().add(schematicSize.x, schematicSize.y, schematicSize.z))

            Tasks.async {
                val mine = PrivateMine(gridIndex, owner, dataScan.playerSpawnPoint!!, cuboid, Cuboid(dataScan.cuboidLower!!, dataScan.cuboidUpper!!))

                // execute initial mine setup
                initialMineSetup(mine)

                // save the world and grid to prevent data loss
                getGridWorld().save()
                saveGrid()

                // spawn npc
                val npc = PrivateMineNpcEntity(dataScan.npcSpawnPoint!!)
                npc.initializeData()

                EntityManager.trackEntity(npc)
            }
        }
    }

    /**
     * Container for storing spawn points.
     */
    data class SchematicDataScan(
        var playerSpawnPoint: Location? = null,
        var npcSpawnPoint: Location? = null,
        var cuboidLower: Location? = null,
        var cuboidUpper: Location? = null
    ) {
        fun isComplete(): Boolean {
            return playerSpawnPoint != null && npcSpawnPoint != null && cuboidLower != null && cuboidLower != null
        }
    }

    /**
     * Finds the spawn point for the NPC by searching for a SIGN tile entity.
     */
    private fun scanSchematic(start: Location): SchematicDataScan {
        val schematicSize = WorldEditUtils.readSchematicSize(schematicFile)

        val minPoint = start.clone()
        val maxPoint = start.clone().add(schematicSize.x, schematicSize.y, schematicSize.z)

        val gridWorld = getGridWorld()
        val dataScan = SchematicDataScan()

        for (x in minPoint.x.toInt()..maxPoint.x.toInt()) {
            for (y in minPoint.y.toInt()..maxPoint.y.toInt()) {
                zLoop@ for (z in minPoint.z.toInt()..maxPoint.z.toInt()) {
                    val block = gridWorld.getBlockAt(x, y, z)

                    if (block.state is Skull) {
                        val below = block.getRelative(BlockFace.DOWN)
                        val skull = block.state as Skull

                        when (skull.skullType) {
                            SkullType.PLAYER -> {
                                dataScan.playerSpawnPoint = block.location.add(0.5, 2.0, 0.5)
                                dataScan.playerSpawnPoint!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
                            }
                            SkullType.CREEPER -> {
                                dataScan.npcSpawnPoint = block.location.add(0.5, 0.0, 0.5)
                                dataScan.npcSpawnPoint!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F

                                if (below.type == Material.FENCE) {
                                    dataScan.npcSpawnPoint = dataScan.npcSpawnPoint!!.subtract(0.0, 1.0, 0.0)
                                }
                            }
                            else -> {
                                continue@zLoop
                            }
                        }

                        TaskManager.IMP.sync {
                            block.type = Material.AIR
                            block.state.update()

                            if (below.type == Material.FENCE) {
                                below.type = Material.AIR
                                below.state.update()
                            }
                        }
                    } else if (block.state is Sign) {
                        val sign = block.state as Sign

                        if (sign.getLine(0) == "CUBE-FINDER") {
                            when (sign.getLine(1)) {
                                "LOWER" -> dataScan.cuboidLower = sign.location
                                "UPPER" -> dataScan.cuboidUpper = sign.location
                            }

                            // remove cube-finder blocks
                            TaskManager.IMP.sync {
                                block.type = Material.AIR
                                block.state.update()

                                val below = block.getRelative(BlockFace.DOWN)
                                if (below.type == Material.FENCE) {
                                    below.type = Material.AIR
                                    below.state.update()
                                }
                            }
                        }
                    }
                }
            }
        }

        return dataScan
    }

    /**
     * Loads the grid data from the file system.
     */
    private fun loadGrid() {
        val worldFolder = getGridWorld().worldFolder
        val memoryFile = File(worldFolder, "privateMines.json")

        if (memoryFile.exists()) {
            Files.newReader(memoryFile, Charsets.UTF_8).use { reader ->
                val gridType = object : TypeToken<List<PrivateMine>>() {}.type
                val gridList = Cubed.gson.fromJson(reader, gridType) as List<PrivateMine>

                for (mine in gridList) {
                    initialMineSetup(mine)

                    // set highest grid index so we know where to create the next mine
                    if (mine.gridIndex > gridIndex) {
                        gridIndex = mine.gridIndex
                    }
                }
            }
        }
    }

    /**
     * Saves the grid data to the file system.
     */
    fun saveGrid() {
        val worldFolder = getGridWorld().worldFolder
        val memoryFile = File(worldFolder, "privateMines.json")

        Files.write(Cubed.gson.toJson(grid.values), memoryFile, Charsets.UTF_8)
    }

    private fun indexToGrid(index: Int): Pair<Int, Int> {
        if (index == 0) {
            return Pair(0, 0)
        }

        val x = index % getGridDimensions()
        val y = index / getGridDimensions()

        return Pair(x, y)
    }

    private fun gridToIndex(x: Int, y: Int): Int {
        return getGridDimensions() * y + x
    }

    private fun gridCoordsToBlockCoords(pos: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(
                pos.first * getGridGutter(),
                pos.second * getGridGutter()
        )
    }

    @JvmStatic
    fun getGridWorldName(): String {
        return MinesModule.config.getString("personal-mines.grid.world-name")
    }

    @JvmStatic
    fun getGridGutter(): Int {
        return MinesModule.config.getInt("personal-mines.grid.gutter")
    }

    @JvmStatic
    fun getGridDimensions(): Int {
        return MinesModule.config.getInt("personal-mines.grid.dimensions")
    }

    @JvmStatic
    fun getNotificationLines(type: String): List<String> {
        return MinesModule.config.getStringList("personal-mines.language.notifications.$type").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

}