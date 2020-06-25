/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.narcotic.dialogue

import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.DialogueSequence
import net.evilblock.prisonaio.module.quest.dialogue.impl.FunctionalDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.RequirementDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.ConversationDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.PlayerConversationIdentity
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.StaticConversationIdentity
import net.evilblock.prisonaio.module.quest.impl.narcotic.Narcotic
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest
import net.evilblock.prisonaio.module.quest.progression.QuestProgression
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import java.util.function.BiPredicate

class MeetPabloEscobarDialogueSequence(private val uuid: UUID) : DialogueSequence {

    override fun getSequence(player: Player): LinkedList<Dialogue> {
        val sequence = LinkedList<Dialogue>()

        val pabloEscobarIdentity = StaticConversationIdentity("${ChatColor.YELLOW}${ChatColor.BOLD}Pablo Escobar")
        val playerIdentity = PlayerConversationIdentity(uuid)

        sequence.add(ConversationDialogue(from = playerIdentity, to = pabloEscobarIdentity, message = "Pablo?", delay = 2000L))
        sequence.add(ConversationDialogue(from = pabloEscobarIdentity, to = playerIdentity, message = "Ah, yes! You must be the new drug runner. I have a shit ton of drugs that I'll need you to distribute to my dealers around the prison.", delay = 7000L))
        sequence.add(ConversationDialogue(from = pabloEscobarIdentity, to = playerIdentity, message = "Each dealer will take either 64 LBs of Marijuana for $1,000,000, or 64 LBs of Cocaine for $5,000,000.", delay = 6000L))
        sequence.add(ConversationDialogue(from = pabloEscobarIdentity, to = playerIdentity, message = "Be careful when walking through-out the prison. If you walk too close to a guard, you might be randomly stopped and searched. If you lose my drugs, you will pay for them.", delay = 6000L))
        sequence.add(ConversationDialogue(from = playerIdentity, to = pabloEscobarIdentity, message = "Alright, I understand.", delay = 3000L))

        val notEnoughSpaceDialogue = ConversationDialogue(
            from = pabloEscobarIdentity,
            to = playerIdentity,
            message = "It seems you don't have enough inventory space (5 slots) to carry my drugs, so how about you come back later."
        )

        sequence.add(RequirementDialogue(
            quest = NarcoticsQuest,
            predicate = hasEmptySlotsPredicate,
            failureDialogue = notEnoughSpaceDialogue,
            delay = 0L
        ))

        sequence.add(ConversationDialogue(
            from = pabloEscobarIdentity,
            to = playerIdentity,
            message = "And it looks like you have enough inventory space to carry my drugs, so here they are...",
            delay = 5000L
        ))

        sequence.add(FunctionalDialogue(
            function = { player ->
                player.inventory.addItem(Narcotic.MARIJUANA.toItemStack(64))
                player.inventory.addItem(Narcotic.MARIJUANA.toItemStack(64))
                player.inventory.addItem(Narcotic.MARIJUANA.toItemStack(64))
                player.updateInventory()

                player.sendMessage("${pabloEscobarIdentity.getName()} ${ChatColor.GRAY}hands you ${ChatColor.GREEN}${ChatColor.BOLD}${Narcotic.MARIJUANA.displayName} ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}192 LB${ChatColor.GRAY})")
            }
        ))

        sequence.add(FunctionalDialogue(
            function = { player ->
                player.inventory.addItem(Narcotic.COCAINE.toItemStack(64))
                player.inventory.addItem(Narcotic.COCAINE.toItemStack(64))
                player.updateInventory()

                player.sendMessage("${pabloEscobarIdentity.getName()} ${ChatColor.GRAY}hands you ${ChatColor.GREEN}${ChatColor.BOLD}${Narcotic.COCAINE.displayName} ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}128 LB${ChatColor.GRAY})")
            },
            delay = 2000L
        ))

        sequence.add(ConversationDialogue(
            from = pabloEscobarIdentity,
            to = playerIdentity,
            message = "Ok. You're ready to go. Good luck.",
            delay = 11000L
        ))

        sequence.add(ConversationDialogue(
            to = playerIdentity,
            from = pabloEscobarIdentity,
            message = "I understand.",
            delay = 2000L
        ))

        return sequence
    }

    companion object {
        private val hasEmptySlotsPredicate = BiPredicate<Player, QuestProgression> { player, progression ->
            var emptySlots = 0
            for (item in player.inventory.contents) {
                if (item == null || item.type == Material.AIR) {
                    if (++emptySlots >= 5) {
                        return@BiPredicate true
                    }
                }
            }
            return@BiPredicate false
        }
    }

}