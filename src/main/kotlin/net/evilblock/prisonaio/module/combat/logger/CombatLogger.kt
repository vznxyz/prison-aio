/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.logger
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramEntity
import net.evilblock.cubed.entity.villager.VillagerEntity
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.combat.logger.event.CombatLoggerDeathEvent
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CombatLogger(@Transient val owner: Player) : VillagerEntity(lines = listOf(), location = owner.location) {

    companion object {
        const val LOGGER_DURATION = 10_000L
    }

    var expiration = System.currentTimeMillis() + LOGGER_DURATION

    override fun initializeData() {
        super.initializeData()

        persistent = false
        root = true

        attackable = true
        health = owner.health

        getAttachedHologram().forceUpdate()
    }

    override fun createHologram() {
        hologram = CombatLoggerHologram(this)
    }

    override fun getAttachedHologram(): UpdatingHologramEntity {
        return hologram as UpdatingHologramEntity
    }

    override fun attack(attacker: Entity, damage: Double, ignoreCooldown: Boolean): Boolean {
        if (isExpired()) {
            return false
        }

        val attacked = super.attack(attacker, damage, ignoreCooldown)
        if (attacked) {
            resetExpiration()
            getAttachedHologram().forceUpdate()
        }

        return attacked
    }

    override fun kill(killer: Entity) {
        super.kill(killer)

        val items = arrayListOf<ItemStack>()
        items.addAll(owner.inventory.armorContents.filterNotNull())
        items.addAll(owner.inventory.storageContents.filterNotNull())

        Tasks.sync {
            for (item in items) {
                location.world.dropItem(location, item)
            }
        }

        owner.inventory.heldItemSlot = 0
        owner.inventory.clear()
        owner.inventory.armorContents = null
        owner.updateInventory()
        owner.allowFlight = false
        owner.isFlying = false
        owner.fireTicks = 0
        owner.noDamageTicks = 0
        owner.flySpeed = 0.1F
        owner.walkSpeed = 0.2F

        for (effect in owner.activePotionEffects) {
            owner.removePotionEffect(effect.type)
        }

        // can't use Entity#teleport
        val spawn = PrisonAIO.instance.getSpawnLocation()
        (owner as CraftPlayer).handle.setLocation(spawn.x, spawn.y, spawn.z, spawn.yaw, spawn.pitch)
        owner.saveData()

        CombatLoggerDeathEvent(killer, this).call()

        Tasks.delayed(40L) {
            CombatLoggerHandler.forgetLogger(this)
        }
    }

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= expiration
    }

    fun resetExpiration() {
        expiration = System.currentTimeMillis() + LOGGER_DURATION
    }

    fun expire() {
        val location = location.clone()
        (owner as CraftPlayer).handle.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)

        owner.health = health
        owner.saveData()

        CombatLoggerHandler.forgetLogger(this)
        destroyForCurrentWatchers()
    }

    fun getRemainingDuration(): Long {
        return expiration - System.currentTimeMillis()
    }

    fun getRemainingSeconds(): Int {
        return (getRemainingDuration() / 1000.0).toInt()
    }

}