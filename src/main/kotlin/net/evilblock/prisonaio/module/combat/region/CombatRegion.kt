/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.region

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.combat.timer.CombatTimer
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

class CombatRegion(val id: String, private var cuboid: Cuboid) : Region {

    override fun getRegionName(): String {
        return "Combat Zone"
    }

    override fun getCuboid(): Cuboid? {
        return cuboid
    }

    fun setCuboid(cuboid: Cuboid) {
        this.cuboid = cuboid
    }

    override fun is3D(): Boolean {
        return true
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
        return false
    }

    override fun supportsRewards(): Boolean {
        return false
    }

    override fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK || cause == EntityDamageEvent.DamageCause.LAVA) {
            cancellable.isCancelled = false
            return
        }

        if (attacker is Player && victim is Player) {
            if (attacker == victim) {
                cancellable.isCancelled = false
                return
            }

            if (!cuboid.contains(victim.location)) {
                cancellable.isCancelled = true
                return
            }

            val attackerCell = CellHandler.getAssumedCell(attacker.uniqueId)
            val victimCell = CellHandler.getAssumedCell(victim.uniqueId)

            if (attackerCell != null && attackerCell == victimCell) {
                cancellable.isCancelled = true
                attacker.sendMessage("${ChatColor.YELLOW}You can't attack ${ChatColor.DARK_GREEN}${attacker.name}${ChatColor.YELLOW}!")
                return
            }

            cancellable.isCancelled = false

            val attackerTimer = CombatTimerHandler.getTimer(attacker.uniqueId)
            if (attackerTimer == null) {
                CombatTimerHandler.trackTimer(CombatTimer(attacker.uniqueId))
            } else {
                attackerTimer.reset()
            }

            val victimTimer = CombatTimerHandler.getTimer(victim.uniqueId)
            if (victimTimer == null) {
                CombatTimerHandler.trackTimer(CombatTimer(victim.uniqueId))
            } else {
                victimTimer.reset()
            }
        }
    }

    override fun onFoodLevelChange(cancellable: Cancellable) {
        cancellable.isCancelled = false
    }

    override fun onPlayerDeath(player: Player, event: PlayerDeathEvent) {
        val deathLocation = player.location

        // force the respawn
        player.spigot().respawn()

        // we filter the dropped items for tools that should be kept on death
        val itemsKept = arrayListOf<ItemStack>()
        val dropIterator = event.drops.iterator()
        while (dropIterator.hasNext()) {
            val itemDrop = dropIterator.next()
            if (MechanicsModule.isTool(itemDrop)) {
                itemsKept.add(itemDrop)
                dropIterator.remove()
            }
        }

        // create a new copy of the drops list, so we can clear the event from dropping any items
        val drops = event.drops.toList()
        event.drops.clear()

        // manually drop the drops list
        for (itemDrop in drops) {
            deathLocation.world.dropItem(deathLocation, itemDrop)
        }

        // remove fire and add kept items back to inventory
        Tasks.delayed(1L) {
            player.fireTicks = 0
            player.inventory.clear()

            if (itemsKept.isNotEmpty()) {
                for (item in itemsKept) {
                    player.inventory.addItem(item)
                }
            }

            player.updateInventory()
        }

        // reset combat timer so player can execute commands again
        val timer = CombatTimerHandler.getTimer(player.uniqueId)
        if (timer != null) {
            CombatTimerHandler.forgetTimer(timer)
        }
    }

    override fun onLeaveRegion(player: Player) {
        player.sendMessage("${ChatColor.GREEN}You are now safe as you have the left the combat zone.")
    }

    override fun onEnterRegion(player: Player) {
        if (player.gameMode != GameMode.CREATIVE) {
            player.allowFlight = false
            player.isFlying = false
            player.walkSpeed = 0.2F
            player.flySpeed = 0.2F
        }

        player.updateInventory()
        player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}DANGER! ${ChatColor.RED}You have entered a combat zone.")

        EnchantsManager.handleItemSwitch(player, null, null)
    }

}