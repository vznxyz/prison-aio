/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.rank.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RankParameterType : ParameterType<Rank?> {

    override fun transform(sender: CommandSender, source: String): Rank? {
        val optionalRank = RankHandler.getRankById(source)
        if (!optionalRank.isPresent) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a rank by the name `${ChatColor.WHITE}$source${ChatColor.RED}`.")
            return null
        }
        return optionalRank.get()
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completions = arrayListOf<String>()
        for (rank in RankHandler.getSortedRanks()) {
            if (rank.id.toLowerCase().startsWith(source.toLowerCase())) {
                completions.add(rank.id)
            }
        }
        return completions
    }

}