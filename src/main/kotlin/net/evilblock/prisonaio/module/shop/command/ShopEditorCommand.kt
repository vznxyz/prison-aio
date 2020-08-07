/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.shop.menu.ShopEditorMenu
import org.bukkit.entity.Player

object ShopEditorCommand {

    @Command(
        names = ["prison shop editor"],
        description = "Open the shop editor",
        permission = "prisonaio.shops.editor"
    )
    @JvmStatic
    fun execute(player: Player) {
        ShopEditorMenu().openMenu(player)
    }

}