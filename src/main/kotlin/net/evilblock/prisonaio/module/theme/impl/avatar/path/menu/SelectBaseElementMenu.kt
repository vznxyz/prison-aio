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
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.theme.impl.avatar.structure.AvatarElement
import net.evilblock.prisonaio.module.theme.impl.avatar.user.AvatarThemeUserData
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class SelectBaseElementMenu(private val userData: AvatarThemeUserData) : Menu() {

    override fun getTitle(player: Player): String {
        return "Select Your Nation"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also {
            it[13] = InfoButton()

            it[28] = HeadButton(AvatarElement.AIR)
            it[37] = SelectionButton(AvatarElement.AIR)

            it[30] = HeadButton(AvatarElement.WATER)
            it[39] = SelectionButton(AvatarElement.WATER)

            it[32] = HeadButton(AvatarElement.EARTH)
            it[41] = SelectionButton(AvatarElement.EARTH)

            it[34] = HeadButton(AvatarElement.FIRE)
            it[43] = SelectionButton(AvatarElement.FIRE)

            for (i in 0 until 54) {
                if (!it.containsKey(i)) {
                    it[i] = GlassButton(7)
                }
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "Information"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf()
        }
    }

    private inner class HeadButton(private val element: AvatarElement) : TexturedHeadButton() {
        override fun getName(player: Player): String {
            return element.getDisplayName()
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.add("${element.nameFormat}Passive Ability")
                it.addAll(TextSplitter.split(text = element.abilityDescription, linePrefix = ChatColor.GRAY.toString()))
            }
        }

        override fun getTexture(player: Player): String {
            return element.headTexture
        }
    }

    private inner class SelectionButton(private val element: AvatarElement) : Button() {
        override fun getName(player: Player): String {
            return "${element.color}${ChatColor.BOLD}Select ${element.getDisplayName()}"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to select element"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return element.glassColor
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (userData.hasBaseElement()) {
                player.sendMessage("${ChatColor.RED}You've already selected your base element!")
                return
            }

            ConfirmMenu { confirmed ->
                if (userData.hasBaseElement()) {
                    player.sendMessage("${ChatColor.RED}You've already selected your base element!")
                    return@ConfirmMenu
                }

                if (confirmed) {
                    userData.updateBaseElement(element)
                    // do a cool animation tailored to the element
                } else {
                    this@SelectBaseElementMenu.openMenu(player)
                }
            }.openMenu(player)
        }
    }

}