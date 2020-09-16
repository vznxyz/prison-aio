/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.sentient

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.system.SystemModule
import net.evilblock.prisonaio.module.system.sentient.guard.PrisonGuardLogic
import org.bukkit.ChatColor
import java.io.File

object SentientHandler : PluginHandler {

    private val DATA_TYPE = object : TypeToken<SentientConfiguration>() {}.type

    var configuration: SentientConfiguration = SentientConfiguration()

    override fun getModule(): PluginModule {
        return SystemModule
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "sentient-configuration.json")
    }

    override fun initialLoad() {
        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                configuration = Cubed.gson.fromJson(reader, DATA_TYPE) as SentientConfiguration
            }
        }

        Tasks.asyncTimer(PrisonGuardLogic, 20L, 20L)
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(configuration, DATA_TYPE), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getPrisonGuardName(): String {
        return ChatColor.translateAlternateColorCodes('&', SystemModule.config.getString("prison-guard.name"))
    }

    fun getPrisonGuardTextureValue(): String {
        return SystemModule.config.getString("prison-guard.texture.value")
    }

    fun getPrisonGuardTextureSignature(): String {
        return SystemModule.config.getString("prison-guard.texture.signature")
    }

    fun getRandomPrisonGuardPhrase(): List<String> {
        val phrases = SystemModule.config.getList("prison-guard.phrases").map { (it as Map<String, Any>)["lines"] as List<String> }
        if (phrases.size == 1) {
            return phrases.first().map { ChatColor.translateAlternateColorCodes('&', it) }
        }

        return phrases.random().map { ChatColor.translateAlternateColorCodes('&', it) }
    }

}