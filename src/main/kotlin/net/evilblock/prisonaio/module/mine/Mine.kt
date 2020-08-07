/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine

import net.evilblock.cubed.lite.LiteEdit
import net.evilblock.cubed.lite.LiteRegion
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.mine.block.BlockType
import net.evilblock.prisonaio.module.mine.config.MineBlocksConfig
import net.evilblock.prisonaio.module.mine.config.MineEffectsConfig
import net.evilblock.prisonaio.module.mine.config.MineResetConfig
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.minecraft.server.v1_12_R1.IBlockData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class Mine(val id: String) : Region {

    @Nullable
    var spawnPoint: Location? = null

    @Nullable
    var region: Cuboid? = null

    @NotNull
    val blocksConfig: MineBlocksConfig = MineBlocksConfig()

    @NotNull
    val resetConfig: MineResetConfig = MineResetConfig()

    @NotNull
    val effectsConfig: MineEffectsConfig = MineEffectsConfig()

    var permission: String? = null

    @Transient
    var lastResetCheck: Long = System.currentTimeMillis()

    @Transient
    var cachedChunks: MutableSet<Chunk> = hashSetOf()

    fun cacheChunks() {
        cachedChunks = hashSetOf()
        region?.getChunks()?.forEach { chunk -> cachedChunks.add(chunk) }
    }

    override fun getRegionName(): String {
        return "Mine $id"
    }

    override fun getCuboid(): Cuboid? {
        return region
    }

    override fun is3D(): Boolean {
        return true
    }

    override fun getBreakableCuboid(): Cuboid? {
        return region
    }

    override fun resetBreakableCuboid() {
        Tasks.async {
            resetRegion()
        }
    }

    override fun supportsAbilityEnchants(): Boolean {
        return true
    }

    override fun supportsPassiveEnchants(): Boolean {
        return true
    }

    override fun supportsRewards(): Boolean {
        return true
    }

    override fun supportsAutoSell(): Boolean {
        return true
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

        MineBlockBreakEvent(player, block, this).call()
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

        val blockList = arrayListOf<BlockType>()
        for (i in 0 until (region!!.sizeX * region!!.sizeY * region!!.sizeZ)) {
            blockList.add(blocksConfig.pickRandomBlockType())
        }

        var index = 0

        val liteRegion = LiteRegion(region!!)
        LiteEdit.fill(liteRegion, object : LiteEdit.FillHandler {
            override fun getBlock(x: Int, y: Int, z: Int): IBlockData? {
                if (RewardsModule.isEnabled()) {
                    if (MineCrateHandler.isAttached(Location(region!!.world, x.toDouble(), y.toDouble(), z.toDouble()))) {
                        return null
                    }
                }

                val blockType = blockList[index++]
                return getData(blockType.material, blockType.data.toInt())
            }
        }, LiteEdit.VoidProgressCallBack)
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