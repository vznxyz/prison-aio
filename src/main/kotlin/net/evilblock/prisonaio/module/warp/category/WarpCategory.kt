/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.category

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.warp.Warp
import net.evilblock.prisonaio.module.warp.WarpHandler
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

class WarpCategory(val id: String) {

    var icon: ItemStack = ItemStack(Material.CHEST)
    var name: String = id
    var description: MutableList<String> = arrayListOf()

    @JsonAdapter(WarpSetSerializer::class)
    val warps: MutableSet<Warp> = hashSetOf()

    private class WarpSetSerializer : JsonSerializer<Set<Warp>>, JsonDeserializer<Set<Warp>> {
        override fun serialize(warps: Set<Warp>, type: Type, context: JsonSerializationContext): JsonElement {
            return JsonArray().also { array ->
                for (warp in warps) {
                    array.add(JsonPrimitive(warp.id))
                }
            }
        }

        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Set<Warp> {
            return hashSetOf<Warp>().also { set ->
                json.asJsonArray.forEach {
                    val warp = WarpHandler.getWarpById(it.asString)
                    if (warp != null) {
                        set.add(warp)
                    }
                }
            }
        }
    }

}