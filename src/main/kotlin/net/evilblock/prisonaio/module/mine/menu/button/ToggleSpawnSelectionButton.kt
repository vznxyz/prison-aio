/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.menu.button

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockMine
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ToggleSpawnSelectionButton(private val mine: LuckyBlockMine) : Button() {

    override fun getName(player: Player): String {
        return if (LuckyBlockHandler.spawnSelectionHandler.isAttached(player)) {
            "${ChatColor.RED}${ChatColor.BOLD}Disable Spawn Location Editor"
        } else {
            "${ChatColor.GREEN}${ChatColor.BOLD}Enable Spawn Location Editor"
        }
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.add("")

            if (LuckyBlockHandler.spawnSelectionHandler.isAttached(player)) {
                desc.addAll(TextSplitter.split(text = "When you're finished selecting spawn points, click here to disable the editor."))
            } else {
                desc.addAll(TextSplitter.split(text = "Click here to enable the editor. When enabled, left-click a block to add/remove it as a spawn location for this mine."))
            }
        }
    }

    override fun getMaterial(player: Player): Material {
        return Material.INK_SACK
    }

    override fun getDamageValue(player: Player): Byte {
        return if (LuckyBlockHandler.spawnSelectionHandler.isAttached(player)) {
            10
        } else {
            8
        }
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            if (mine.region == null) {
                player.sendMessage("${ChatColor.RED}You must define the mine's region first!")
                return
            }

            if (LuckyBlockHandler.spawnSelectionHandler.isAttached(player)) {
                LuckyBlockHandler.spawnSelectionHandler.removeHandler(player)
            } else {
                player.sendMessage("${ChatColor.GREEN}Left-click a block to add/remove it as a spawn location.")

                FancyMessage("${ChatColor.GREEN}When you're finished, click ")
                    .then("${ChatColor.AQUA}${ChatColor.BOLD}${ChatColor.UNDERLINE}HERE")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to disable the editor."))
                    .then("${ChatColor.GREEN} to disable the editor.")
                    .send(player)

                LuckyBlockHandler.spawnSelectionHandler.attachHandler(player) { event ->
                    if (event.action == Action.LEFT_CLICK_BLOCK) {
                        event.isCancelled = true

                        if (!mine.region!!.contains(event.clickedBlock.location)) {
                            event.player.sendMessage("${ChatColor.RED}You need to select locations that are within the mine's bounds!")
                            return@attachHandler
                        }

                        if (mine.spawnLocations.contains(event.clickedBlock.location)) {
                            mine.spawnLocations.remove(event.clickedBlock.location)
                        } else {
                            mine.spawnLocations.add(event.clickedBlock.location)
                        }

                        Tasks.async {
                            MineHandler.saveData()
                        }
                    }
                }
            }
        }
    }

}