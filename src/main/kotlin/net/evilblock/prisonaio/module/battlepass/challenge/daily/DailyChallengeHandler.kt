/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.daily

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.battlepass.BattlePassModule
import java.io.File

object DailyChallengeHandler : PluginHandler {

    private var currentSession: DailyChallengeSession = DailyChallengeSession()

    override fun getModule(): PluginModule {
        return BattlePassModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "battle-pass-daily-challenges.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                currentSession = Cubed.gson.fromJson(reader, DATA_TYPE) as DailyChallengeSession
            }
        }

        Tasks.asyncTimer(20L * 60L, 20L * 60L) {
            if (currentSession.hasExpired()) {
                newSession()
                saveData()
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(currentSession, DATA_TYPE), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getSession(): DailyChallengeSession {
        return currentSession
    }

    fun newSession() {
        currentSession.clear()
        currentSession = DailyChallengeSession()

        PrisonAIO.instance.systemLog("Generated new Daily Challenges session!")
    }

    private val DATA_TYPE = object : TypeToken<DailyChallengeSession>() {}.type

}