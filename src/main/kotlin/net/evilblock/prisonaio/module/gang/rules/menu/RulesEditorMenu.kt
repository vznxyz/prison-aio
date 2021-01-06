/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.rules.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PageButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.prisonaio.module.system.menu.PrisonManagementMenu
import net.evilblock.prisonaio.module.gang.rules.GangRule
import net.evilblock.prisonaio.module.gang.rules.GangRulesHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class RulesEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Gang Rules Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[2] = AddRuleButton()

            if (page == 1) {
                buttons[0] = BackButton { PrisonManagementMenu().openMenu(player) }
                buttons[8] = PageButton(1, this)
            } else {
                buttons[0] = PageButton(-1, this)
                buttons[8] = PageButton(1, this)
            }

            for (i in 9 until 17) {
                buttons[i] = GlassButton(0)
            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            val rules = GangRulesHandler.rules.let {
                if (it.isNotEmpty()) {
                    it.sortedBy { it.order }
                } else {
                    it
                }
            }

            for (rule in rules) {
                buttons[buttons.size] = RuleButton(rule)
            }
        }
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    private inner class AddRuleButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Add New Rule"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Create a new rule by following the setup procedure."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to add new rule"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a title for the new rule.")
                    .acceptInput { input ->
                        val rule = GangRule(ChatColor.translateAlternateColorCodes('&', input))
                        GangRulesHandler.rules.add(rule)

                        Tasks.async {
                            GangRulesHandler.saveData()
                        }

                        EditRuleMenu(rule).openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class RuleButton(private val rule: GangRule) : Button() {

    }

}