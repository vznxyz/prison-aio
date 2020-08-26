/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class ChatModeOption(private val chatMode: ChatMode) : UserSettingOption {

    override fun getName(): String {
        return chatMode.getDisplayName()
    }

    override fun <T> getValue(): T {
        return chatMode as T
    }

    override fun getAbstractType(): Type {
        return ChatModeOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is ChatModeOption && other.chatMode == chatMode
    }

    override fun hashCode(): Int {
        return chatMode.hashCode()
    }

    enum class ChatMode(private val displayName: String) {
        HIDDEN("Hidden chat"),
        SYSTEM_CHAT("System chat"),
        GLOBAL_CHAT("Global chat");

        fun getDisplayName(): String {
            return displayName
        }
    }

}