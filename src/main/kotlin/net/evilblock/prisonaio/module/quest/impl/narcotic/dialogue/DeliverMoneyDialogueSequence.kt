/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.narcotic.dialogue

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.DialogueSequence
import net.evilblock.prisonaio.module.quest.dialogue.impl.FunctionalDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.ThoughtDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.ConversationDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.PlayerConversationIdentity
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.StaticConversationIdentity
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class DeliverMoneyDialogueSequence(private val uuid: UUID) : DialogueSequence {

    override fun getSequence(player: Player): LinkedList<Dialogue> {
        val playerIdentity = PlayerConversationIdentity(uuid)
        val pabloEscobarIdentity = StaticConversationIdentity("${ChatColor.YELLOW}${ChatColor.BOLD}Pablo Escobar")

        val sequence = LinkedList<Dialogue>()

        val balance = VaultHook.useEconomyAndReturn { it.getBalance(player) }
        if (balance < 13_000_000.0) {
            sequence.add(ThoughtDialogue(message = "Maybe I should come back when I have Pablo's money..."))
            return sequence
        }

        sequence.add(ConversationDialogue(
            from = playerIdentity,
            to = pabloEscobarIdentity,
            message = "I'm back Pablo. I've delivered all of your drugs to the dealers and collected your money from them.",
            delay = 7000L
        ))

        sequence.add(ConversationDialogue(
            from = pabloEscobarIdentity,
            to = playerIdentity,
            message = "Ah, good work! For your troubles, I'll let you keep half of what you've collected.",
            delay = 5000L
        ))

        sequence.add(FunctionalDialogue(
            function = { player ->
                VaultHook.useEconomy { it.withdrawPlayer(player, 6_500_000.0) }
                player.sendMessage(" ${ChatColor.GRAY}You hand over ${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}6,500,000 ${ChatColor.GRAY}to Pablo...")
            }
        ))

        sequence.add(ConversationDialogue(
            from = pabloEscobarIdentity,
            to = playerIdentity,
            message = "Also, you can access my seed shop and network of drug dealers at any time you please.",
            delay = 5000L
        ))

        sequence.add(ConversationDialogue(
            from = pabloEscobarIdentity,
            to = playerIdentity,
            message = "With the seeds you purchase from my shop, grow and manufacture narcotics in your cell, and then distribute to my dealers for them to sell for you.",
            delay = 9000L
        ))

        sequence.add(ConversationDialogue(
            from = pabloEscobarIdentity,
            to = playerIdentity,
            message = "Thanks again my friend!",
            delay = 3000L
        ))

        sequence.add(ConversationDialogue(
            from = playerIdentity,
            to = pabloEscobarIdentity,
            message = "No problem! Good bye!",
            delay = 3000L
        ))

        return sequence
    }

}