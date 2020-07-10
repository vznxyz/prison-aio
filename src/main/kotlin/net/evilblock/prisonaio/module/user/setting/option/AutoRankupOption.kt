/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class AutoRankupOption(private val enabled: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (enabled) {
            "Automatically purchase rankups"
        } else {
            "Purchase rankups by command"
        }
    }

    override fun <T> getValue(): T {
        return enabled as T
    }

    override fun getAbstractType(): Type {
        return AutoRankupOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is AutoRankupOption && other.enabled == enabled
    }

    override fun hashCode(): Int {
        return enabled.hashCode()
    }

}