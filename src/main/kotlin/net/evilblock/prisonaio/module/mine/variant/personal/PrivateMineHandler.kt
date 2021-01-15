/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import com.sk89q.worldedit.Vector
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.backup.BackupHandler
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.bukkit.generator.EmptyChunkGenerator
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.mine.MinesModule
import net.evilblock.prisonaio.module.mine.variant.personal.schematic.PrivateMineSchematicData
import net.evilblock.prisonaio.module.mine.variant.personal.schematic.PrivateMineSchematicScanResults
import net.evilblock.prisonaio.module.region.RegionHandler
import org.bukkit.*
import org.bukkit.block.Skull
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.set

/**
 * A 2-dimensional grid filled in a linear sequence.
 */
object PrivateMineHandler : PluginHandler() {

    val schematicFile: File = File(File(Bukkit.getPluginManager().getPlugin("WorldEdit").dataFolder, "schematics"), "PrivateMine.schematic")

    private var gridIndex = 0
    private val grid: HashMap<Int, PrivateMine> = HashMap()

    private val mineByOwner: MutableMap<UUID, PrivateMine> = ConcurrentHashMap()

    private val playerAccess: MutableMap<UUID, HashSet<PrivateMine>> = ConcurrentHashMap()
    private val playerVisiting: MutableMap<UUID, PrivateMine> = ConcurrentHashMap()

