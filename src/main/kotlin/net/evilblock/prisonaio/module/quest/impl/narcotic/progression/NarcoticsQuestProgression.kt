package net.evilblock.prisonaio.module.quest.impl.narcotic.progression

import net.evilblock.prisonaio.module.quest.impl.narcotic.Narcotic
import net.evilblock.prisonaio.module.quest.progression.QuestProgression
import net.evilblock.prisonaio.module.quest.impl.narcotic.NarcoticsQuest

class NarcoticsQuestProgression : QuestProgression(NarcoticsQuest) {

    var marijuanaDelivered: Int = 0
    var cocaineDelivered: Int = 0
    private var delivered = hashSetOf<String>()

    fun getDelivered(): Int {
        return marijuanaDelivered + cocaineDelivered
    }

    fun hasDeliveredTo(character: String): Boolean {
        return delivered.contains(character)
    }

    fun onDelivered(character: String, narcotic: Narcotic) {
        delivered.add(character)

        if (narcotic == Narcotic.MARIJUANA) {
            marijuanaDelivered++
        } else if (narcotic == Narcotic.COCAINE) {
            cocaineDelivered++
        }

        requiresSave = true
    }

}