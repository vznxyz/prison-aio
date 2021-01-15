/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.koth

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule

object KOTHHandler : PluginHandler() {

    private val activeKoth: KOTHEvent? = null

    override fun getModule(): PluginModule {
        return KOTHModule
    }

    fun isActive(): Boolean {
        return activeKoth != null
    }

    fun getActiveEvent(): KOTHEvent? {
        return activeKoth
    }

}