    override fun getModule(): MinesModule {
        return MinesModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "private-mines.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        loadWorld()
        loadGrid()

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(grid.values), getInternalDataFile(), Charsets.UTF_8)
    }

    private fun loadWorld() {
        WorldCreator(getGridWorldName())
                .generator(EmptyChunkGenerator())
                .generateStructures(false)
                .createWorld()
    }

    private fun loadGrid() {
        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            val backupFile = BackupHandler.findNextBackupFile("private-mines")
            Files.copy(dataFile, backupFile)

            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val mines = Cubed.gson.fromJson(reader, object : TypeToken<List<PrivateMine>>() {}.type) as List<PrivateMine>
                for (mine in mines) {
                    mine.initializeData()
                    synchronizeCaches(mine)

                    // set highest grid index so we know where to create the next mine
                    if (mine.gridIndex > gridIndex) {
                        gridIndex = mine.gridIndex
                    }
                }
            }
        }

        getModule().getPluginFramework().logger.info("Loaded ${getAllMines().size} robots from mines.json!")
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
        return playerVisiting[player.uniqueId]
    }

    /**
     * Updates the [PrivateMine] a player is currently at.
     */
    fun updateCurrentMine(player: Player, mine: PrivateMine?) {
        getCurrentMine(player)?.removeFromActivePlayers(player)

        if (mine == null) {
            playerVisiting.remove(player.uniqueId)
        } else {
            mine.addToActivePlayers(player)
            playerVisiting[player.uniqueId] = mine
        }

        setPreviousMine(player.uniqueId, mine)
    }

    /**
     * Returns a set of [PrivateMine] that the player owns.
     */
    fun getOwnedMines(owner: UUID): Set<PrivateMine> {
        return playerAccess.getOrDefault(owner, hashSetOf()).filter { it.owner == owner }.toSet()
    }

    /**
     * Returns a set of [PrivateMine] that the player has access to.
     */
    fun getAccessibleMines(player: UUID): Set<PrivateMine> {
        return playerAccess.getOrDefault(player, hashSetOf()).sortedBy {
            return@sortedBy if (it.owner == player) {
                1
            } else {
                0
            }
        }.toSet()
    }

    fun addAccessToMine(uuid: UUID, mine: PrivateMine) {
        playerAccess.putIfAbsent(uuid, hashSetOf())
        playerAccess[uuid]!!.add(mine)
    }

    fun removeAccessToMine(uuid: UUID, mine: PrivateMine) {
        playerAccess.getOrDefault(uuid, HashSet()).remove(mine)
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

    fun synchronizeCaches(mine: PrivateMine) {
        grid[mine.gridIndex] = mine
        mineByOwner[mine.owner] = mine

        for (player in mine.whitelistedPlayers) {
            addAccessToMine(player, mine)
        }

        mine.resetRegion()
        RegionHandler.updateBlockCache(mine)
    }

    /**
     * Creates and inserts a [PrivateMine] into the grid.
     *
     * This method should always be called asynchronously.
     */
    @Throws(IllegalStateException::class)
    fun createMine(owner: UUID, onFinish: (PrivateMine) -> Unit) {
        assert(!Bukkit.isPrimaryThread()) { "Cannot generate new private mine on primary thread" }

        if (!schematicFile.exists()) {
            throw IllegalStateException("Schematic file doesn't exist: ${schematicFile.name}")
        }

        if (getOwnedMines(owner).isNotEmpty()) {
            throw IllegalStateException("Player already owns a private mine")
        }

        val gridIndex = ++gridIndex
        val schematicData = PrivateMineSchematicData.of(gridIndex)

        pasteSchematic(schematicData) { success ->
            if (!success) {
                throw IllegalStateException("Failed to paste schematic")
            }

            val scanResults = startSchematicScan(schematicData.pasteLocation)

            if (scanResults.spawnLocation == null) {
                throw IllegalStateException("Missing player spawn location")
            }

            if (scanResults.npcLocation == null) {
                throw IllegalStateException("Missing NPC spawn location")
            }

            if (scanResults.cuboidLower == null) {
                throw IllegalStateException("Missing cuboid lower location")
            }

            if (scanResults.cuboidUpper == null) {
                throw IllegalStateException("Missing cuboid upper location")
            }

            Tasks.sync {
                for (block in scanResults.blocks) {
                    block.type = Material.AIR
                    block.state.update()
                }
            }

            val mine = PrivateMine(
                gridIndex = gridIndex,
                owner = owner,
                spawnPoint = scanResults.spawnLocation!!,
                npcLocation = scanResults.npcLocation!!,
                cuboid = schematicData.cuboid,
                innerCuboid = Cuboid(scanResults.cuboidLower!!, scanResults.cuboidUpper!!)
            )

            mine.initializeData()

            synchronizeCaches(mine)

            onFinish.invoke(mine)
        }
    }

    @Throws(IllegalStateException::class)
    fun pasteSchematic(schematicData: PrivateMineSchematicData, onFinish: (Boolean) -> Unit) {
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
     * Finds the spawn points for a given location.
     */
    private fun startSchematicScan(start: Location): PrivateMineSchematicScanResults {
        val schematicSize = WorldEditUtils.readSchematicSize(schematicFile)

        val minPoint = start.clone()
        val maxPoint = start.clone().add(schematicSize.x, schematicSize.y, schematicSize.z)

        val world = getGridWorld()
        val results = PrivateMineSchematicScanResults()

        for (x in minPoint.x.toInt()..maxPoint.x.toInt()) {
            for (y in minPoint.y.toInt()..maxPoint.y.toInt()) {
                zLoop@ for (z in minPoint.z.toInt()..maxPoint.z.toInt()) {
                    val block = world.getBlockAt(x, y, z)
                    if (block.state is Skull) {
                        val skull = block.state as Skull
                        when (skull.skullType) {
                            SkullType.PLAYER -> {
                                results.spawnLocation = block.location.add(0.5, 2.0, 0.5)
                                results.spawnLocation!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
                            }
                            SkullType.CREEPER -> {
                                results.npcLocation = block.location.add(0.5, 0.0, 0.5)
                                results.npcLocation!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
                            }
                            SkullType.WITHER -> {
                                if (results.cuboidLower == null) {
                                    results.cuboidLower = block.location
                                } else {
                                    if (block.location.y > results.cuboidLower!!.y) {
                                        results.cuboidUpper = block.location
                                    } else {
                                        results.cuboidUpper = results.cuboidLower
                                        results.cuboidLower = block.location
                                    }
                                }
                            }
                            else -> continue@zLoop
                        }

                        results.blocks.add(block)
                    }
                }
            }
        }

        return results
    }

    fun indexToGrid(index: Int): Pair<Int, Int> {
        if (index == 0) {
            return Pair(0, 0)
        }

        val x = index % getGridDimensions()
        val y = index / getGridDimensions()

        return Pair(x, y)
    }

    fun gridToIndex(x: Int, y: Int): Int {
        return getGridDimensions() * y + x
    }

    fun gridCoordsToBlockCoords(pos: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(pos.first * getGridGutter(), pos.second * getGridGutter())
    }

    fun getGridWorldName(): String {
        return MinesModule.config.getString("personal-mines.grid.world-name")
    }

    fun getGridGutter(): Int {
        return MinesModule.config.getInt("personal-mines.grid.gutter")
    }

    fun getGridDimensions(): Int {
        return MinesModule.config.getInt("personal-mines.grid.dimensions")
    }

    fun getNotificationLines(type: String): List<String> {
        return MinesModule.config.getStringList("personal-mines.language.notifications.$type").map {
            ChatColor.translateAlternateColorCodes('&', it)
        }
    }

    fun getPrivateMineNPCHologramLines(): List<String> {
        return MinesModule.config.getStringList("personal-mines.mine.npc.hologram-lines").map {
            ChatColor.translateAlternateColorCodes('&', it)
        }
    }

    fun getPrivateMineNPCTextureValue(): String {
        return MinesModule.config.getString("personal-mines.mine.npc.texture-value")
    }

    fun getPrivateMineNPCTextureSignature(): String {
        return MinesModule.config.getString("personal-mines.mine.npc.texture-signature")
    }

}