/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object UserStatisticsCommands {

    @Command(
        names = ["user set-stat blocks-mined"],
        description = "Set the blocks-mined statistic of a user",
        permission = Permissions.USERS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "amount") amount: Int) {
        assert(amount > 0) { "Number must be above 0" }

        user.statistics.setBlocksMined(amount)
        UserHandler.saveUser(user)

        sender.sendMessage("${ChatColor.GREEN}Updated ${user.getUsername()}'s blocks-mined statistic to $amount.")
    }

    @Command(
        names = ["user set-stat blocks-mined-at-mine"],
        description = "Set the blocks-mined-at-mine statistic of a user",
        permission = Permissions.USERS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Flag(value = ["dbp"], description = "Modify daily BattlePass stats or user stats", defaultValue = false) dbp: Boolean,
        @Param(name = "player") user: User,
        @Param(name = "mine") mine: Mine,
        @Param(name = "amount") amount: Int
    ) {
        assert(amount > 0) { "Number must be above 0" }

        if (dbp) {
            DailyChallengeHandler.getSession().getProgress(user.uuid).setBlocksMinedAtMine(mine, amount)
        } else {
            user.statistics.setBlocksMinedAtMine(mine, amount)
            UserHandler.saveUser(user)
        }

        sender.sendMessage("${ChatColor.GREEN}Updated ${user.getUsername()}'s blocks-mined-at-mine statistic to $amount for mine ${ChatColor.BLUE}${mine.id}${ChatColor.GREEN}.")
    }

    @Command(
        names = ["user set-stat playtime"],
        description = "Set the play-time statistic of a user",
        permission = Permissions.USERS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User, @Param(name = "milliseconds") amount: Long) {
        assert(amount > 0) { "Number must be above 0" }

        user.statistics.setPlayTime(amount)
        UserHandler.saveUser(user)

        sender.sendMessage("${ChatColor.GREEN}Updated ${user.getUsername()}'s play-time statistic to $amount.")
    }

}