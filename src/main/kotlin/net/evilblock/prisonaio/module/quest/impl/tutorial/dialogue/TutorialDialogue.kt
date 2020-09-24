/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.dialogue

import net.evilblock.cubed.entity.npc.animation.target.PlayerTarget
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.dialogue.DialogueSequence
import net.evilblock.prisonaio.module.quest.dialogue.impl.FunctionalDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.NpcConversationDialogue
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.NpcConversationIdentity
import net.evilblock.prisonaio.module.quest.dialogue.impl.conversation.identity.PlayerConversationIdentity
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.module.quest.impl.tutorial.entity.PersonalTutorialGuide
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*

class TutorialDialogue(private val uuid: UUID, private val npc: PersonalTutorialGuide) : DialogueSequence {

    override fun getSequence(player: Player): LinkedList<Dialogue> {
        val originalLines = npc.getLines()
        val guideIdentity = NpcConversationIdentity(npc)
        val playerIdentity = PlayerConversationIdentity(uuid)

        val sequence = LinkedList<Dialogue>()

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.targetLocation(PlayerTarget(player)) }
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "Listen up ${player.name}! I'm ${ChatColor.stripColor(QuestsModule.getNpcName("tutorial-guide"))}...",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ ->
                npc.updateLines(listOf("${ChatColor.RED}${ChatColor.GREEN}${ChatColor.BLUE}"))
            },
            delay = 2500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "You've landed yourself in the Junkie Prison, the most hardcore maximum security prison in Minecraft.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(listOf("${ChatColor.RED}${ChatColor.GREEN}${ChatColor.BLUE}")) },
            delay = 4000L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "I'm going to teach you your duties as a prisoner, and how to work your way up through the prisoner ranks.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(listOf("${ChatColor.RED}${ChatColor.GREEN}${ChatColor.BLUE}")) },
            delay = 4500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "I will be moving around a lot, so make sure to keep up with me!",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ ->
                npc.updateLines(originalLines)
            },
            delay = 3000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, dialogue ->
                npc.moveTo(player.location.clone().add(player.location.direction.clone().multiply(Vector(10.0, 0.0, 10.0)))) {
                    dialogue.complete = true
                }
            },
            useState = true
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.targetLocation(PlayerTarget(player)) }
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "Most anything you do here requires money and tokens, so you need to keep up with the economy.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 6000L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "You earn money by mining blocks and selling them to shops. Some mines sell their blocks for more money.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 7000L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "You earn tokens by mining as well. For every one block you break, you earn one token.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 5500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "You can earn more money and tokens through pickaxe enchants, such as: Lucky Money, Token Pouch, and Tokenator.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 7000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, dialogue ->
                npc.moveTo(player.location.clone().add(player.location.direction.clone().multiply(Vector(10.0, 0.0, 10.0)))) {
                    dialogue.complete = true
                }
            },
            useState = true
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.targetLocation(PlayerTarget(player)) }
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "MineJunkie has many custom enchants that can be applied to your pickaxe. There are 3 types of enchants: destructive, rewarding, and ability.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 6500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "To start building your pickaxe, hold it in your hand and right-click.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, dialogue ->
                Tasks.asyncTimer(object : BukkitRunnable() {
                    override fun run() {
                        if (!player.isOnline) {
                            cancel()
                            return
                        }

                        val progress = TutorialQuest.getProgress(player)
                        if (progress.openedEnchantsMenu) {
                            dialogue.complete = true
                            cancel()
                            return
                        }
                    }
                }, 1L, 1L)
            },
            useState = true
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 1000L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "You can also refund an enchantment, or completely salvage your pickaxe for a 25% return of its total cost.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 4500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "Pickaxes and enchanted books are trade-able between players and are advertised in chat.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 6000L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "To apply an enchanted book to a pickaxe, open your inventory and simply drag and drop the book onto the pickaxe.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 6500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "To advertise the item in your hands to chat, type `[item]`.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 5000L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "Enchant limits help maintain stable map progression. You can increase enchant limits by meeting prestige requirements. Hover over a pickaxe for more details.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 7500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "Now you know all about enchanting... Lets talk about ranks!",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 3000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, dialogue ->
                npc.moveTo(player.location.clone().add(player.location.direction.clone().multiply(Vector(10.0, 0.0, 10.0)))) {
                    dialogue.complete = true
                }
            },
            useState = true
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.targetLocation(PlayerTarget(player)) }
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "There are 26 ranks, A-Z. Each rank has its own mine, which increasingly sells their blocks for more money.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 6500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "You can rankup to the next rank by collecting the money and tokens needed to purchase it. Type `/ranks` to see their prices.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 7000L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "Once you reach the Z rank, you can then prestige. If you prestige, you will be given a prestige token to spend at the PT shop.",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 6500L
        ))

        sequence.add(NpcConversationDialogue(
            from = guideIdentity,
            to = playerIdentity,
            message = "...",
            delay = 1000L
        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ -> npc.updateLines(originalLines) },
            delay = 6500L
        ))

//        sequence.add(FunctionalDialogue(
//            function = { _, dialogue ->
//                HighlightedSettingMenu(UserSetting.QUICK_ACCESS_ENCHANTS) {
//                    dialogue.complete = true
//                }.openMenu(player)
//            },
//            useState = true
//        ))

        sequence.add(FunctionalDialogue(
            function = { _, _ ->

            },
            delay = 0L,
            useState = true
        ))

        return sequence
    }

}