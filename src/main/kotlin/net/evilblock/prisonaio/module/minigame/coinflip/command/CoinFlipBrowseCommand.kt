/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.coinflip.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.minigame.coinflip.menu.CoinFlipBrowserMenu
import org.bukkit.entity.Player

object CoinFlipBrowseCommand {

    @Command(
        names = ["coinflip", "cf", "coinflip browse", "cf browse"],
        description = "Browse the current coinflip games"
    )
    @JvmStatic
    fun execute(player: Player) {
        CoinFlipBrowserMenu().openMenu(player)
    }

}