/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.rank.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import java.lang.reflect.Type

object RankReferenceSerializer : JsonSerializer<Rank>, JsonDeserializer<Rank> {

    override fun serialize(rank: Rank, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(rank.id)
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Rank? {
        val optionalRank = RankHandler.getRankById(json.asString)
        if (optionalRank.isPresent) {
            return optionalRank.get()
        }
        return null
    }

}