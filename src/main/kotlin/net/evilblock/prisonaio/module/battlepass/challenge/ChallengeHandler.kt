package net.evilblock.prisonaio.module.battlepass.challenge

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.battlepass.BattlePassModule
import net.evilblock.prisonaio.module.battlepass.challenge.impl.*
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import java.io.File

object ChallengeHandler : PluginHandler {

    @JvmStatic
    val CHALLENGE_TYPES: MutableList<ChallengeType> = arrayListOf(
        BlocksMinedChallenge.BlocksMinedChallengeType,
        BlocksMinedAtMineChallenge.BlocksMinedAtMineChallengeType,
        PlayTimeChallenge.PlayTimeChallengeChallengeType,
        PrestigeChallenge.PrestigeRequirementType,
        ExecuteCommandChallenge.ExecuteCommandChallengeType
    )

    private val DATA_TYPE = object : TypeToken<List<Challenge>>() {}.type

    private val challenges: MutableMap<String, Challenge> = hashMapOf()

    override fun getModule(): PluginModule {
        return BattlePassModule
    }

    override fun hasInternalData(): Boolean {
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
                    challenges[challenge.id.toLowerCase()] = challenge
                }
            }
        }
    }

    override fun saveData() {
        Files.write(Cubed.gson.toJson(challenges.values, DATA_TYPE), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getChallenges(): List<Challenge> {
        return challenges.values.toList()
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

    fun generateDailyChallenges(user: User) {
        val dailyChallenges = arrayListOf<Challenge>()
        dailyChallenges.add(BlocksMinedChallenge("daily-mine-5k-blocks", "${ChatColor.GOLD}${ChatColor.BOLD}Mine 5,000 Blocks", 5000, 5))
        dailyChallenges.add(BlocksMinedChallenge("daily-mine-10k-blocks", "${ChatColor.GOLD}${ChatColor.BOLD}Mine 10,000 Blocks", 10000, 10))
        dailyChallenges.add(BlocksMinedAtMineChallenge("daily-mine-10k-blocks-at-h", "${ChatColor.GOLD}${ChatColor.BOLD}Mine 10,000 Blocks", MineHandler.getMines().random(), 10000, 10))
        dailyChallenges.add(BlocksMinedAtMineChallenge("daily-mine-10k-blocks-at-h", "${ChatColor.GOLD}${ChatColor.BOLD}Mine 10,000 Blocks", MineHandler.getMines().random(), 10000, 10))
    }

}