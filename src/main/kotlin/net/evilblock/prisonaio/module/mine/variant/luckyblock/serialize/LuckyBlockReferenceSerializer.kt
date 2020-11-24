/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlock
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import java.lang.reflect.Type

class LuckyBlockReferenceSerializer : JsonSerializer<LuckyBlock>, JsonDeserializer<LuckyBlock> {

    override fun serialize(luckyBlock: LuckyBlock, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(luckyBlock.id)
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): LuckyBlock? {
        return LuckyBlockHandler.getBlockTypeById(json.asString)
    }

}