package net.evilblock.prisonaio.module.reward.deliveryman.reward.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirement
import java.lang.reflect.Type

object DeliveryManRewardRequirementSerializer : JsonSerializer<DeliveryManRewardRequirement>, JsonDeserializer<DeliveryManRewardRequirement> {

    override fun serialize(src: DeliveryManRewardRequirement, type: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()
        json.addProperty("type", src::class.java.name)
        json.add("properties", context.serialize(src, src::class.java))
        return json
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): DeliveryManRewardRequirement {
        val type = json.asJsonObject.get("type").asString
        val properties = json.asJsonObject.get("properties")

        try {
            return context.deserialize(properties, Class.forName(type))
        } catch (e: ClassNotFoundException) {
            throw JsonParseException("Unknown type: $type", e)
        }
    }

}