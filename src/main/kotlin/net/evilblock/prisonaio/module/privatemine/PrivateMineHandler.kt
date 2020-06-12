package net.evilblock.prisonaio.module.privatemine

import com.boydti.fawe.util.TaskManager
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.bukkit.generator.EmptyChunkGenerator
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.privatemine.data.PrivateMineTier
import net.evilblock.prisonaio.module.privatemine.entity.PrivateMineNpcEntity
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
object PrivateMineHandler : PluginHandler {

    override fun getModule(): PluginModule {
        return PrivateMinesModule
    }

    private val tiers = hashMapOf<Int, PrivateMineTier>()
    private var gridIndex = 0
    private val grid: HashMap<Int, PrivateMine> = HashMap()
    private val mineAccess: HashMap<UUID, HashSet<PrivateMine>> = HashMap()
    private val currentlyAt: HashMap<UUID, PrivateMine> = HashMap()

    override fun initialLoad() {
        loadConfig()
        loadWorld()
        loadGrid()
    }

    override fun saveData() {
        saveGrid()
    }

    fun loadConfig() {
        for (tierMap in PrivateMinesModule.config.getList("tiers") as List<Map<String, Any>>) {
            val tier = PrivateMineTier.fromMap(tierMap)

            if (!tier.schematicFile.exists()) {
                PrisonAIO.instance.logger.severe("Couldn't find schematic file for tier ${tier.number}")
            }

            tiers[tier.number] = tier
        }
    }

    fun getTierByNumber(tier: Int): PrivateMineTier? {
        return tiers[tier]
    }

    private fun loadWorld() {
        WorldCreator(PrivateMinesModule.getGridWorldName())
                .generator(EmptyChunkGenerator())
                .generateStructures(false)
                .createWorld()
    }

    /**
     * Gets the [World] that hosts the mine grid.
     */
    fun getGridWorld(): World {
        return Bukkit.getWorld(PrivateMinesModule.getGridWorldName())
    }

    /**
     * Gets all of the [PrivateMine]s in the grid.
     */
    fun getAllMines(): Set<PrivateMine> {
        return HashSet(grid.values)
    }

    /**
     * Gets the [PrivateMine] that the given [location] belongs to.
     */
    fun getMineByLocation(location: Location): Optional<PrivateMine> {
        for (mine in grid.values) {
            if (mine.cuboid.contains(location)) {
                return Optional.of(mine)
            }
        }
        return Optional.empty()
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
            val tier = split[1].toInt()

            for (mine in getOwnedMines(owner)) {
                if (mine.tier.number == tier) {
                    return mine
                }
            }

            return null
        } catch (e: Exception) {
            return null
        }
    }

    fun setPreviousMine(uuid: UUID, mine: PrivateMine?) {
        Cubed.instance.redis.runRedisCommand { redis ->
            if (mine == null) {
                redis.del("PrivateMines:LastMine.$uuid")
            } else {
                redis.set("PrivateMines:LastMine.$uuid", "${mine.owner},${mine.tier.number}")
            }
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
        if (mine.getActivePlayers().size >= mine.tier.playerLimit) {
            player.sendMessage("${ChatColor.RED}That mine is full of players! Try again later. (Max slots: (${mine.tier.playerLimit} players)")
            return
        }

        updateCurrentMine(player, mine)
    }

    private fun initialMineSetup(mine: PrivateMine) {
        mine.activePlayers = hashSetOf()
        grid[mine.gridIndex] = mine

        for (player in mine.whitelistedPlayers) {
            mineAccess.putIfAbsent(player, HashSet())
            mineAccess[player]!!.add(mine)
        }

        mine.resetRegion()
    }

    /**
     * Creates and inserts a [PrivateMine] into the [PrivateMineHandler].
     */
    @Throws(IllegalStateException::class)
    fun createMine(owner: UUID, tier: PrivateMineTier) {
        if (!tier.schematicFile.exists()) {
            throw IllegalStateException("Tier schematic file doesn't exist: ${tier.schematicFile.name}")
        }

        mineAccess.putIfAbsent(owner, hashSetOf())

        // throw exception if player already owns a mine with the same tier
        for (mine in mineAccess[owner]!!) {
            if (mine.tier == tier && mine.owner == owner) {
                throw IllegalStateException("Player owns mine tier ${tier.number} already!")
            }
        }

        val gridIndex = ++gridIndex
        val blockCoords = gridCoordsToBlockCoords(indexToGrid(gridIndex))
        val pasteLocation = Location(getGridWorld(), blockCoords.first.toDouble(), 68.0, blockCoords.second.toDouble())

        // paste schematic
        WorldEditUtils.paste(tier.schematicFile, pasteLocation)
        println("Pasted schematic at vector ${pasteLocation.x}, ${pasteLocation.z}")

        // find the breakable region of the pasted schematic
        val cubeStart = Location(getGridWorld(), blockCoords.first.toDouble(), 68.0, blockCoords.second.toDouble())

        Tasks.sync {
            // find the spawn points for player and npcs
            val dataScan = scanSchematic(cubeStart, tier)
            if (!dataScan.isComplete()) {
                throw IllegalStateException("Tier ${tier.number} schematic is missing a scanned data field")
            }

            val schematicSize = WorldEditUtils.readSchematicSize(tier.schematicFile)
            val cuboid = Cuboid(pasteLocation, pasteLocation.clone().add(schematicSize.x, schematicSize.y, schematicSize.z))

            Tasks.async {
                val mine = PrivateMine(gridIndex, owner, tier, dataScan.playerSpawnPoint!!, cuboid, Cuboid(dataScan.cuboidLower!!, dataScan.cuboidUpper!!))

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
    private fun scanSchematic(start: Location, tier: PrivateMineTier): SchematicDataScan {
        val schematicSize = WorldEditUtils.readSchematicSize(tier.schematicFile)

        val minPoint = start.clone()
        val maxPoint = start.clone().add(schematicSize.x, schematicSize.y, schematicSize.z)

        val gridWorld = getGridWorld()
        val dataScan = SchematicDataScan()

        for (x in minPoint.x.toInt()..maxPoint.x.toInt()) {
            for (y in minPoint.y.toInt()..maxPoint.y.toInt()) {
                zLoop@ for (z in minPoint.z.toInt()..maxPoint.z.toInt()) {
                    val block = gridWorld.getBlockAt(x, y, z)

                    if (block.state is Skull) {
                        val skull = block.state as Skull

                        when (skull.skullType) {
                            SkullType.PLAYER -> {
                                dataScan.playerSpawnPoint = block.location.add(0.5, 2.0, 0.5)
                                dataScan.playerSpawnPoint!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
                            }
                            SkullType.CREEPER -> {
                                dataScan.npcSpawnPoint = block.location.add(0.5, 0.0, 0.5)
                                dataScan.npcSpawnPoint!!.yaw = AngleUtils.faceToYaw(skull.rotation) + 90F
                            }
                            else -> {
                                continue@zLoop
                            }
                        }

                        // remove sign - run sync
                        TaskManager.IMP.sync {
                            block.type = Material.AIR
                            block.state.update()
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

        val x = index % PrivateMinesModule.getGridDimensions()
        val y = index / PrivateMinesModule.getGridDimensions()

        return Pair(x, y)
    }

    private fun gridToIndex(x: Int, y: Int): Int {
        return PrivateMinesModule.getGridDimensions() * y + x
    }

    private fun gridCoordsToBlockCoords(pos: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(
                pos.first * PrivateMinesModule.getGridGutter(),
                pos.second * PrivateMinesModule.getGridGutter()
        )
    }

}