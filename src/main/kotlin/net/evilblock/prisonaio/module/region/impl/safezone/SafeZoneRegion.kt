/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.impl.safezone

import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.ChatColor
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.entity.EntityDamageEvent

class SafeZoneRegion : Region {

    override fun getRegionName(): String {
        return "${ChatColor.GREEN}${ChatColor.BOLD}SafeZone"
    }

    override fun getCuboid(): Cuboid? {
        return null
    }

    override fun is3D(): Boolean {
        return false
    }

    override fun getBreakableCuboid(): Cuboid? {
        return null
    }

    override fun resetBreakableCuboid() {

    }

    override fun supportsAbilityEnchants(): Boolean {
        return false
    }

    override fun supportsPassiveEnchants(): Boolean {
        return true
    }

    override fun supportsRewards(): Boolean {
        return false
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onBlockPlace(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onBucketEmpty(player: Player, emptiedAt: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onBucketFill(player: Player, filledFrom: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onBlockExplode(block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onBlockIgnite(block: Block, entity: Entity?, cause: BlockIgniteEvent.IgniteCause, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onEntityDamage(entity: Entity, cause: EntityDamageEvent.DamageCause, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    override fun onFoodLevelChange(cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

}