package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class ScoreboardVisibilityOption(private val visible: Boolean) : UserSettingOption {

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
        return ScoreboardVisibilityOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is ScoreboardVisibilityOption && other.visible == visible
    }

    override fun hashCode(): Int {
        return visible.hashCode()
    }

}