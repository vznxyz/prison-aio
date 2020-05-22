package net.evilblock.prisonaio.module.quest

import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest
import net.evilblock.prisonaio.module.quest.progression.QuestProgression
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

interface Quest<T : Quest<T>> {

    fun getId(): String

    fun getName(): String

    fun getSortedMissions(): List<QuestMission<T>>

    fun getMissionById(id: String): QuestMission<T>? {
        for (mission in getSortedMissions()) {
            if (mission.getId() == id) {
                return mission
            }
        }
        return null
    }

    fun onStartQuest(player: Player) {
        val progress = getProgress(player)
        if (progress.hasStarted()) {
            throw IllegalStateException("Quest already started")
        }

        progress.start()

        player.sendMessage("")
        player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}Quest Started! ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${NarcoticsQuest.getName()}${ChatColor.GRAY})")
        player.sendMessage(" ${ChatColor.GRAY}Your quest has begun. If you need help throughout the")
        player.sendMessage(" ${ChatColor.GRAY}quest, use the /quest help command.")
        player.sendMessage("")
    }

    fun onCompleteQuest(player: Player) {
        player.sendMessage("")
        player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Quest Complete! ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${NarcoticsQuest.getName()}${ChatColor.GRAY})")

        val completionText = " Congratulations on completing your quest. " + getCompletionText()

        for (line in TextSplitter.split(52, completionText, "${ChatColor.GRAY}", " ")) {
            player.sendMessage(line)
        }

        if (hasRewards()) {
            player.sendMessage("")
            player.sendMessage(" ${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Quest Rewards")

            for (rewardText in getRewardsText()) {
                player.sendMessage("  $rewardText")
            }
        }

        player.sendMessage("")
    }

    fun onCompleteMission(player: Player, mission: QuestMission<T>) {
        val progress = getProgress(player)
        progress.markMissionCompleted(mission)
        progress.requiresSave = true

        if (progress.hasCurrentMission()) {
            val newMission = progress.getCurrentMission()

            player.sendMessage("")
            player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}New Quest Mission ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${newMission.getName()}${ChatColor.GRAY})")
            player.sendMessage(" ${ChatColor.GRAY}${newMission.getMissionText(player)}")
            player.sendMessage("")
        }

        if (mission == getLastMission()) {
            onCompleteQuest(player)
        }
    }

    fun getFirstMission(): QuestMission<T> {
        return getSortedMissions().first()
    }

    fun getLastMission(): QuestMission<T> {
        return getSortedMissions().last()
    }

    fun startProgress(): QuestProgression

    fun getProgress(player: Player): QuestProgression {
        val user = UserHandler.getUser(player.uniqueId)
        return user.getQuestProgression(this)
    }

    fun getCompletionText(): String {
        return ""
    }

    fun getStartText(): String

    fun hasRewards(): Boolean {
        return false
    }

    fun getRewardsText(): List<String> {
        return emptyList()
    }

}