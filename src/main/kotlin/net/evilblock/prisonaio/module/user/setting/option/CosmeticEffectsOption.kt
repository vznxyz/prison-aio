/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class CosmeticEffectsOption(private var display: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (display) {
            "Display cosmetic effects"
        } else {
            "Hide cosmetic effects"
        }
    }

    override fun <T> getValue(): T {
        return display as T
    }

    override fun getAbstractType(): Type {
        return CosmeticEffectsOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is CosmeticEffectsOption && display == other.display
    }

    override fun hashCode(): Int {
        return display.hashCode()
    }

}