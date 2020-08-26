/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class ScoreboardStyleOption(private val style: ScoreboardStyle) : UserSettingOption {

    override fun getName(): String {
        return style.getDisplayName()
    }

    override fun <T> getValue(): T {
        return style as T
    }

    override fun getAbstractType(): Type {
        return ScoreboardStyleOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is ScoreboardStyleOption && other.style == style
    }

    override fun hashCode(): Int {
        return style.hashCode()
    }

    enum class ScoreboardStyle(private val displayName: String) {
        SIMPLE("Simple scoreboard"),
        FANCY("Fancy scoreboard");

        fun getDisplayName(): String {
            return displayName
        }
    }

}