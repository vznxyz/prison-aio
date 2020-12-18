/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.menu.MainMenu
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.menu.button.SettingButton
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class UserSettingsMenu(private val user: User) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Your Settings"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = BackButton { MainMenu(user).openMenu(player) }
            buttons[4] = InfoButton()

            for (setting in UserSetting.values()) {
                buttons[buttons.size] = SettingButton(user, setting)
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Settings"
        }

        override fun getMaterial(player: Player): Material {
            return Material.REDSTONE_COMPARATOR
        }
    }

}