package net.evilblock.prisonaio.module.quest.impl.narcotic.mission

import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest
import net.evilblock.prisonaio.module.quest.impl.narcotic.dialogue.MeetPabloEscobarDialogueSequence
import net.evilblock.prisonaio.module.quest.impl.narcotic.entity.PabloEscobarNpcEntity
import net.evilblock.prisonaio.module.quest.impl.narcotic.menu.NarcoticsSupplierMenu
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MeetPabloEscobarMission : QuestMission<NarcoticsQuest>, Listener {

    override fun getQuest(): NarcoticsQuest {
        return NarcoticsQuest
    }

    override fun getId(): String {
        return "meet-pablo-escobar"
    }

    override fun getName(): String {
        return "Meet Pablo"
    }

    override fun getMissionText(player: Player): String {
        return "Meet Pablo, who is located near the Basketball Court (-97, 56, -59). He will have Lex Luthor's `stuff` for you to transport to another location."
    }

    override fun getOrder(): Int {
        return 1
    }

    @EventHandler
    fun onPlayerRightClickEntityEvent(event: PlayerRightClickEntityEvent) {
        if (event.entity is PabloEscobarNpcEntity) {
            val progress = NarcoticsQuest.getProgress(event.player)
            if (progress.isCompleted()) {
                NarcoticsSupplierMenu().openMenu(event.player)
                return
            }

            if (!progress.hasStarted()) {
                event.player.sendMessage("${ChatColor.RED}Do I know you? Leave me alone!")
                return
            }

            if (progress.getCurrentMission() == MeetPabloEscobarMission) {
                if (QuestHandler.isDialogueSequencePlaying(event.player)) {
                    return
                }

                QuestHandler.startDialogueSequence(event.player, MeetPabloEscobarDialogueSequence(event.player.uniqueId)) {
                    NarcoticsQuest.onCompleteMission(event.player, MeetPabloEscobarMission)
                }
            }
        }
    }

}