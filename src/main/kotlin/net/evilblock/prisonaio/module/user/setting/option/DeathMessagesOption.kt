/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class DeathMessagesOption(var receive: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (receive) {
            "Show PvP death notifications"
        } else {
            "Hide PvP death notifications"
        }
    }

    override fun <T> getValue(): T {
        return receive as T
    }

    override fun getAbstractType(): Type {
        return DeathMessagesOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is DeathMessagesOption && other.receive == receive
    }

    override fun hashCode(): Int {
        return receive.hashCode()
    }

}