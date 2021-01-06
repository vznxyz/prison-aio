/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.bitmask.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditRegionBitmaskMenu(private val region: BitmaskRegion) : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Bitmask"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (bitmaskType in RegionBitmask.values()) {
                buttons[buttons.size] = BitmaskTypeButton(bitmaskType)
            }
        }
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 54
    }

    private inner class BitmaskTypeButton(private val bitmaskType: RegionBitmask) : Button() {
        override fun getName(player: Player): String {
            return buildString {
                if (region.hasBitmask(bitmaskType)) {
                    append("${ChatColor.GREEN}${ChatColor.BOLD}${bitmaskType.displayName}")
                } else {
                    append("${ChatColor.RED}${ChatColor.BOLD}${bitmaskType.displayName}")
                }

                append(" ${ChatColor.GRAY}(${bitmaskType.bitmaskValue})")
            }
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = bitmaskType.description))
                desc.add("")

                if (region.hasBitmask(bitmaskType)) {
                    desc.add(styleAction(ChatColor.RED, "LEFT-CLICK", "to remove bitmask"))
                } else {
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to add bitmask"))
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return bitmaskType.icon.type
        }

        override fun getDamageValue(player: Player): Byte {
            return bitmaskType.icon.durability.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (region.hasBitmask(bitmaskType)) {
                    region.removeBitmask(bitmaskType)
                } else {
                    region.addBitmask(bitmaskType)
                }

                Tasks.async {
                    if (region.persistent) {
                        RegionHandler.saveData()
                    }
                }
            }
        }
    }

}