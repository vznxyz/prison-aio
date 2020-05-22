package net.evilblock.prisonaio.module.quest.impl.narcotic.mission

import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest
import net.evilblock.prisonaio.module.quest.impl.narcotic.dialogue.DeliverDrugsDialogueSequence
import net.evilblock.prisonaio.module.quest.impl.narcotic.entity.DrugDealerNpcEntity
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object DeliverDrugsMission : QuestMission<NarcoticsQuest>, Listener {

    override fun getQuest(): NarcoticsQuest {
        return NarcoticsQuest
    }

    override fun getId(): String {
        return "deliver-drugs"
    }

    override fun getName(): String {
        return "Deliver The Drugs"
    }

    override fun getMissionText(player: Player): String {
        val progress = getQuest().getProgress(player)
        return "Pablo has given you the drugs to deliver to Lex Luthor's network of drug dealers, which can be found around the prison. You have delivered to ${ChatColor.GREEN}${ChatColor.BOLD}${progress.getDelivered()}${ChatColor.GRAY}/5 dealers. If you lose the drugs, you will need to re-visit Pablo and purchase more."
    }

    override fun getOrder(): Int {
        return 2
    }

    @EventHandler
    fun onPlayerRightClickEntityEvent(event: PlayerRightClickEntityEvent) {
        if (event.entity is DrugDealerNpcEntity) {
            val drugDealer = (event.entity as DrugDealerNpcEntity)

            val progress = getQuest().getProgress(event.player)
            if (progress.isCompleted()) {
                return
            }

            if (!progress.hasStarted()) {
                event.player.sendMessage("${ChatColor.RED}Do I know you? Leave me alone!")
                return
            }

            // the player is eligible to make this npc sell for them
            if (progress.getCurrentMission() == DeliverDrugsMission && progress.getDelivered() < 5) {
                // prevent the player from selling to this npc more than once
                if (progress.hasDeliveredTo(drugDealer.npcId)) {
                    event.player.sendMessage("${QuestsModule.getNpcName((event.entity as DrugDealerNpcEntity).npcId)} ${ChatColor.RED}doesn't have time to talk right now.")
                    return
                }

                // already in a dialogue sequence
                if (QuestHandler.isDialogueSequencePlaying(event.player)) {
                    return
                }

                QuestHandler.startDialogueSequence(event.player, DeliverDrugsDialogueSequence(drugDealer.npcId)) {
                    if (progress.getDelivered() >= 5) {
                        NarcoticsQuest.onCompleteMission(event.player, DeliverDrugsMission)
                    }

                    progress.requiresSave = true
                }
            }
        }
    }

}