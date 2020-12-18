/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.rename

import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object RenameTagUtils {

    @JvmStatic
    fun makeRenameTag(amount: Int): ItemStack {
        return ItemBuilder.of(Material.NAME_TAG)
            .amount(amount)
            .name("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}!${ChatColor.GRAY}] ${ChatColor.AQUA}${ChatColor.BOLD}Rename Tag ${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}!${ChatColor.GRAY}]")
            .setLore(TextSplitter.split(text = "Drag and drop this tag onto your pickaxe to rename it."))
            .glow()
            .build()
    }

    @JvmStatic
    fun isRenameTag(item: ItemStack): Boolean {
        return ItemUtils.itemTagHasKey(item, "RenameTag")
    }

}