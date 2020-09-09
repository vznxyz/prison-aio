/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.progress.QuestProgress
import java.lang.reflect.Type

class UserQuestProgressionSerializer : JsonSerializer<Map<Quest<*>, QuestProgress>>, JsonDeserializer<Map<Quest<*>, QuestProgress>> {

    override fun serialize(src: Map<Quest<*>, QuestProgress>, typeOf: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        for ((quest, progression) in src.entries) {
            json.add(quest.getId(), context.serialize(progression, QuestProgress::class.java))
        }

        return json
    }

    override fun deserialize(json: JsonElement, typeOf: Type, context: JsonDeserializationContext): Map<Quest<*>, QuestProgress> {
        val jsonObject = json.asJsonObject
        val map = hashMapOf<Quest<*>, QuestProgress>()

        for ((questId, progressionJson) in jsonObject.entrySet()) {
            val quest = QuestHandler.getQuestById(questId) ?: continue
            val progression = context.deserialize<QuestProgress>(progressionJson, QuestProgress::class.java)

            map[quest] = progression
        }

        return map
    }

}