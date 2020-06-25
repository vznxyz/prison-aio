/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.serializer

import com.google.gson.*
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import net.evilblock.prisonaio.module.privatemine.data.PrivateMineTier
import java.lang.reflect.Type

object TierReferenceSerializer : JsonSerializer<PrivateMineTier>, JsonDeserializer<PrivateMineTier> {

    override fun serialize(tier: PrivateMineTier, type: Type?, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(tier.number)
    }

    override fun deserialize(json: JsonElement, type: Type?, context: JsonDeserializationContext): PrivateMineTier {
        // handle old serialization
        if (json.isJsonObject) {
            return PrivateMineHandler.getTierByNumber(json.asJsonObject.get("number").asInt)!!
        }

        return PrivateMineHandler.getTierByNumber(json.asInt)!!
    }

}