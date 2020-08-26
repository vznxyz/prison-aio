/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class RobotHologramsOption(private val visible: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (visible) {
            "Show the holograms"
        } else {
            "Hide the holograms"
        }
    }

    override fun <T> getValue(): T {
        return visible as T
    }

    override fun getAbstractType(): Type {
        return RobotHologramsOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is RobotHologramsOption && other.visible == visible
    }

    override fun hashCode(): Int {
        return visible.hashCode()
    }

}