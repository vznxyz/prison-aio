/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.dialogue.reason.DialogueEndReason
import net.evilblock.prisonaio.module.quest.impl.tutorial.command.SpawnTutorialGuideCommand
import net.evilblock.prisonaio.module.quest.impl.tutorial.command.TutorialQuestConfigCommands
import net.evilblock.prisonaio.module.quest.impl.tutorial.dialogue.TutorialDialogue
import net.evilblock.prisonaio.module.quest.impl.tutorial.entity.PersonalTutorialGuide
import net.evilblock.prisonaio.module.quest.impl.tutorial.mission.TutorialMission
import net.evilblock.prisonaio.module.quest.impl.tutorial.progress.TutorialProgress
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import net.evilblock.prisonaio.module.quest.progress.QuestProgress
import net.evilblock.prisonaio.module.region.RegionHandler
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File

object TutorialQuest : Quest {

    private lateinit var configFile: File
    internal lateinit var config: TutorialQuestConfig

    override fun initializeData() {
        configFile = File(File(PrisonAIO.instance.dataFolder, "quests"), "tutorial.json")
        configFile.parentFile.mkdirs()

        if (configFile.exists()) {
            Files.newReader(configFile, Charsets.UTF_8).use { reader ->
                config = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<TutorialQuestConfig>() {}.type) as TutorialQuestConfig
            }
        } else {
            config = TutorialQuestConfig()
        }
    }

    override fun saveData() {
        Files.write(Cubed.gson.toJson(config, object : TypeToken<TutorialQuestConfig>() {}.type), configFile, Charsets.UTF_8)
    }

    override fun getId(): String {
        return "tutorial"
    }

    override fun getName(): String {
        return "Tutorial"
    }

    override fun getSortedMissions(): List<QuestMission> {
        return listOf(
            TutorialMission
        ).sortedBy { it.getOrder() }
    }

    override fun startProgress(): QuestProgress {
        return TutorialProgress()
    }

    override fun getProgress(player: Player): TutorialProgress {
        return super.getProgress(player) as TutorialProgress
    }

    override fun onStartQuest(player: Player) {
        super.onStartQuest(player)

        val guide = PersonalTutorialGuide(location = getStartLocation(), player = player)
        guide.initializeData()
        guide.spawn(player)

        QuestHandler.addMissionEntity(player, guide)

        QuestHandler.startDialogueSequence(player, TutorialDialogue(player.uniqueId, guide)) { endReason ->
            QuestHandler.removeMissionEntity(player, guide)

            if (endReason == DialogueEndReason.FINISHED) {
                onCompleteMission(player, TutorialMission)
            }
        }
    }

    override fun getStartText(): String {
        val region = RegionHandler.findRegion(getStartLocation())
        return "You can start this quest by talking to the Tutorial Guide located at ${region.getRegionName()} ${ChatColor.GRAY}(${formatLocation(getStartLocation())}${ChatColor.GRAY})."
    }

    override fun getStartLocation(): Location {
        return config.startLocation!!
    }

    override fun getCompletionText(): String {
        return "You've completed the tutorial and are ready to start roaming the prison freely! You can always come back and restart the tutorial if you feel you've missed something."
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
//            TutorialCommand::class.java,
            SpawnTutorialGuideCommand::class.java,
            TutorialQuestConfigCommands.javaClass
        )
    }

}