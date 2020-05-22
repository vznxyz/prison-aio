package net.evilblock.prisonaio.module.quest.impl.narcotic.mission

import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest
import net.evilblock.prisonaio.module.quest.impl.narcotic.dialogue.DeliverMoneyDialogueSequence
import net.evilblock.prisonaio.module.quest.impl.narcotic.entity.PabloEscobarNpcEntity
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object DeliverMoneyMission : QuestMission<NarcoticsQuest>, Listener {

    override fun getQuest(): NarcoticsQuest {
        return NarcoticsQuest
    }

    override fun getId(): String {
        return "deliver-money"
    }

    override fun getName(): String {
        return "Deliver the Money"
    }

    override fun getMissionText(player: Player): String {
        return "You've exchanged the drugs with all of the dealers. Now you need to return to Pablo to give him his money."
    }

    override fun getOrder(): Int {
        return 3
    }

    @EventHandler
    fun onPlayerRightClickEntityEvent(event: PlayerRightClickEntityEvent) {
        if (event.entity is PabloEscobarNpcEntity) {
            val progress = getQuest().getProgress(event.player)
            if (progress.isCompleted() || !progress.hasStarted()) {
                return
            }

            if (progress.getCurrentMission() == DeliverMoneyMission) {
                // already in a dialogue sequence
                if (QuestHandler.isDialogueSequencePlaying(event.player)) {
                    return
                }

                QuestHandler.startDialogueSequence(event.player, DeliverMoneyDialogueSequence(event.player.uniqueId)) {
                    NarcoticsQuest.onCompleteMission(event.player, DeliverMoneyMission)
                }
            }
        }
    }

}