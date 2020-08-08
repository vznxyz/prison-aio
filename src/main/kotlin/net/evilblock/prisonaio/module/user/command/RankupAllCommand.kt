/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.rank.event.PlayerRankupEvent
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RankupAllCommand {

    @Command(
        names = ["rankup all", "rankupall", "rankupmax", "maxrankup"],
        description = "Rankup as many levels as you can"
    )
    @JvmStatic
    fun execute(player: Player) {
        UserHandler.getUser(player.uniqueId).purchaseMaxRankups(player, manual = true)
    }

}