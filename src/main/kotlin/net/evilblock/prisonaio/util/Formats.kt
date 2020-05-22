package net.evilblock.prisonaio.util

import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat

object Formats {

    @JvmStatic
    fun capitalizeFully(name: String): String {
        return if (name.length > 1) {
            if (name.contains("_")) {
                val sbName = StringBuilder()

                for (subName in name.split("_").toTypedArray()) {
                    sbName.append(subName.substring(0, 1).toUpperCase() + subName.substring(1).toLowerCase()).append(" ")
                }

                sbName.toString().substring(0, sbName.length - 1)
            } else {
                name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase()
            }
        } else {
            name.toUpperCase()
        }
    }

    @JvmStatic
    fun formatItemStack(itemStack: ItemStack): String {
        if (itemStack.hasItemMeta() && itemStack.itemMeta.hasDisplayName()) {
            return itemStack.itemMeta.displayName
        }
        return capitalizeFully(itemStack.type.name)
    }

    @JvmStatic
    fun formatMoney(amount: Double): String {
        val formatted = NumberFormat.getInstance().format(amount)
        return "${ChatColor.AQUA}${Constants.MONEY_SYMBOL}${ChatColor.GREEN}${ChatColor.BOLD}$formatted"
    }

    @JvmStatic
    fun formatTokens(amount: Long): String {
        val formatted = NumberFormat.getInstance().format(amount)
        return "${ChatColor.RED}${Constants.TOKENS_SYMBOL}${ChatColor.YELLOW}${ChatColor.BOLD}$formatted"
    }

}