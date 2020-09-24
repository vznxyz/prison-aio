/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.quest.dialogue.DialoguePlayer
import net.evilblock.prisonaio.module.quest.dialogue.DialogueSequence
import net.evilblock.prisonaio.module.quest.dialogue.reason.DialogueEndReason
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

object QuestHandler : PluginHandler {

    private val quests = arrayListOf<Quest>(TutorialQuest)
    private val missionEntities: MutableMap<UUID, MutableList<Entity>> = hashMapOf()

    override fun getModule(): PluginModule {
        return QuestsModule
    }

    override fun initialLoad() {
        for (quest in quests) {
            quest.initializeData()

            for (mission in quest.getSortedMissions()) {
                if (mission is Listener) {
                    getModule().getPluginFramework().server.pluginManager.registerEvents(mission, getModule().getPluginFramework())
                }
            }
        }
    }

    fun getQuests(): List<Quest> {
        return quests.toList()
    }

    fun getQuestById(id: String): Quest? {
        return quests.firstOrNull { it.getId() == id }
    }

    fun getMissionEntities(player: Player): MutableList<Entity> {
        return missionEntities.getOrDefault(player.uniqueId, arrayListOf())
    }

    fun addMissionEntity(player: Player, entity: Entity) {
        missionEntities.putIfAbsent(player.uniqueId, arrayListOf())
        missionEntities[player.uniqueId]!!.add(entity)
        EntityManager.trackEntity(entity)
    }

    fun removeMissionEntity(player: Player, entity: Entity) {
        missionEntities.getOrDefault(player.uniqueId, arrayListOf()).remove(entity)
        EntityManager.forgetEntity(entity)
        entity.destroyForCurrentWatchers()
    }

    fun clearMissionEntities(player: Player) {
        val entities = missionEntities.remove(player.uniqueId)
        if (entities != null) {
            for (entity in entities) {
                EntityManager.forgetEntity(entity)
                entity.destroyForCurrentWatchers()
            }
        }
    }

    fun isDialogueSequencePlaying(player: Player): Boolean {
        return player.hasMetadata("QUEST_DIALOGUE_TICKER")
                && player.hasMetadata("QUEST_DIALOGUE_PLAYER")
                && player.hasMetadata("QUEST_DIALOGUE_CALLBACK")
    }

    fun startDialogueSequence(player: Player, sequence: DialogueSequence, onComplete: (DialogueEndReason) -> Unit) {
        if (isDialogueSequencePlaying(player)) {
            throw IllegalStateException("A dialogue sequence is already playing")
        }

        val dialoguePlayer = DialoguePlayer(player, sequence)

        val ticker = Tasks.asyncTimer(object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    stopDialogueSequence(player, DialogueEndReason.DISCONNECTED)
                    return
                }

                if (dialoguePlayer.hasNext()) {
                    if (dialoguePlayer.isSleeping()) {
                        return
                    }

                    if (dialoguePlayer.isReady(player)) {
                        dialoguePlayer.send(player)
                    }
                } else {
                    val lastDialogue = dialoguePlayer.getLast()
                    if (lastDialogue.useState) {
                        if (lastDialogue.complete) {
                            stopDialogueSequence(player, DialogueEndReason.FINISHED)
                        }
                    } else {
                        stopDialogueSequence(player, DialogueEndReason.FINISHED)
                    }
                }
            }
        }, 1L, 1L)

        player.setMetadata("QUEST_DIALOGUE_TICKER", FixedMetadataValue(PrisonAIO.instance, ticker))
        player.setMetadata("QUEST_DIALOGUE_PLAYER", FixedMetadataValue(PrisonAIO.instance, dialoguePlayer))
        player.setMetadata("QUEST_DIALOGUE_CALLBACK", FixedMetadataValue(PrisonAIO.instance, onComplete))
    }

    fun stopDialogueSequence(player: Player, reason: DialogueEndReason) {
        if (!isDialogueSequencePlaying(player)) {
            throw IllegalStateException("A dialogue sequence is not playing")
        }

        val dialogueTicker = player.getMetadata("QUEST_DIALOGUE_TICKER").first().value() as BukkitTask
        if (!dialogueTicker.isCancelled) {
            dialogueTicker.cancel()
        }

        val dialogueSequence = player.getMetadata("QUEST_DIALOGUE_PLAYER").first().value() as DialoguePlayer
        if (dialogueSequence.canAllBeSent(player)) {
            for (dialogue in dialogueSequence.getDialoguesForSkip()) {
                dialogue.send(player)

                if (dialogue.isSpaced()) {
                    player.sendMessage("")
                }
            }
        }

        (player.getMetadata("QUEST_DIALOGUE_CALLBACK").first().value() as (DialogueEndReason) -> Unit).invoke(reason)

        player.removeMetadata("QUEST_DIALOGUE_TICKER", PrisonAIO.instance)
        player.removeMetadata("QUEST_DIALOGUE_PLAYER", PrisonAIO.instance)
        player.removeMetadata("QUEST_DIALOGUE_CALLBACK", PrisonAIO.instance)
    }

}