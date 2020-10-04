/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import java.lang.reflect.Type

class EnchantsMapReferenceSerializer : JsonSerializer<MutableMap<AbstractEnchant, Int>>, JsonDeserializer<MutableMap<AbstractEnchant, Int>> {

    override fun serialize(map: MutableMap<AbstractEnchant, Int>, type: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        for ((key, value) in map) {
            json.addProperty(key.id, value)
        }

        return json
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): MutableMap<AbstractEnchant, Int> {
        val map = hashMapOf<AbstractEnchant, Int>()

        for (entry in json.asJsonObject.entrySet()) {
            val enchant = EnchantsManager.getEnchantById(entry.key) ?: continue
            map[enchant] = entry.value.asInt
        }

        return map
    }

}