/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.serializer

import com.google.gson.*
import net.evilblock.prisonaio.module.exchange.GrandExchangeHandler
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import java.lang.reflect.Type
import java.util.*

class ListingsReferenceSerializer : JsonSerializer<List<GrandExchangeListing>>, JsonDeserializer<List<GrandExchangeListing>> {

    override fun serialize(list: List<GrandExchangeListing>, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().also {
            for (listing in list) {
                it.add(JsonPrimitive(listing.id.toString()))
            }
        }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): List<GrandExchangeListing> {
        return arrayListOf<GrandExchangeListing>().also {
            for (element in json.asJsonArray) {
                val listing = GrandExchangeHandler.getListingById(UUID.fromString(element.asString))
                if (listing != null) {
                    it.add(listing)
                }
            }
        }
    }

}