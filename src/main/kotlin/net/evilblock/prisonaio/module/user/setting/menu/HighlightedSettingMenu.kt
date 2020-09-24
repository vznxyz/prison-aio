/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.menu.button.SettingButton
import org.bukkit.entity.Player

class HighlightedSettingMenu (
    private val setting: UserSetting,
    private val close: (() -> Unit)? = null
) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Settings (${setting.getDisplayName()})"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return mapOf(4 to SettingButton(UserHandler.getUser(player.uniqueId), setting))
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        close?.invoke()
    }

}