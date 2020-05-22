package net.evilblock.prisonaio.module.user.perk

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

enum class Perk(
    val displayName: String,
    val icon: ItemStack,
    val permission: String? = null
) {

    AUTO_SELL("Auto-Sell", ItemStack(Material.EMERALD), Permissions.AUTO_SELL),
    AUTO_SMELT("Auto-Smelt", ItemStack(Material.IRON_INGOT)),
    SALES_BOOST("Sales Boost", ItemStack(Material.GOLD_NUGGET));

    object PerkParameterType : ParameterType<Perk> {

        override fun transform(sender: CommandSender, source: String): Perk? {
            return try {
                valueOf(source)
            } catch (e: Exception) {
                sender.sendMessage("${ChatColor.RED}Can't find perk by the name of `$source`.")
                sender.sendMessage("${ChatColor.RED}Available perks: ${values().joinToString { it.name }}")
                null
            }
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            val completed = arrayListOf<String>()

            for (perk in values()) {
                println("test1")
                if (perk.name.startsWith(source, ignoreCase = true)) {
                    println("test2")
                    completed.add(perk.name.toLowerCase())
                }
            }

            return completed
        }

    }

}