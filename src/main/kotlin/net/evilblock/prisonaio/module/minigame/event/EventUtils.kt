package net.evilblock.prisonaio.module.minigame.event

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player

object EventUtils {

    @JvmStatic
    fun resetInventoryNow(player: Player) {
        player.inventory.heldItemSlot = 0
        player.inventory.clear()
        player.inventory.armorContents = null
        player.allowFlight = false
        player.isFlying = false
        player.fireTicks = 0
        player.noDamageTicks = 0
        player.flySpeed = 0.1F
        player.walkSpeed = 0.2F

        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }

        player.updateInventory()
    }

    @JvmStatic
    fun resetPlayer(player: Player) {
        resetInventoryNow(player)

        player.gameMode = GameMode.SURVIVAL
        player.inventory.setItem(4, EventItems.LEAVE_EVENT)
        player.updateInventory()
    }

    @JvmStatic
    fun hasEmptyInventory(player: Player): Boolean {
        for (itemStack in player.inventory.contents) {
            if (itemStack != null && itemStack.type != Material.AIR) {
                return false
            }
        }

        for (itemStack in player.inventory.armorContents) {
            if (itemStack != null && itemStack.type != Material.AIR) {
                return false
            }
        }

        return true
    }

}