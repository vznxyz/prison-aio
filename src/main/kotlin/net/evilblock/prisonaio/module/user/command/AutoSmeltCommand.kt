/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.perk.Perk
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object AutoSmeltCommand {

    @Command(
        names = ["auto-smelt", "autosmelt"],
        description = "Toggle your auto-smelt perk, if available"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (UsersModule.isAutoSmeltPerkEnabledByDefault()) {
            player.sendMessage("${ChatColor.RED}Auto-smelt is enabled for all players by default.")
            return
        }

        val user = UserHandler.getUser(player.uniqueId)
        if (!user.perks.hasPerk(player, Perk.AUTO_SMELT)) {
            player.sendMessage("${ChatColor.RED}You don't have access to auto-smelt.")
            return
        }

        user.perks.togglePerk(Perk.AUTO_SMELT)

        if (user.perks.isPerkEnabled(Perk.AUTO_SMELT)) {
            player.sendMessage("${ChatColor.GREEN}Auto-smelt is now enabled.")
        } else {
            player.sendMessage("${ChatColor.RED}Auto-smelt is now disabled.")
        }
    }

}