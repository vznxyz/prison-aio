/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.serialize.AbstractTypeSerializable
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import net.evilblock.prisonaio.module.mine.menu.button.MineTeleportButton
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

abstract class Mine(id: String) : BitmaskRegion(id, null), AbstractTypeSerializable {

    var region: Cuboid? = null
    var spawnPoint: Location? = null
    var permission: String? = Permissions.MINE_ACCESS + id

    @Transient
    var cachedChunks: MutableSet<Chunk> = hashSetOf()

    init {
        persistent = false
    }

    open fun initializeData() {
        cacheChunks()
    }

    override fun getRegionName(): String {
        return "Mine $id"
    }

    override fun getPriority(): Int {
        return 100
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
        resetRegion()
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

    open fun supportsAutomaticReset(): Boolean {
        return true
    }

    open fun getEditorButtons(): List<Button> {
        return listOf(MineTeleportButton(this))
    }

    /**
     * Executes this mine's region reset implementation.
     */
    @Throws(IllegalStateException::class)
    open fun resetRegion() {
        throw IllegalStateException("")
    }

    /**
     * Builds and returns a list of players that are considered nearby.
     */
    fun getNearbyPlayers(): List<Player> {
        // players cannot be nearby a mine that has no region
        if (region == null) {
            return emptyList()
        }

        return Bukkit.getOnlinePlayers().filter { player -> isNearbyMine(player) }.toList()
    }

    /**
     * If the given [player] is considered nearby.
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

    fun cacheChunks() {
        cachedChunks = hashSetOf()
        region?.getChunks()?.forEach { chunk -> cachedChunks.add(chunk) }
    }

}