/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.rules.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.rules.GangRule
import net.evilblock.prisonaio.module.gang.rules.GangRulesHandler
import org.bukkit.entity.Player

class EditRuleDescriptionMenu(private val rule: GangRule) : TextEditorMenu(lines = rule.description) {

    init {
        supportsColors = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Rule Description"
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditRuleMenu(rule).openMenu(player)
        }
    }

    override fun onSave(player: Player, list: List<String>) {
        rule.description = list.toMutableList()

        Tasks.async {
            GangRulesHandler.saveData()
        }
    }

}