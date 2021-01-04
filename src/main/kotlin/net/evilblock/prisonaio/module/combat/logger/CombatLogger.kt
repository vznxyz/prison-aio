/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.logger

import net.evilblock.cubed.entity.villager.VillagerEntity
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CombatLogger(
    private val owner: Player,
    location: Location
) : VillagerEntity(lines = listOf(), location = location) {

    var health: Double = 20.0
    var dead: Boolean = false

    override fun initializeData() {
        super.initializeData()

        persistent = false
    }

    override fun onLeftClick(player: Player) {
        if (dead) {
            return
        }



        if (health <= 0.0) {
            kill()
        } else {
            updateLines()
        }
    }

    fun updateLines() {
        updateLines(listOf(
            "${ChatColor.RED}${owner.name}${getHealthDisplay()}",
            "${ChatColor.GRAY}(Combat Logger)"
        ))
    }

    fun getHealthDisplay(): String {
        return buildString {
            if (!dead && health > 0) {
                append(" ${ChatColor.WHITE}${(health / 2.0).toInt()}${ChatColor.DARK_RED}${Constants.HEART_SYMBOL}")
            }
        }
    }

    fun kill() {
        dead = true

        CombatLoggerHandler.forgetLogger(this)

        val items = arrayListOf<ItemStack>()
        items.addAll(owner.inventory.armorContents)
        items.addAll(owner.inventory.storageContents)

        Tasks.sync {
            for (item in items) {
                location.world.dropItem(location, item)
            }
        }

        owner.inventory.heldItemSlot = 0
        owner.inventory.clear()
        owner.inventory.armorContents = null
        owner.allowFlight = false
        owner.isFlying = false
        owner.fireTicks = 0
        owner.noDamageTicks = 0
        owner.flySpeed = 0.1F
        owner.walkSpeed = 0.2F

        for (effect in owner.activePotionEffects) {
            owner.removePotionEffect(effect.type)
        }

        owner.teleport(PrisonAIO.instance.getSpawnLocation())
        owner.saveData()
    }

}