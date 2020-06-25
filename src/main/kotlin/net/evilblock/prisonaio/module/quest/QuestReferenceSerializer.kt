/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest

import com.google.gson.*
import java.lang.reflect.Type

object QuestReferenceSerializer : JsonSerializer<Quest<*>>, JsonDeserializer<Quest<*>> {

    override fun serialize(quest: Quest<*>, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(quest.getId())
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Quest<*> {
        return QuestHandler.getQuestById(json.asString)!!
    }

}