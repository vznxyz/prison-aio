package net.evilblock.prisonaio.module.user.setting.chat

enum class ChatMode(private val displayName: String) {

    HIDDEN("Hidden chat"),
    SYSTEM_CHAT("System chat"),
    GLOBAL_CHAT("Global chat");

    fun getDisplayName(): String {
        return displayName
    }

}