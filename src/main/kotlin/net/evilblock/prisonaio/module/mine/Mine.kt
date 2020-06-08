package net.evilblock.prisonaio.module.mine

import com.boydti.fawe.FaweAPI
import com.boydti.fawe.util.EditSessionBuilder
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.blocks.BaseBlock
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.module.mine.block.BlockType
import net.evilblock.prisonaio.module.mine.config.MineBlocksConfig
import net.evilblock.prisonaio.module.mine.config.MineEffectsConfig
import net.evilblock.prisonaio.module.mine.config.MineResetConfig
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class Mine(val id: String) : Region {

    /**
     * The location players spawn at when teleporting to this mine
     */
    @Nullable
    var spawnPoint: Location? = null

    /**
     * The region that forms the mining area
     */
    @Nullable
    var region: Cuboid? = null

    /**
     * This mine's blocks configuration
     */
    @NotNull
    val blocksConfig: MineBlocksConfig = MineBlocksConfig()

    /**
     * This mine's reset configuration
     */
    @NotNull
    val resetConfig: MineResetConfig = MineResetConfig()

    /**
     * This mine's effects configuration
     */
    @NotNull
    val effectsConfig: MineEffectsConfig = MineEffectsConfig()

    @Transient
    var nextReset: Int = 0

    @Transient
    var blocksRemaining: Int = 0

    /**
     * The permission required to break inside this mine
     */
    var permission: String? = null

    /**
     * The name of this mining region
     */
    override fun getRegionName(): String {
        return "Mine $id"
    }

    override fun getBreakableRegion(): Cuboid? {
        return region
    }

    override fun resetBreakableRegion() {
        Tasks.async {
            resetRegion()
        }
    }

    fun getRegionSize(): Int {
        if (region == null) {
            return 0
        }

        val region = region!!
        return (region.upperX - region.lowerX) * (region.upperY - region.lowerY) * (region.upperZ - region.lowerZ)
    }

    fun getRemainingPercentage(): Double {
        return (blocksRemaining.toDouble() / getRegionSize().toDouble()) * 100.0
    }

    override fun onBlockPlace(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        // allow block break if the player is breaking a mine
        if (permission != null && !player.hasPermission(permission)) {
            player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}No access! ${ChatColor.RED}You lack the permission ${ChatColor.WHITE}${permission} ${ChatColor.RED}to mine here.")
            cancellable.isCancelled = true
            return
        }
    }

    override fun supportsEnchants(): Boolean {
        // TODO: do something better later
        if (id.contains("beacon", ignoreCase = true)) {
            return false
        }

        return true
    }

    override fun supportsRewards(): Boolean {
        // TODO: do something better later
        if (id.contains("beacon", ignoreCase = true)) {
            return false
        }

        return true
    }

    /**
     * Resets this mine's region
     */
    @Throws(IllegalStateException::class)
    fun resetRegion() {
        if (region == null) {
            throw IllegalStateException("Cannot reset mine if its region is not set")
        }

        if (blocksConfig.blockTypes.isEmpty()) {
            throw IllegalStateException("Cannot reset mine if the blocks config contains no block types")
        }

        nextReset = resetConfig.resetInterval

        val blockList = arrayListOf<BlockType>()
        for (i in 0 until (region!!.sizeX * region!!.sizeY * region!!.sizeZ)) {
            blockList.add(blocksConfig.pickRandomBlockType())
        }

        // update the blocks synchronously
        val wrappedWorld = FaweAPI.getWorld(region!!.world.name)
        val editSession = EditSessionBuilder(wrappedWorld).fastmode(true).build()

        region!!.getBlockLocations().forEachIndexed { index, location ->
            if (RewardsModule.isEnabled()) {
                if (MineCrateHandler.isAttached(location)) {
                    return@forEachIndexed
                }
            }

            val blockType = blockList[index]
            editSession.setBlock(Vector(location.x, location.y, location.z), BaseBlock(blockType.material.id, blockType.data.toInt()))
        }

        editSession.flushQueue()

        blocksRemaining = getRegionSize()
    }

    /**
     * Gets the players that are nearby this mine
     */
    fun getNearbyPlayers(): List<Player> {
        // players cannot be nearby a mine that has no region
        if (region == null) {
            return emptyList()
        }

        return Bukkit.getOnlinePlayers().filter { player -> isNearbyMine(player) }.toList()
    }

    /**
     * If a player is nearby this mine
     */
    fun isNearbyMine(player: Player): Boolean {
        if (region == null) {
            throw IllegalStateException("Cannot check if player is nearby if no region has been set for this mine")
        }

        // the radius added to the region
        val nearbyRadius = MinesModule.getNearbyRadius()

        // save processing power here by directly checking if the region contains the player's location
        if (nearbyRadius <= 0) {
            if (region!!.contains(player.location)) {
                return true
            }
        }

        // clone the region and grow by the radius, including up and down
        val expandedRegion = region!!.clone()
            .grow(nearbyRadius)
            .expand(Cuboid.CuboidDirection.UP, nearbyRadius)
            .expand(Cuboid.CuboidDirection.DOWN, nearbyRadius)

        // check if expanded region contains the player's location
        if (expandedRegion.contains(player.location)) {
            return true
        }

        // the player is not nearby
        return false
    }

}