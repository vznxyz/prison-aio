package net.evilblock.prisonaio.module.user.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.progression.QuestProgression
import java.lang.reflect.Type

class UserQuestProgressionSerializer : JsonSerializer<Map<Quest<*>, QuestProgression>>, JsonDeserializer<Map<Quest<*>, QuestProgression>> {

    override fun serialize(src: Map<Quest<*>, QuestProgression>, typeOf: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        for ((quest, progression) in src.entries) {
            json.add(quest.getId(), context.serialize(progression, QuestProgression::class.java))
        }

        return json
    }

    override fun deserialize(json: JsonElement, typeOf: Type, context: JsonDeserializationContext): Map<Quest<*>, QuestProgression> {
        val jsonObject = json.asJsonObject
        val map = hashMapOf<Quest<*>, QuestProgression>()

        for ((questId, progressionJson) in jsonObject.entrySet()) {
            val quest = QuestHandler.getQuestById(questId) ?: continue
            val progression = context.deserialize<QuestProgression>(progressionJson, QuestProgression::class.java)

            map[quest] = progression
        }

        return map
    }

}