/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import java.lang.reflect.Type

object MineReferenceSerializer : JsonSerializer<Mine>, JsonDeserializer<Mine> {

    override fun serialize(src: Mine, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Mine? {
        return MineHandler.getMineById(json.asString).orElseGet { null }
    }

}
