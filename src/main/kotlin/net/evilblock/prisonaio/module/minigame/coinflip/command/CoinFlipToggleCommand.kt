/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.coinflip.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler
import net.evilblock.prisonaio.module.minigame.coinflip.menu.CoinFlipBrowserMenu
import net.evilblock.prisonaio.module.minigame.coinflip.menu.CoinFlipGameMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object CoinFlipToggleCommand {

    @Command(
        names = ["coinflip toggle"],
        description = "Administrator toggle to disable CoinFlip minigame",
        permission = Permissions.COINFLIP_MANAGE,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        CoinFlipHandler.disabled = !CoinFlipHandler.disabled

        if (CoinFlipHandler.disabled) {
            sender.sendMessage("${ChatColor.RED}You've disabled CoinFlip!")

            Tasks.sync {
                CoinFlipHandler.cancelGames()

                for (player in Bukkit.getOnlinePlayers()) {
                    val openedMenu = Menu.currentlyOpenedMenus[player.uniqueId]
                    if (openedMenu is CoinFlipBrowserMenu || openedMenu is CoinFlipGameMenu) {
                        player.closeInventory()
                    }
                }
            }
        } else {
            sender.sendMessage("${ChatColor.GREEN}You've enabled CoinFlip!")
        }
    }

}