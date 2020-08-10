/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.gang.challenge.GangChallenge
import net.evilblock.prisonaio.module.gang.challenge.GangChallengeHandler
import java.lang.reflect.Type

class GangChallengeSetSerializer : JsonSerializer<Set<GangChallenge>>, JsonDeserializer<Set<GangChallenge>> {

    override fun serialize(challenges: Set<GangChallenge>, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().also { array -> challenges.forEach { array.add(JsonPrimitive(it.id)) } }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Set<GangChallenge> {
        return json.asJsonArray.mapNotNull { GangChallengeHandler.getChallengeById(it.asString) }.toMutableSet()
    }

}