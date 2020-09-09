/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.progress

import com.google.gson.*
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.mission.QuestMission
import java.lang.reflect.Type

class QuestProgressMissionCompletedSerializer : JsonSerializer<Set<QuestMission>>, JsonDeserializer<Set<QuestMission>> {

    override fun serialize(source: Set<QuestMission>, type: Type, context: JsonSerializationContext): JsonElement {
        val array = JsonArray()
        for (mission in source) {
            val obj = JsonObject()
            obj.addProperty("quest", mission.getQuest().getId())
            obj.addProperty("mission", mission.getId())
            array.add(obj)
        }
        return array
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Set<QuestMission> {
        val set = hashSetOf<QuestMission>()
        for (element in json.asJsonArray) {
            val obj = element.asJsonObject
            val quest = QuestHandler.getQuestById(obj["quest"].asString)!!
            val mission = quest.getMissionById(obj["mission"].asString)!!
            set.add(mission)
        }
        return set
    }

}