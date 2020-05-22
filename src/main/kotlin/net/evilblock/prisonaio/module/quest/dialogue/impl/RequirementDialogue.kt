package net.evilblock.prisonaio.module.quest.dialogue.impl

import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.dialogue.Dialogue
import net.evilblock.prisonaio.module.quest.progression.QuestProgression
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.entity.Player
import java.util.function.BiPredicate

class RequirementDialogue<T : Quest<T>>(
    private val quest: T,
    private val predicate: BiPredicate<Player, QuestProgression>,
    private val failureDialogue: Dialogue,
    delay: Long = 0L
) : Dialogue(delay) {

    override fun send(player: Player) {}

    override fun canSend(player: Player): Boolean {
        val user = UserHandler.getUser(player.uniqueId)
        val test = predicate.test(player, user.getQuestProgression(quest) as QuestProgression)

        if (!test) {
            failureDialogue.send(player)
        }

        return test
    }

    override fun isSpaced(): Boolean {
        return false
    }

}