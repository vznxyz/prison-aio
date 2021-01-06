/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme.impl.avatar.path.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.theme.impl.avatar.structure.AvatarElement
import net.evilblock.prisonaio.module.theme.impl.avatar.user.AvatarThemeUserData
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class SelectPathMenu(private val userData: AvatarThemeUserData) : Menu() {

    override fun getTitle(player: Player): String {
        return "Select Path"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also {
            it[13] = InfoButton()
            it[19] = SelectionButton(AvatarElement.AIR)
            it[21] = SelectionButton(AvatarElement.WATER)
            it[23] = SelectionButton(AvatarElement.EARTH)
            it[25] = SelectionButton(AvatarElement.FIRE)

            for (i in 0 until 36) {
                if (!it.containsKey(i)) {
                    it[i] = GlassButton(7)
                }
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "Information"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf()
        }
    }

    private inner class SelectionButton(private val element: AvatarElement) : Button() {
        override fun getName(player: Player): String {
            return element.getDisplayName()
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.add("${element.nameFormat}Passive Ability")
                it.addAll(TextSplitter.split(text = element.abilityDescription, linePrefix = ChatColor.GRAY.toString()))
                it.add("")
                it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to select element")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (userData.hasBaseElement()) {
                player.sendMessage("${ChatColor.RED}You've already selected your base path!")
                return
            }

            ConfirmMenu { confirmed ->
                if (confirmed) {
                    userData.updateBaseElement(element)
                    // do a cool animation tailored to the element
                } else {
                    this@SelectPathMenu.openMenu(player)
                }
            }.openMenu(player)
        }
    }

}