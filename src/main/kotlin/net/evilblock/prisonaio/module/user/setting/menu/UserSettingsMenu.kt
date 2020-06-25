/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import net.evilblock.prisonaio.util.Constants
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UserSettingsMenu(private val user: User) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Your Settings"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (setting in UserSetting.values()) {
            buttons[buttons.size] = SettingButton(setting)
        }

        return buttons
    }

    private inner class SettingButton(private val setting: UserSetting) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${setting.getDisplayName()}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = setting.getDescription(), linePrefix = "${ChatColor.GRAY}"))
            description.add("")

            for (option in setting.getOptions<UserSettingOption>()) {
                if (user.getSettingOption(setting) == option) {
                    description.add(" ${ChatColor.BLUE}${ChatColor.BOLD}» ${ChatColor.GREEN}${option.getName()}")
                } else {
                    description.add("    ${ChatColor.YELLOW}${option.getName()}")
                }
            }

            description.add("")
            description.add("${ChatColor.RED}${Constants.ARROW_UP} ${ChatColor.YELLOW}${ChatColor.BOLD}LEFT-CLICK")
            description.add("${ChatColor.GREEN}${Constants.ARROW_DOWN} ${ChatColor.YELLOW}${ChatColor.BOLD}RIGHT-CLICK")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return setting.getIcon().type
        }

        override fun getDamageValue(player: Player): Byte {
            return setting.getIcon().durability.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                user.updateSettingOption(setting, setting.getPreviousValue(user.getSettingOption(setting)))
                setting.onUpdate.invoke(user, user.getSettingOption(setting))
            } else if (clickType.isRightClick) {
                user.updateSettingOption(setting, setting.getNextValue(user.getSettingOption(setting)))
                setting.onUpdate.invoke(user, user.getSettingOption(setting))
            }
        }
    }

}