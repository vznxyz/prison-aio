/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp

import net.evilblock.prisonaio.module.mechanic.economy.Currency
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Warp(val id: String, var location: Location) {

    var displayName: String = id
    var description: String = ""

    var icon: ItemStack = ItemStack(Material.ENDER_PEARL)

    var currency: Currency.Type? = null
    var price: Number? = null

    fun getPermission(): String {
        return "warps.${id.toLowerCase()}"
    }

    fun hasPermission(player: Player): Boolean {
        return player.hasPermission(Permissions.WARPS_ACCESS_ALL) || player.hasPermission(getPermission())
    }

    fun getFormattedName(): String {
        if (displayName == id) {
            return "${ChatColor.RESET}${ChatColor.BOLD}$displayName"
        } else {
            return displayName
        }
    }

    fun isPriceSet(): Boolean {
        return currency != null && price != null
    }

    fun canAfford(player: Player): Boolean {
        return currency!!.has(player.uniqueId, price!!)
    }

    fun teleport(player: Player) {
        if (isPriceSet()) {
            currency!!.take(player.uniqueId, price!!)
        }

        player.teleport(location)

        if (isPriceSet()) {
            player.sendMessage("${ChatColor.YELLOW}You've been teleported to ${getFormattedName()}${ChatColor.YELLOW} for ${currency!!.format(price!!)}${ChatColor.YELLOW}!")
        } else {
            player.sendMessage("${ChatColor.YELLOW}You've been teleported to ${getFormattedName()}${ChatColor.YELLOW}!")
        }
    }

}