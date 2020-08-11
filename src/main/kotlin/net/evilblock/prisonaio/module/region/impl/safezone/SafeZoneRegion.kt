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
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.projectiles.ProjectileSource

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

    override fun onEntityDamage(entity: Entity, cause: EntityDamageEvent.DamageCause, cancellable: Cancellable) {
        super.onEntityDamage(entity, cause, cancellable)

        entity.fireTicks = 0
    }

    override fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        super.onEntityDamageEntity(attacker, victim, cause, damage, cancellable)

        victim.fireTicks = 0
    }

    override fun onProjectileLaunch(projectile: Projectile, source: ProjectileSource, cancellable: Cancellable) {
        cancellable.isCancelled = true

        if (source is Player) {
            source.sendMessage("${ChatColor.RED}You can't launch projectiles in a safe-zone.")
        }
    }

}