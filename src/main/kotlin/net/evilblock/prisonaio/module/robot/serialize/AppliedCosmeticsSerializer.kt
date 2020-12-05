package net.evilblock.prisonaio.module.robot.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.robot.cosmetic.Cosmetic
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import java.lang.reflect.Type

object AppliedCosmeticsSerializer : JsonSerializer<List<Cosmetic>>, JsonDeserializer<List<Cosmetic>> {

    override fun serialize(list: List<Cosmetic>, type: Type, context: JsonSerializationContext): JsonElement {
        return toJson(list)
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): List<Cosmetic> {
        return arrayListOf<Cosmetic>().also {
            for (element in json.asJsonArray) {
                it.add(CosmeticHandler.getRegisteredCosmetics().first { it.getUniqueId() == element.asString })
            }
        }
    }

    @JvmStatic
    fun toJson(list: List<Cosmetic>): JsonArray {
        return JsonArray().also {
            for (cosmetic in list) {
                it.add(JsonPrimitive(cosmetic.getUniqueId()))
            }
        }
    }

}