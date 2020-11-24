/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.block

import com.google.gson.*
import org.bukkit.Material
import java.lang.reflect.Type

data class BlockType(val material: Material, val data: Byte, var percentage: Double) {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is BlockType) {
            return false
        }

        return other.material == material && other.data == data && other.percentage == percentage
    }

    override fun hashCode(): Int {
        return 32 * material.id + 32 * data + (32 * percentage).toInt()
    }

    class Serializer : JsonDeserializer<BlockType>, JsonSerializer<BlockType> {

        override fun serialize(src: BlockType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val jsonObject = JsonObject()
            jsonObject.addProperty("material", src.material.name)
            jsonObject.addProperty("data", src.data)
            jsonObject.addProperty("percentage", src.percentage)
            return jsonObject
        }

        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BlockType? {
            val jsonObject = json.asJsonObject
            val material = Material.valueOf(jsonObject.get("material").asString)
            val data = jsonObject.get("data").asByte
            val percentage = jsonObject.get("percentage").asDouble
            return BlockType(material, data, percentage)
        }

    }

}