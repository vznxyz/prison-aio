/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region

import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.region.listener.RegionListeners
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.Cancellable
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

interface Region {

    fun getRegionName(): String

    fun getCuboid(): Cuboid?

    fun is3D(): Boolean

    fun getBreakableCuboid(): Cuboid?

    fun resetBreakableCuboid()

    fun supportsAbilityEnchants(): Boolean {
        return false
    }

    fun supportsPassiveEnchants(): Boolean {
        return true
    }

    fun supportsRewards(): Boolean {
        return false
    }

    fun supportsAutoSell(): Boolean {
        return false
    }

    fun onLeftClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {

    }

    fun onRightClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {

    }

    fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun onBlockPlace(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun onBucketEmpty(player: Player, emptiedAt: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun onBucketFill(player: Player, filledFrom: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun onBlockExplode(block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun onBlockIgnite(block: Block, entity: Entity?, cause: BlockIgniteEvent.IgniteCause, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun onEntityDamage(entity: Entity, cause: EntityDamageEvent.DamageCause, cancellable: Cancellable) {
        if (entity is Player) {
            cancellable.isCancelled = true
        }
    }

    fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        if (victim is Player) {
            cancellable.isCancelled = true
        }

        if (victim is ItemFrame) {
            if (attacker is Player) {
                if (!RegionListeners.bypassCheck(attacker, cancellable)) {
                    cancellable.isCancelled = true
                }
            }
        }
    }

    fun onFoodLevelChange(cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun onPlayerInteractEntity(player: Player, rightClicked: Entity, cancellable: Cancellable) {
        if (RegionListeners.bypassCheck(player, cancellable)) {
            return
        }

        cancellable.isCancelled = rightClicked !is Boat && rightClicked !is Minecart
    }

    fun onPlayerDeath(player: Player, event: PlayerDeathEvent) {

    }

    fun onLeaveRegion(player: Player) {

    }

    fun onEnterRegion(player: Player) {

    }

}