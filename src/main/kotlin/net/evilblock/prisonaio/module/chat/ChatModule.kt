package net.evilblock.prisonaio.module.chat

import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.chat.listener.ChatFormatListeners
import org.bukkit.event.Listener

object ChatModule : PluginModule() {

    override fun getName(): String {
        return "Chat"
    }

    override fun getConfigFileName(): String {
        return "chat"
    }

    override fun getListeners(): List<Listener> {
        return arrayListOf(ChatFormatListeners)
    }

}