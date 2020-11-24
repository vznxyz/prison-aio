/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class GangQuickChatOption(private val enabled: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (enabled) {
            "Enable quick chat for my gang"
        } else {
            "Disable quick chat for my gang"
        }
    }

    override fun <T> getValue(): T {
        return enabled as T
    }

    override fun getAbstractType(): Type {
        return GangQuickChatOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is GangQuickChatOption && other.enabled == enabled
    }

    override fun hashCode(): Int {
        return enabled.hashCode()
    }

}