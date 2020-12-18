/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.serializer

import com.google.gson.*
import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.auction.listing.Listing
import java.lang.reflect.Type
import java.util.*

class ListingsReferenceSerializer : JsonSerializer<List<Listing>>, JsonDeserializer<List<Listing>> {

    override fun serialize(list: List<Listing>, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().also {
            for (listing in list) {
                it.add(JsonPrimitive(listing.id.toString()))
            }
        }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): List<Listing> {
        return arrayListOf<Listing>().also {
            for (element in json.asJsonArray) {
                val listing = AuctionHouseHandler.getListingById(UUID.fromString(element.asString))
                if (listing != null) {
                    it.add(listing)
                }
            }
        }
    }

}