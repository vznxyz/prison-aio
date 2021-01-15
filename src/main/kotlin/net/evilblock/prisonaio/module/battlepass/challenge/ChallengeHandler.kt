/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.battlepass.BattlePassModule
import net.evilblock.prisonaio.module.battlepass.challenge.impl.*
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object ChallengeHandler : PluginHandler() {

    @JvmStatic
    val CHALLENGE_TYPES: MutableList<ChallengeType> = arrayListOf(
        BlocksMinedChallenge.BlocksMinedChallengeType,
        BlocksMinedAtMineChallenge.BlocksMinedAtMineChallengeType,
        PlayTimeChallenge.PlayTimeChallengeChallengeType,
        PrestigeChallenge.PrestigeRequirementType,
        KillsChallenge.KillsChallengeType,
        ExecuteCommandChallenge.ExecuteCommandChallengeType
    )

    private val DATA_TYPE = object : TypeToken<List<Challenge>>() {}.type

    private val challenges: MutableMap<String, Challenge> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return BattlePassModule
    }

    override fun hasDefaultInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "battle-pass-challenges.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val list = Cubed.gson.fromJson(reader, DATA_TYPE) as List<Challenge>

                for (challenge in list) {
                    if (challenge.isSetup()) {
                        trackChallenge(challenge)
                    }
                }
            }
        }

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(challenges.values, DATA_TYPE), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getAllChallenges(): Collection<Challenge> {
        return arrayListOf<Challenge>().also { challenges ->
            challenges.addAll(getChallenges())
            challenges.addAll(DailyChallengeHandler.getSession().getChallenges())
        }
    }

    fun getChallenges(): Collection<Challenge> {
        return challenges.values
    }

    fun getChallengeById(id: String): Challenge? {
        return challenges[id]
    }

    fun trackChallenge(challenge: Challenge) {
        challenges[challenge.id.toLowerCase()] = challenge
    }

    fun forgetChallenge(challenge: Challenge) {
        challenges.remove(challenge.id.toLowerCase())
    }

}