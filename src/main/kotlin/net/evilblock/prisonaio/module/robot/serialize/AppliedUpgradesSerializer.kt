package net.evilblock.prisonaio.module.robot.serialize

import com.google.gson.*
import net.evilblock.prisonaio.module.robot.impl.upgrade.Upgrade
import net.evilblock.prisonaio.module.robot.impl.upgrade.UpgradeManager
import java.lang.reflect.Type

object AppliedUpgradesSerializer : JsonSerializer<Map<Upgrade, Int>>, JsonDeserializer<Map<Upgrade, Int>> {

    override fun serialize(map: Map<Upgrade, Int>, type: Type, context: JsonSerializationContext): JsonElement {
        return toJson(map)
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Map<Upgrade, Int> {
        return hashMapOf<Upgrade, Int>().also {
            json.asJsonObject.entrySet().forEach { entry ->
                val upgrade = UpgradeManager.getRegisteredUpgrades().first { it.getUniqueId() == entry.key }
                it[upgrade] = entry.value.asInt
            }
        }
    }

    @JvmStatic
    fun toJson(map: Map<Upgrade, Int>): JsonObject {
        return JsonObject().also {
            map.forEach { (upgrade, level) ->
                it.addProperty(upgrade.getUniqueId(), level)
            }
        }
    }

}