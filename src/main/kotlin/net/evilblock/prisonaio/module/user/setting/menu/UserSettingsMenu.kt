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
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.lang.StringBuilder

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
                val builder = StringBuilder()

                builder.append(if (user.settings.getSettingOption(setting) == option) {
                    " ${ChatColor.BLUE}${ChatColor.BOLD}» ${ChatColor.GREEN}${option.getName()}"
                } else {
                    "    ${ChatColor.YELLOW}${option.getName()}"
                })

                if (setting.getDefaultOption() == option) {
                    builder.append(" ${ChatColor.GRAY}(Default)")
                }

                description.add(builder.toString())
            }

            description.add("")
            description.add("${ChatColor.BLUE}${Constants.ARROW_UP} ${ChatColor.YELLOW}${ChatColor.BOLD}LEFT-CLICK")
            description.add("${ChatColor.BLUE}${Constants.ARROW_DOWN} ${ChatColor.YELLOW}${ChatColor.BOLD}RIGHT-CLICK")

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
                user.settings.updateSettingOption(setting, setting.getPreviousValue(user.settings.getSettingOption(setting)))
                setting.onUpdate.invoke(user, user.settings.getSettingOption(setting))
            } else if (clickType.isRightClick) {
                user.settings.updateSettingOption(setting, setting.getNextValue(user.settings.getSettingOption(setting)))
                setting.onUpdate.invoke(user, user.settings.getSettingOption(setting))
            }
        }
    }

}