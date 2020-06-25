/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.config

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.lang.reflect.Type

data class MineEffectsConfig(
    @JsonAdapter(PotionEffectTypeSetSerializer::class)
    /**
     * The effects given to players near the mine
     */
    val enabledEffects: HashSet<PotionEffectType> = hashSetOf(),
    /**
     * The potency of each effect type
     */
    @JsonAdapter(PotionEffectTypeMapSerializer::class)
    val effectPotency: HashMap<PotionEffectType, Int> = hashMapOf()
) {

    init {
        for (potionEffectType in PotionEffectType.values().filterNotNull()) {
            effectPotency[potionEffectType] = 1
        }
    }

    fun giveEffectsToPlayer(player: Player) {
        for (potionEffectType in enabledEffects) {
            player.addPotionEffect(PotionEffect(potionEffectType, 10 * 20, effectPotency.getOrDefault(potionEffectType, 1) - 1))
        }
    }

    private inner class PotionEffectTypeSetSerializer : JsonSerializer<HashSet<PotionEffectType>>, JsonDeserializer<HashSet<PotionEffectType>> {
        override fun serialize(src: HashSet<PotionEffectType>, type: Type, context: JsonSerializationContext): JsonElement {
            val json = JsonArray()
            src.forEach { potionEffectType -> json.add(JsonPrimitive(potionEffectType.id)) }
            return json
        }

        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): HashSet<PotionEffectType> {
            val set = hashSetOf<PotionEffectType>()
            json.asJsonArray.map { element -> set.add(PotionEffectType.getById(element.asInt)) }
            return set
        }
    }

    private inner class PotionEffectTypeMapSerializer : JsonSerializer<HashMap<PotionEffectType, Int>>, JsonDeserializer<HashMap<PotionEffectType, Int>> {
        override fun serialize(src: HashMap<PotionEffectType, Int>, type: Type, context: JsonSerializationContext): JsonElement {
            val json = JsonArray()
            src.forEach { (potionEffectType, potency) ->
                val elementJson = JsonObject()
                elementJson.addProperty("id", potionEffectType.id)
                elementJson.addProperty("potency", potency)
                json.add(elementJson)
            }
            return json
        }

        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): HashMap<PotionEffectType, Int> {
            val map = hashMapOf<PotionEffectType, Int>()
            json.asJsonArray.map { element -> element.asJsonObject }.forEach { elementObj ->
                map[PotionEffectType.getById(elementObj.get("id").asInt)] = elementObj.get("potency").asInt
            }
            return map
        }
    }

}