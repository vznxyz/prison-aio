/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.pickaxe.prestige.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.enchant.pickaxe.prestige.PickaxePrestige
import net.evilblock.prisonaio.module.enchant.pickaxe.prestige.PickaxePrestigeHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class PrestigeEditorMenu : PaginatedMenu() {

    init {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Pickaxe Prestige Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = AddPrestigeButton()

        for (i in 9..17) {
            buttons[i] = GlassButton(0)
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (prestige in PickaxePrestigeHandler.getPrestigeSet().sortedBy { it.number }) {
            buttons[buttons.size] = PrestigeButton(prestige)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    private inner class AddPrestigeButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Prestige"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Create a new prestige by completing",
                "${ChatColor.GRAY}the setup procedure.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new prestige"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt("${ChatColor.GREEN}Please input a prestige (number) to create.") { number ->
                    if (PickaxePrestigeHandler.getPrestige(number.toInt()) != null) {
                        player.sendMessage("${ChatColor.RED}A prestige with that number already exists.")
                        return@NumberPrompt
                    }

                    assert(number.toInt() > 0) { "Number must be more than 0" }

                    val prestige = PickaxePrestige(number.toInt())
                    PickaxePrestigeHandler.trackPrestige(prestige)

                    Tasks.async {
                        PickaxePrestigeHandler.saveData()
                    }

                    EditPrestigeMenu(prestige).openMenu(player)
                }.start(player)
            }
        }
    }

    private inner class PrestigeButton(private val prestige: PickaxePrestige) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Prestige ${prestige.number}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit prestige")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete prestige")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_PICKAXE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditPrestigeMenu(prestige).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        PickaxePrestigeHandler.forgetPrestige(prestige)

                        Tasks.async {
                            PickaxePrestigeHandler.saveData()
                        }
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made.")
                    }

                    this@PrestigeEditorMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}