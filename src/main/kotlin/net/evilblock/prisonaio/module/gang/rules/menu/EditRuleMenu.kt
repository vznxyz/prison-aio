/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.rules.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.prisonaio.module.gang.rules.GangRule
import net.evilblock.prisonaio.module.gang.rules.GangRulesHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditRuleMenu(private val rule: GangRule) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        val preview = if (rule.title.length >= 20) {
            rule.title.substring(0, 20)
        } else {
            rule.title
        }

        return "Edit Rule - $preview"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[1] = EditTitleButton()
            buttons[3] = EditDescriptionButton()

            for (i in 0 until 9) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                RulesEditorMenu().openMenu(player)
            }
        }
    }

    private inner class EditTitleButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Title"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Update the title of this rule, which is rendered in chat and menu text."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit title"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a new title.")
                    .acceptInput { input ->
                        rule.title = ChatColor.translateAlternateColorCodes('&', input)

                        Tasks.async {
                            GangRulesHandler.saveData()
                        }

                        this@EditRuleMenu.openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class EditDescriptionButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Description"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Update the description of this rule. Try to be as clear and precise as possible when explaining rules."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit description"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditRuleDescriptionMenu(rule).openMenu(player)
            }
        }
    }

}