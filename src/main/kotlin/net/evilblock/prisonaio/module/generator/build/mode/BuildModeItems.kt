/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.build.mode

import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material

object BuildModeItems {

    val CONFIRM = ItemBuilder.of(Material.BEACON)
        .name("${ChatColor.GREEN}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.GRAY}to ${ChatColor.GREEN}${ChatColor.BOLD}CONFIRM ${ChatColor.GRAY}build location")
        .build()

    val PREVIEW = ItemBuilder.of(Material.ITEM_FRAME)
        .name("${ChatColor.GOLD}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.GRAY}to ${ChatColor.GOLD}${ChatColor.BOLD}PREVIEW ${ChatColor.GRAY}build")
        .build()

    val EXIT = ItemBuilder.of(Material.REDSTONE)
        .name("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.GRAY}to ${ChatColor.RED}${ChatColor.BOLD}EXIT ${ChatColor.GRAY}build mode")
        .build()

}