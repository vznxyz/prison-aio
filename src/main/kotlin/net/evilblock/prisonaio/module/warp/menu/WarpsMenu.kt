/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PageButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.MainMenu
import net.evilblock.prisonaio.module.warp.Warp
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class WarpsMenu(private val warps: List<Warp>) : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Warps"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[4] = InfoButton()

            if (page == 1) {
                buttons[0] = BackButton { MainMenu(UserHandler.getUser(player)).openMenu(player) }
            } else {
                buttons[0] = PageButton(-1, this)
            }

            buttons[8] = PageButton(1, this)
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (warp in warps) {
                buttons[buttons.size] = WarpButton(warp)
            }
        }
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Warps"
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENDER_PEARL
        }
    }

    private inner class WarpButton(private val warp: Warp) : Button() {
        override fun getName(player: Player): String {
            return warp.getFormattedName()
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = warp.description))
                desc.add("")

                if (warp.hasPermission(player)) {
                    if (warp.isPriceSet()) {
                        desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to teleport for ${warp.currency!!.format(warp.price!!)}"))
                    } else {
                        desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to teleport"))
                    }
                } else {
                    desc.add("${ChatColor.RED}No permission to access this warp!")
                }
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(warp.icon)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!warp.hasPermission(player)) {
                player.sendMessage("${ChatColor.RED}You don't have access to that warp!")
                return
            }

            if (warp.isPriceSet()) {
                if (warp.canAfford(player)) {
                    warp.teleport(player)
                } else {
                    player.sendMessage("${ChatColor.RED}You can't afford to purchase that warp!")
                }
            } else {
                warp.teleport(player)
            }
        }
    }

}