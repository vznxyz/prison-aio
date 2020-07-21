/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.ShopHandler
import java.lang.reflect.Type

object ShopReferenceSerializer : JsonSerializer<Shop>, JsonDeserializer<Shop> {

    override fun serialize(shop: Shop, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(shop.id)
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Shop? {
        return ShopHandler.getShopById(json.asString).orElseGet { null }
    }

}