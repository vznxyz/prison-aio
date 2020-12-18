/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import java.lang.reflect.Type

class EnchantsMapReferenceSerializer : JsonSerializer<MutableMap<Enchant, Int>>, JsonDeserializer<MutableMap<Enchant, Int>> {

    override fun serialize(map: MutableMap<Enchant, Int>, type: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        for ((key, value) in map) {
            json.addProperty(key.id, value)
        }

        return json
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): MutableMap<Enchant, Int> {
        val map = hashMapOf<Enchant, Int>()

        for (entry in json.asJsonObject.entrySet()) {
            val enchant = EnchantHandler.getEnchantById(entry.key) ?: continue
            map[enchant] = entry.value.asInt
        }

        return map
    }

}