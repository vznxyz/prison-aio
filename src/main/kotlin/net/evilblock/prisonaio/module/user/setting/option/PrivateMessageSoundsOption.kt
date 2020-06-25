/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class PrivateMessageSoundsOption(private val playSounds: Boolean) : UserSettingOption {

    override fun getName(): String {
        return if (playSounds) {
            "Play private message sounds"
        } else {
            "Don't play private message sounds"
        }
    }

    override fun <T> getValue(): T {
        return playSounds as T
    }

    override fun getAbstractType(): Type {
        return PrivateMessageSoundsOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is PrivateMessageSoundsOption && other.playSounds == playSounds
    }

    override fun hashCode(): Int {
        return playSounds.hashCode()
    }

}