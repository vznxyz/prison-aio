/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import net.evilblock.prisonaio.module.quest.progress.QuestProgress
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player

interface Quest {

    fun initializeData() {

    }

    fun saveData() {

    }

    fun getId(): String

    fun getName(): String

    fun getSortedMissions(): List<QuestMission>

    fun getMissionById(id: String): QuestMission? {
        for (mission in getSortedMissions()) {
            if (mission.getId() == id) {
                return mission
            }
        }
        return null
    }

    fun onStartQuest(player: Player) {
        getProgress(player).start()

        val firstMission = getFirstMission()

        player.sendMessage("")
        player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}New Mission ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${firstMission.getName()}${ChatColor.GRAY})")

        TextSplitter.split(length = 50, text = firstMission.getMissionText(player), linePrefix = " ${ChatColor.GRAY}").forEach { player.sendMessage(it) }

        player.sendMessage("")
    }

    fun onCompleteQuest(player: Player) {
        val progress = getProgress(player)
        progress.complete()

        player.sendMessage("")
        player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Quest Completed! ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${TutorialQuest.getName()}${ChatColor.GRAY})")

        for (line in TextSplitter.split(length = 50, text = getCompletionText(), linePrefix = "${ChatColor.GRAY} ")) {
            player.sendMessage(line)
        }

        if (hasRewards()) {
            player.sendMessage("")
            player.sendMessage(" ${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Quest Rewards")

            for (rewardText in getRewardsText()) {
                player.sendMessage(" ${ChatColor.GRAY}${Constants.DOT_SYMBOL} $rewardText")
            }
        }

        player.sendMessage("")
    }

    fun onCompleteMission(player: Player, mission: QuestMission) {
        val progress = getProgress(player)
        progress.markMissionCompleted(mission)
        progress.requiresSave = true

        if (progress.hasCurrentMission()) {
            val newMission = progress.getCurrentMission()

            player.sendMessage("")
            player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}New Mission ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}${newMission.getName()}${ChatColor.GRAY})")

            TextSplitter.split(length = 50, text = newMission.getMissionText(player), linePrefix = " ${ChatColor.GRAY}").forEach { player.sendMessage(it) }

            player.sendMessage("")
        }

        if (mission == getLastMission()) {
            onCompleteQuest(player)
        }
    }

    fun getFirstMission(): QuestMission {
        return getSortedMissions().first()
    }

    fun getLastMission(): QuestMission {
        return getSortedMissions().last()
    }

    fun startProgress(): QuestProgress

    fun getProgress(player: Player): QuestProgress {
        val user = UserHandler.getUser(player.uniqueId)
        return user.getQuestProgress(this)
    }

    fun getStartText(): String

    fun getStartLocation(): Location

    fun getCompletionText(): String {
        return ""
    }

    fun hasRewards(): Boolean {
        return false
    }

    fun getRewardsText(): List<String> {
        return emptyList()
    }

    fun getCommands(): List<Class<*>> {
        return listOf()
    }

    fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf()
    }

    fun formatLocation(location: Location): String {
        return "${ChatColor.AQUA}${location.blockX}, ${location.blockY}, ${location.blockZ}"
    }

}