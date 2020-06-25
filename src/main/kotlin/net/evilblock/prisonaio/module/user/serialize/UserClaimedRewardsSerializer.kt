/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import java.lang.reflect.Type

class UserClaimedRewardsSerializer : JsonSerializer<Map<DeliveryManReward, Long>>, JsonDeserializer<Map<DeliveryManReward, Long>> {

    override fun serialize(src: Map<DeliveryManReward, Long>, typeOf: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        src.forEach { (reward, timestamp) ->
            json.addProperty(reward.id, timestamp)
        }

        return json
    }

    override fun deserialize(json: JsonElement, typeOf: Type, context: JsonDeserializationContext): Map<DeliveryManReward, Long> {
        val jsonObject = json.asJsonObject
        val map = hashMapOf<DeliveryManReward, Long>()

        for ((rewardId, timestampElement) in jsonObject.entrySet()) {
            val reward = DeliveryManHandler.getRewardById(rewardId) ?: continue
            val timestamp = timestampElement.asLong
            map[reward] = timestamp
        }

        return map
    }

}