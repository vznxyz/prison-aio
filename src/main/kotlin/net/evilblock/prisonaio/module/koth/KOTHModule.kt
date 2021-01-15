/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.koth

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.koth.listener.KOTHListeners
import net.evilblock.prisonaio.module.koth.service.KOTHTickService
import net.evilblock.prisonaio.service.ServiceRegistry
import org.bukkit.event.Listener

object KOTHModule : PluginModule() {

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun getName(): String {
        return "KoTH"
    }

    override fun getConfigFileName(): String {
        return "koths"
    }

    override fun onEnable() {
        super.onEnable()

        KOTHHandler.initialLoad()

        ServiceRegistry.register(KOTHTickService, 10L, 10L)
    }

    override fun onDisable() {
        super.onDisable()

        KOTHHandler.saveData()
    }

    override fun onAutoSave() {
        super.onAutoSave()

        KOTHHandler.saveData()
    }

    override fun getListeners(): List<Listener> {
        return listOf(KOTHListeners)
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(

        )
    }

}