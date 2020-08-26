package net.evilblock.prisonaio.module.minigame.event

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.Location
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

object EventConfig {

    private val dataFile: File = File(PrisonAIO.instance.dataFolder, "events-config.json")
    private val dataType: Type = object : TypeToken<EventConfig>() {}.type

    var lobbyLocation: Location? = null

    fun load() {
        if (dataFile.exists()) {
            try {
                Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                    val config = Cubed.gson.fromJson(reader, dataType) as EventConfig
                    lobbyLocation = config.lobbyLocation
                }
            } catch (e: IOException) {
                e.printStackTrace()
                PrisonAIO.instance.logger.severe(ChatColor.RED.toString() + "Failed to load events-config.json!")
            }
        }
    }

    fun save() {
        try {
            Files.write(Cubed.gson.toJson(this, dataType), dataFile, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            PrisonAIO.instance.logger.severe(ChatColor.RED.toString() + "Failed to save events-config.json!")
        }
    }

}