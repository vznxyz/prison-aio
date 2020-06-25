/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CrateHandler
import java.lang.reflect.Type

object CrateReferenceSerializer : JsonSerializer<Crate>, JsonDeserializer<Crate> {

    override fun serialize(crate: Crate, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(crate.id)
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Crate {
        return CrateHandler.findCrate(json.asString) ?: throw IllegalStateException("Cannot reference crate with ID of ${json.asString}")
    }

}