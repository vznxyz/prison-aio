/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.battlepass.tier.Tier
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import java.lang.reflect.Type

class TierSetReferenceSerializer : JsonSerializer<Set<Tier>>, JsonDeserializer<Set<Tier>> {

    override fun serialize(src: Set<Tier>, type: Type, context: JsonSerializationContext?): JsonElement {
        return JsonArray().also { array ->
            src.forEach { tier ->
                array.add(JsonPrimitive(tier.number))
            }
        }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext?): Set<Tier> {
        return json.asJsonArray.mapNotNull { element -> TierHandler.getTierByNumber(element.asInt) }.toMutableSet()
    }

}