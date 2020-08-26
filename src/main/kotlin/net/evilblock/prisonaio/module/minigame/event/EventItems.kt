package net.evilblock.prisonaio.module.minigame.event

import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object EventItems {

    val LEAVE_EVENT: ItemStack = ItemBuilder.of(Material.INK_SACK)
        .data(DyeColor.RED.dyeData.toShort())
        .name("${ChatColor.RED}${ChatColor.BOLD}Leave Event")
        .build()

    val VOTE_FOR_ARENA: ItemStack = ItemBuilder.of(Material.PAPER)
        .name("${ChatColor.GRAY}» ${ChatColor.GOLD}${ChatColor.BOLD}%MAP% ${ChatColor.GRAY}«")
        .build()

}