/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward
import java.lang.reflect.Type

class RewardListReferenceSerializer : JsonSerializer<List<Reward>>, JsonDeserializer<List<Reward>> {

    override fun serialize(src: List<Reward>, type: Type, context: JsonSerializationContext?): JsonElement {
        return JsonArray().also { array ->
            src.forEach { reward ->
                array.add(JsonPrimitive("${reward.tier.number}:${reward.isFreeReward()}"))
            }
        }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext?): List<Reward> {
        return json.asJsonArray.mapNotNull { element ->
            val split = element.asString.split(":")
            val tier = TierHandler.getTierByNumber(split[0].toInt())
            val free = split[1].toBoolean()

            if (free) {
                tier?.freeReward
            } else {
                tier?.premiumReward
            }
        }
    }

}