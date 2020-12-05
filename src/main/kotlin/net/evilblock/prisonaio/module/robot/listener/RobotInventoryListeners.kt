package net.evilblock.prisonaio.module.robot.listener

import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object RobotInventoryListeners : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {
        if (Menu.currentlyOpenedMenus.containsKey(event.whoClicked.uniqueId)) {
            return
        }

        if (event.clickedInventory == null) {
            return
        }

        if (event.cursor == null || event.cursor.amount > 1) {
            return
        }

        if (event.currentItem == null || event.currentItem.amount > 1) {
            return
        }

        if (!RobotUtils.isRobotItem(event.cursor) || !RobotUtils.isRobotItem(event.currentItem)) {
            return
        }

        val cursorTier = RobotHandler.getTier(event.cursor)
        val currentItemTier = RobotHandler.getTier(event.currentItem)
        if (cursorTier == currentItemTier) {
            event.isCancelled = true

            if (cursorTier >= 7) {
                event.whoClicked.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You can't combine robots that are tier 7 or above.")
                return
            }

            val cursor = event.cursor
            val clicked = event.currentItem

            event.cursor = null
            event.currentItem = null

            Tasks.delayed(1L) {
                ConfirmMenu("Combine Robots?") { confirmed ->
                    val player = event.whoClicked as Player
                    if (confirmed) {
                        player.inventory.addItem(RobotUtils.makeRobotItem(1, cursorTier.coerceAtLeast(0) + 1))
                        player.updateInventory()

                        player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.GRAY}You've combined your robots into a ${ChatColor.RED}${ChatColor.BOLD}Tier ${cursorTier.coerceAtLeast(0) + 1} Robot${ChatColor.GRAY}!")
                        player.playSound(event.whoClicked.location, Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F)
                    } else {
                        player.inventory.addItem(cursor)
                        player.inventory.addItem(clicked)
                        player.updateInventory()

                        player.sendMessage("${ChatColor.RED}Your tiers haven't been combined and have been returned to your inventory.")
                    }
                }.openMenu(event.whoClicked as Player)
            }
        } else {
            if (cursorTier == -1 || currentItemTier == -1) {
                event.whoClicked.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You can't combine a tiered robot with a robot that has no tiers.")
            } else {
                event.whoClicked.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You can't combine two robots of different tiers.")
            }

            event.isCancelled = true
        }
    }

}