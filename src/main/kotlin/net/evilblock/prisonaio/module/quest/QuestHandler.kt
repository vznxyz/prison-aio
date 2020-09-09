/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.quest.dialogue.DialoguePlayer
import net.evilblock.prisonaio.module.quest.dialogue.DialogueSequence
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object QuestHandler : PluginHandler {

    private val quests = arrayListOf<Quest<*>>()

    override fun getModule(): PluginModule {
        return QuestsModule
    }

    override fun initialLoad() {
        for (quest in quests) {
            for (mission in quest.getSortedMissions()) {
                if (mission is Listener) {
                    getModule().getPluginFramework().server.pluginManager.registerEvents(mission, getModule().getPluginFramework())
                }
            }
        }
    }

    fun getQuests(): List<Quest<*>> {
        return quests.toList()
    }

    fun getQuestById(id: String): Quest<*>? {
        return quests.firstOrNull { it.getId() == id }
    }

    fun isDialogueSequencePlaying(player: Player): Boolean {
        return player.hasMetadata("QUEST_DIALOGUE_TICKER")
                && player.hasMetadata("QUEST_DIALOGUE_PLAYER")
                && player.hasMetadata("QUEST_CALLBACK")
    }

    fun startDialogueSequence(player: Player, sequence: DialogueSequence, onComplete: () -> Unit) {
        if (isDialogueSequencePlaying(player)) {
            throw IllegalStateException("A dialogue sequence is already playing")
        }

        val dialoguePlayer = DialoguePlayer(player, sequence)

        val ticker = object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    stopDialogueSequence(player)
                    return
                }

                if (dialoguePlayer.isOnCooldown()) {
                    return
                }

                if (dialoguePlayer.hasNext()) {
                    if (dialoguePlayer.canSendNext(player)) {
                        dialoguePlayer.sendNext(player)
                    } else {
                        stopDialogueSequence(player)
                    }
                } else {
                    stopDialogueSequence(player)
                }
            }
        }.runTaskTimerAsynchronously(PrisonAIO.instance, 1L, 1L)

        player.setMetadata("QUEST_DIALOGUE_TICKER", FixedMetadataValue(PrisonAIO.instance, ticker))
        player.setMetadata("QUEST_DIALOGUE_PLAYER", FixedMetadataValue(PrisonAIO.instance, dialoguePlayer))
        player.setMetadata("QUEST_CALLBACK", FixedMetadataValue(PrisonAIO.instance, onComplete))
    }

    fun stopDialogueSequence(player: Player) {
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

        val dialogueCallback = player.getMetadata("QUEST_CALLBACK").first().value() as () -> Unit
        dialogueCallback.invoke()

        player.removeMetadata("QUEST_DIALOGUE_TICKER", PrisonAIO.instance)
        player.removeMetadata("QUEST_DIALOGUE_PLAYER", PrisonAIO.instance)
        player.removeMetadata("QUEST_CALLBACK", PrisonAIO.instance)
    }

}