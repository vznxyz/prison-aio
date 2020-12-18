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

class EnchantsSetReferenceSerializer : JsonSerializer<MutableSet<Enchant>>, JsonDeserializer<MutableSet<Enchant>> {

    override fun serialize(set: MutableSet<Enchant>, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().also { array -> set.forEach { array.add(JsonPrimitive(it.id)) } }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): MutableSet<Enchant> {
        return json.asJsonArray.mapNotNull { EnchantHandler.getEnchantById(it.asString) }.toMutableSet()
    }

}