/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class SneakToTeleportOption(private val enabled: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (enabled) {
            "Enable sneak to teleport"
        } else {
            "Disable sneak to teleport"
        }
    }

    override fun <T> getValue(): T {
        return enabled as T
    }

    override fun getAbstractType(): Type {
        return SneakToTeleportOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is SneakToTeleportOption && other.enabled == enabled
    }

    override fun hashCode(): Int {
        return enabled.hashCode()
    }

}