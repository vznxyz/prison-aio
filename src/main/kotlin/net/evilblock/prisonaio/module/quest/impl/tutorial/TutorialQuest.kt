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
import net.evilblock.prisonaio.module.quest.impl.tutorial.command.SpawnTutorialGuideCommand
import net.evilblock.prisonaio.module.quest.impl.tutorial.command.TutorialQuestConfigCommands
import net.evilblock.prisonaio.module.quest.impl.tutorial.dialogue.TutorialStartDialogue
import net.evilblock.prisonaio.module.quest.impl.tutorial.mission.FollowGuideMission
import net.evilblock.prisonaio.module.quest.impl.tutorial.progress.TutorialProgress
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import net.evilblock.prisonaio.module.quest.progress.QuestProgress
import net.evilblock.prisonaio.module.region.RegionHandler
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

    override fun getId(): String {
        return "tutorial"
    }

    override fun getName(): String {
        return "Tutorial"
    }

    override fun getSortedMissions(): List<QuestMission> {
        return listOf(
            FollowGuideMission
        ).sortedBy { it.getOrder() }
    }

    override fun startProgress(): QuestProgress {
        return TutorialProgress()
    }

    override fun onStartQuest(player: Player) {
        super.onStartQuest(player)

        QuestHandler.startDialogueSequence(player, TutorialStartDialogue(player.uniqueId)) {
            onCompleteMission(player, FollowGuideMission)
        }
    }

    override fun getStartText(): String {
        val region = RegionHandler.findRegion(getStartLocation())
        return "You can start this quest by talking to the Tutorial Guide located at ${region.getRegionName()} (${formatLocation(getStartLocation())})."
    }

    override fun getStartLocation(): Location {
        return config.startLocation!!
    }

    override fun getCompletionText(): String {
        return "You've completed the tutorial and ready to start roaming the prison freely. If you ever need to re-visit the tutorial, type \"/tutorial\"!"
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
//            TutorialCommand::class.java,
            SpawnTutorialGuideCommand::class.java,
            TutorialQuestConfigCommands.javaClass
        )
    }

}