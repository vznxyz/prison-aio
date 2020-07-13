/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.chat

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.chat.listener.ChatFormatListeners
import org.bukkit.event.Listener

object ChatModule : PluginModule() {

    override fun getName(): String {
        return "Chat"
    }

    override fun getConfigFileName(): String {
        return "chat"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun getListeners(): List<Listener> {
        return arrayListOf(ChatFormatListeners)
    }

}