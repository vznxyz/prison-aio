/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class PrivateMessagesOption(private val mode: OptionValue) : UserSettingOption {

    override fun getName(): String {
        return mode.getDisplayName()
    }

    override fun <T> getValue(): T {
        return mode as T
    }

    override fun getAbstractType(): Type {
        return PrivateMessagesOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is PrivateMessagesOption && other.mode == mode
    }

    override fun hashCode(): Int {
        return mode.hashCode()
    }

    enum class OptionValue(private val text: String) {

        RECEIVE_ALL("Receive all private messages"),
        RECEIVE_INITIATED("Receive private messages from conversations you initiated"),
        DISABLED("Don't receive any private messages");

        fun getDisplayName(): String {
            return text
        }

    }

}