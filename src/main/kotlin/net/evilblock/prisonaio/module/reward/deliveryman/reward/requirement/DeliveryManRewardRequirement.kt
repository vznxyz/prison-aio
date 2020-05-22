package net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement

import com.google.gson.*
import org.bukkit.entity.Player
import java.lang.reflect.Type

interface DeliveryManRewardRequirement {

    fun getType(): DeliveryManRewardRequirementType<*>

    fun getText(): String

    fun test(player: Player): Boolean

    object Serializer : JsonSerializer<DeliveryManRewardRequirement>, JsonDeserializer<DeliveryManRewardRequirement> {
        override fun serialize(reward: DeliveryManRewardRequirement, type: Type, context: JsonSerializationContext): JsonElement {
            val json = JsonObject()
            json.addProperty("type", reward::class.java.name)
            json.add("properties", context.serialize(reward, reward::class.java))
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

}