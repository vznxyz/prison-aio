/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.narcotic.dialogue

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.DialogueSequence
import net.evilblock.prisonaio.module.quest.dialogue.impl.FunctionalDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.ConversationDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.PlayerConversationIdentity
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.StaticConversationIdentity
import net.evilblock.prisonaio.module.quest.impl.narcotic.Narcotic
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest
import net.evilblock.prisonaio.module.quest.impl.narcotic.progression.NarcoticsQuestProgression
import net.evilblock.prisonaio.module.quest.progression.QuestProgression
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat
import java.util.*

class DeliverDrugsDialogueSequence(private val character: String) : DialogueSequence {

    override fun getSequence(player: Player): LinkedList<Dialogue> {
        val sequence = LinkedList<Dialogue>()

        val drugDealerIdentity = StaticConversationIdentity(QuestsModule.getNpcName(character))
        val playerIdentity = PlayerConversationIdentity(player.uniqueId)

        sequence.add(
            ConversationDialogue(
                from = playerIdentity,
                to = drugDealerIdentity,
                message = "Hey ${ChatColor.stripColor(drugDealerIdentity.getName())}${ChatColor.GRAY}! I'm here to deliver some drugs for Pablo.",
                delay = 3000L
            )
        )

        val chosenNarcotic = determineNarcotic(player, NarcoticsQuest.getProgress(player))
        if (chosenNarcotic == null) {
            sequence.add(ConversationDialogue(
                    from = playerIdentity,
                    to = drugDealerIdentity,
                    message = "Well... actually it seems I've misplaced the drugs. I'll be back later!",
                    delay = 3000L
            ))

            return sequence
        }

        val price = if (chosenNarcotic == Narcotic.MARIJUANA) {
            1_000_000L
        } else {
            5_000_000L
        }

        sequence.add(
            ConversationDialogue(
                from = playerIdentity,
                to = drugDealerIdentity,
                message = "I have 64 LBs of ${ChatColor.stripColor(chosenNarcotic.displayName)} ${ChatColor.GRAY}for you. That'll cost you $${NumberFormat.getInstance().format(price)}.",
                delay = 3000L
            )
        )

        sequence.add(
            ConversationDialogue(
                from = drugDealerIdentity,
                to = playerIdentity,
                message = "Sounds good to me.",
                delay = 2500L
            )
        )

        sequence.add(FunctionalDialogue(
            function = { player ->
                val foundItems = chosenNarcotic.findInInventory(player)
                if (foundItems.isNotEmpty()) {
                    var amountConsumed = 0

                    for ((slot, itemStack) in foundItems) {
                        val amountNeeded = 64 - amountConsumed
                        if (amountNeeded > 0) {
                            if (itemStack.amount <= amountNeeded) {
                                amountConsumed += itemStack.amount
                                player.inventory.setItem(slot, ItemStack(Material.AIR))
                                player.updateInventory()
                            } else {
                                amountConsumed += amountNeeded
                                itemStack.amount = itemStack.amount - amountNeeded
                            }
                        }
                    }

                    player.updateInventory()
                }

                NarcoticsQuest.getProgress(player).onDelivered(character, chosenNarcotic)

                player.sendMessage(" ${ChatColor.GRAY}You hand ${chosenNarcotic.displayName} ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}64 LB${ChatColor.GRAY}) to ${drugDealerIdentity.getName()}${ChatColor.GRAY}")
            },
            delay = 2000L
        ))

        sequence.add(FunctionalDialogue(
            function = { player ->
                VaultHook.useEconomy { economy ->
                    economy.depositPlayer(player, price.toDouble())
                }

                player.sendMessage(" ${drugDealerIdentity.getName()} ${ChatColor.GRAY}hands you ${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}${NumberFormat.getInstance().format(price)}${ChatColor.GRAY}")
            },
            delay = 2000L
        ))

        sequence.add(ConversationDialogue(from = playerIdentity, to = drugDealerIdentity, message = "Good doing business with you. Catch you later!"))

        return sequence
    }

    companion object {
        private fun determineNarcotic(player: Player, progress: QuestProgression): Narcotic? {
            val foundMarijuana = Narcotic.MARIJUANA.findInInventory(player)
            val foundCocaine = Narcotic.COCAINE.findInInventory(player)

            if (foundMarijuana.isNotEmpty() && foundMarijuana.values.sumBy { it.amount } >= 64 && (progress as NarcoticsQuestProgression).marijuanaDelivered < 3) {
                return Narcotic.MARIJUANA
            }

            if (foundCocaine.isNotEmpty() && foundCocaine.values.sumBy { it.amount } >= 64 && (progress as NarcoticsQuestProgression).cocaineDelivered < 2) {
                return Narcotic.COCAINE
            }

            return null
        }
    }

}