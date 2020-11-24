/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.config

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.minigame.MinigamesModule
import org.bukkit.ChatColor
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

object EventConfigHandler : PluginHandler {

    private val dataFile: File = File(File(PrisonAIO.instance.dataFolder, "internal"), "events-config.json")
    private val dataType: Type = object : TypeToken<EventConfig>() {}.type

    lateinit var config: EventConfig

    override fun getModule(): PluginModule {
        return MinigamesModule
    }

    override fun initialLoad() {
        super.initialLoad()

        if (dataFile.exists()) {
            try {
                Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                    config = Cubed.gson.fromJson(reader, dataType) as EventConfig
                }
            } catch (e: IOException) {
                e.printStackTrace()
                PrisonAIO.instance.logger.severe(ChatColor.RED.toString() + "Failed to load events-config.json!")
            }
        } else {
            config = EventConfig()
        }
    }

    override fun saveData() {
        try {
            Files.write(Cubed.gson.toJson(config, dataType), dataFile, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            PrisonAIO.instance.logger.severe(ChatColor.RED.toString() + "Failed to save events-config.json!")
        }
    }

}