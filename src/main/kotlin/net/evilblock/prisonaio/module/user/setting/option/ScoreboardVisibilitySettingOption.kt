package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class ScoreboardVisibilitySettingOption(private val visible: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (visible) {
            "Show the scoreboard"
        } else {
            "Hide the scoreboard"
        }
    }

    override fun <T> getValue(): T {
        return visible as T
    }

    override fun getAbstractType(): Type {
        return ScoreboardVisibilitySettingOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is ScoreboardVisibilitySettingOption && other.visible == visible
    }

    override fun hashCode(): Int {
        return visible.hashCode()
    }

}