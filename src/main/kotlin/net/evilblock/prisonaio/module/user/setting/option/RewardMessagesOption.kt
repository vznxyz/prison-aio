/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class RewardMessagesOption(private val receive: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (receive) {
            "Receive reward messages"
        } else {
            "Don't receive reward messages"
        }
    }

    override fun <T> getValue(): T {
        return receive as T
    }

    override fun getAbstractType(): Type {
        return RewardMessagesOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is RewardMessagesOption && other.receive == receive
    }

    override fun hashCode(): Int {
        return receive.hashCode()
    }

}