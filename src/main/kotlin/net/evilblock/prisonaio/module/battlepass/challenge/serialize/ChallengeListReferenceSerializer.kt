package net.evilblock.prisonaio.module.battlepass.challenge.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import java.lang.reflect.Type

class ChallengeListReferenceSerializer : JsonSerializer<List<Challenge>>, JsonDeserializer<List<Challenge>> {

    override fun serialize(src: List<Challenge>, type: Type, context: JsonSerializationContext?): JsonElement {
        return JsonArray().also { array -> src.forEach { challenge -> array.add(JsonPrimitive(challenge.id)) } }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext?): List<Challenge> {
        return json.asJsonArray.mapNotNull { element -> ChallengeHandler.getChallengeById(element.asString) }
    }

}