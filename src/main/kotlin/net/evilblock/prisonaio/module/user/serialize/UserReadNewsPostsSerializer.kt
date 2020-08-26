/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.user.news.News
import net.evilblock.prisonaio.module.user.news.NewsHandler
import java.lang.reflect.Type
import java.util.*

class UserReadNewsPostsSerializer : JsonSerializer<MutableSet<News>>, JsonDeserializer<MutableSet<News>> {

    override fun serialize(set: MutableSet<News>, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().also { array -> set.forEach { array.add(JsonPrimitive(it.id.toString())) } }
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): MutableSet<News> {
        return hashSetOf<News>().also { set ->
            json.asJsonArray.forEach {
                val newsPost = NewsHandler.getNewsById(UUID.fromString(it.asString))
                if (newsPost != null) {
                    set.add(newsPost)
                }
            }
        }
    }

}