/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.rename.listener

import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.tool.rename.RenameTagUtils
import net.evilblock.source.chat.filter.ChatFilterHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object RenameTagListeners : Listener {

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.clickedInventory == null || event.cursor == null || event.currentItem == null) {
            return
        }

        if (event.currentItem.amount > 1 || event.cursor.amount > 1) {
            return
        }

        if (event.cursor.type != Material.NAME_TAG || !MechanicsModule.isPickaxe(event.currentItem) || !RenameTagUtils.isRenameTag(event.cursor)) {
            return
        }

        event.cursor = null
        event.isCancelled = true

        val pickaxeItem = event.currentItem
        val pickaxeData = PickaxeHandler.getPickaxeData(pickaxeItem) ?: return

        val player = event.whoClicked as Player

        ConfirmMenu("Rename pickaxe?") { confirmed ->
            if (confirmed) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a new name for your pickaxe. ${ChatColor.GRAY}(Limited to 32 characters)")
                    .withLimit(32)
                    .acceptInput { input ->
                        val colored = ChatColor.translateAlternateColorCodes('&', input)
                        val stripped = ChatColor.stripColor(colored)

                        if (ChatFilterHandler.filterMessage(stripped) != null) {
                            player.sendMessage("${ChatColor.RED}The name you input contains inappropriate content. Please try a different name.")
                            return@acceptInput
                        }

                        pickaxeData.customName = colored
                        pickaxeData.applyMeta(pickaxeItem)

                        player.updateInventory()
                    }
                    .start(player)
            }
        }
    }

}