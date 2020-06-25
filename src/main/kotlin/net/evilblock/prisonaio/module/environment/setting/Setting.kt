/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.environment.setting

import net.evilblock.prisonaio.util.Constants
import org.bukkit.ChatColor

enum class Setting(val defaultValue: Any? = null) {

    FIRST_JOIN_MESSAGE_FORMAT("${ChatColor.RED}${Constants.THICK_VERTICAL_LINE} ${ChatColor.RED}{playerName} ${ChatColor.GRAY}has joined the server for the first time! (${ChatColor.RED}#{uniqueJoin}${ChatColor.GRAY})"),
    FIRST_JOIN_MESSAGE_TOGGLE(true);

    fun <T> getValue(): T {
        return SettingHandler.getSetting(this) as T
    }

    fun <T> updateValue(value: T) {
        SettingHandler.updateSetting(this, value)
    }

}