/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.serialize

import com.google.gson.*
import net.evilblock.cubed.serialize.ItemStackAdapter
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

object MappedInventorySerializer : JsonSerializer<Map<Int, ItemStack>>, JsonDeserializer<Map<Int, ItemStack>> {

    override fun serialize(map: Map<Int, ItemStack>, type: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        for ((index, item) in map) {
            json.add(index.toString(), ItemStackAdapter.serialize(item))
        }

        return json
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Map<Int, ItemStack> {
        val map = hashMapOf<Int, ItemStack>()

        for ((key, element) in json.asJsonObject.entrySet()) {
            map[key.toInt()] = ItemStackAdapter.deserialize(element)
        }

        return map
    }

}