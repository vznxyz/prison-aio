/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class TradeRequestsOption(private val receive: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (receive) {
            "Receive trade requests"
        } else {
            "Don't receive trade requests"
        }
    }

    override fun <T> getValue(): T {
        return receive as T
    }

    override fun getAbstractType(): Type {
        return TradeRequestsOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is TradeRequestsOption && other.receive == receive
    }

    override fun hashCode(): Int {
        return receive.hashCode()
    }

}