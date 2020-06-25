/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class PluginEvent : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    fun call() {
        Bukkit.getPluginManager().callEvent(this)
    }

}