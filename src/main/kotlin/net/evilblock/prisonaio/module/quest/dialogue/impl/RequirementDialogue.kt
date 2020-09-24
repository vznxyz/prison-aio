/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.dialogue.impl

import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.progress.QuestProgress
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.entity.Player
import java.util.function.BiPredicate

class RequirementDialogue(
    private val quest: Quest,
    private val predicate: BiPredicate<Player, QuestProgress>,
    private val failureDialogue: Dialogue,
    delay: Long = 0L,
    useState: Boolean = false
) : Dialogue(delay, useState) {

    override fun send(player: Player) {}

    override fun canSend(player: Player): Boolean {
        val user = UserHandler.getUser(player.uniqueId)

        val test = predicate.test(player, user.getQuestProgress(quest))
        if (!test) {
            failureDialogue.send(player)
        }

        return test
    }

    override fun isSpaced(): Boolean {
        return false
    }

}