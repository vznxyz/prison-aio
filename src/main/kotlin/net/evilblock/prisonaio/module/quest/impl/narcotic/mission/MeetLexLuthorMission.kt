package net.evilblock.prisonaio.module.quest.impl.narcotic.mission

import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest
import net.evilblock.prisonaio.module.quest.impl.narcotic.dialogue.MeetLexLuthorDialogueSequence
import net.evilblock.prisonaio.module.quest.impl.narcotic.entity.LexLuthorNpcEntity
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MeetLexLuthorMission : QuestMission<NarcoticsQuest>, Listener {

    override fun getQuest(): NarcoticsQuest {
        return NarcoticsQuest
    }

    override fun getId(): String {
        return "meet-lex-luthor"
    }

    override fun getName(): String {
        return "Meet Lex Luthor"
    }

    override fun getMissionText(player: Player): String {
        return "Meet Lex Luthor near the Outdoors Tavern (78, 51, -29)."
    }

    override fun getOrder(): Int {
        return 0
    }

    @EventHandler
    fun onPlayerRightClickEntityEvent(event: PlayerRightClickEntityEvent) {
        if (event.entity is LexLuthorNpcEntity) {
            val progress = getQuest().getProgress(event.player)
            if (progress.isCompleted()) {
                return
            }

            if (!progress.hasStarted()) {
                ConfirmMenu("Start Quest: ${NarcoticsQuest.getName()}") { confirmed ->
                    if (confirmed) {
                        NarcoticsQuest.onStartQuest(event.player)

                        PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                            QuestHandler.startDialogueSequence(event.player, MeetLexLuthorDialogueSequence(event.player.uniqueId)) {
                                NarcoticsQuest.onCompleteMission(event.player, MeetLexLuthorMission)
                            }
                        }, 50L)
                    } else {
                        event.player.sendMessage("${ChatColor.RED}Maybe I should come back when I'm done being shy...")
                    }
                }.openMenu(event.player)
            }
        }
    }

}