package net.evilblock.prisonaio.module.crate.reward

import com.google.gson.*
import net.evilblock.prisonaio.module.crate.CratesModule
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

abstract class CrateReward {

    var name: String = "Unnamed Reward"
    var chance: Double = 0.0
    internal var icon: ItemStack = ItemStack(Material.COMMAND_REPEATING)
    var commands: MutableList<String> = arrayListOf()
    var sortOrder: Int = 1

    fun setIcon(icon: ItemStack) {
        this.icon = icon
    }

    open fun getIcon(): ItemStack {
        return icon
    }

    open fun execute(player: Player) {
        player.sendMessage("${CratesModule.getChatPrefix()}You have won the $name${ChatColor.GRAY}!")

        for (command in commands) {
            val processedCommand = command
                .replace("{playerName}", player.name)
                .replace("{playerUuid}", player.uniqueId.toString())
                .replace("{rewardName}", name)

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand)
        }
    }

    object Serializer : JsonSerializer<CrateReward>, JsonDeserializer<CrateReward> {

        override fun serialize(src: CrateReward, type: Type, context: JsonSerializationContext): JsonElement {
            val json = JsonObject()
            json.addProperty("type", src::class.java.name)
            json.add("properties", context.serialize(src, src::class.java))
            return json
        }

        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): CrateReward {
            val jsonObject = json.asJsonObject
            val classType = jsonObject.get("type").asString
            val properties = jsonObject.get("properties").asJsonObject
            try {
                return context.deserialize(properties, Class.forName(classType))
            } catch (e: ClassNotFoundException) {
                throw JsonParseException("Unknown type: $type", e)
            }
        }

    }

}