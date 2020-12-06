/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region

import net.evilblock.cubed.serialize.AbstractTypeSerializable
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.Cancellable
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.projectiles.ProjectileSource

abstract class Region(val id: String, internal var cuboid: Cuboid? = null) : AbstractTypeSerializable {

    var persistent: Boolean = true

    abstract fun getRegionName(): String

    abstract fun getPriority(): Int

    open fun is3D(): Boolean {
        return false
    }

    open fun getCuboid(): Cuboid? {
        return cuboid
    }

    open fun setCuboid(cuboid: Cuboid?) {
        this.cuboid = cuboid
    }

    open fun getBreakableCuboid(): Cuboid? {
        return null
    }

    open fun resetBreakableCuboid() {

    }

    open fun supportsAbilityEnchants(): Boolean {
        return false
    }

    open fun supportsPassiveEnchants(): Boolean {
        return true
    }

    open fun supportsRewards(): Boolean {
        return false
    }

    open fun supportsAutoSell(): Boolean {
        return false
    }

    open fun onLeftClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {

    }

    open fun onRightClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {

    }

    open fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    open fun onBlockPlace(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    open fun onBucketEmpty(player: Player, emptiedAt: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    open fun onBucketFill(player: Player, filledFrom: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    open fun onBlockExplode(block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    open fun onBlockIgnite(block: Block, entity: Entity?, cause: BlockIgniteEvent.IgniteCause, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    open fun onEntityDamage(entity: Entity, cause: EntityDamageEvent.DamageCause, cancellable: Cancellable) {
        if (entity is Player) {
            cancellable.isCancelled = true
        }
    }

    open fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        if (victim is Player) {
            cancellable.isCancelled = true
        }

        if (victim is ItemFrame) {
            if (attacker is Player) {
                if (!RegionHandler.bypassCheck(attacker, cancellable)) {
                    cancellable.isCancelled = true
                }
            }
        }
    }

    open fun onProjectileLaunch(projectile: Projectile, source: ProjectileSource, cancellable: Cancellable) {

    }

    open fun onFoodLevelChange(cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    open fun onPlayerInteractEntity(player: Player, rightClicked: Entity, cancellable: Cancellable) {
        if (RegionHandler.bypassCheck(player, cancellable)) {
            return
        }

        cancellable.isCancelled = rightClicked !is Boat && rightClicked !is Minecart
    }

    open fun onPlayerDeath(player: Player, event: PlayerDeathEvent) {
        event.keepInventory = true
    }

    open fun onLeaveRegion(player: Player) {

    }

    open fun onEnterRegion(player: Player) {

    }

}