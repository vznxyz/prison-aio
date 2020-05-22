package net.evilblock.prisonaio.module.mechanic.region

import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.util.Constants
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.entity.EntityDamageEvent

interface Region {

    fun getRegionName(): String

    fun getBreakableRegion(): Cuboid?

    fun resetBreakableRegion() {}

    fun onLeftClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {

    }

    fun onRightClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {
        if (Constants.CONTAINER_TYPES.contains(clickedBlock.type)) {
            cancellable.isCancelled = true
        }
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
        cancellable.isCancelled = true
    }

    fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun onPlayerInteractEntity(player: Player, rightClicked: Entity, cancellable: Cancellable) {
        cancellable.isCancelled = true
    }

    fun supportsEnchants(): Boolean {
        return false
    }

    fun supportsRewards(): Boolean {
        return false
    }

}