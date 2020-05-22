package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import net.evilblock.prisonaio.module.user.setting.chat.ChatMode
import java.lang.reflect.Type

class ChatModeSettingOption(private val chatMode: ChatMode) : UserSettingOption {

    override fun getName(): String {
        return chatMode.getDisplayName()
    }

    override fun <T> getValue(): T {
        return chatMode as T
    }

    override fun getAbstractType(): Type {
        return ChatModeSettingOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is ChatModeSettingOption && other.chatMode == chatMode
    }

    override fun hashCode(): Int {
        return chatMode.hashCode()
    }

}