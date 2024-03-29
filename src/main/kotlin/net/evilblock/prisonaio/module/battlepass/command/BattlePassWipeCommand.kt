/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.battlepass.BattlePassProgress
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Permissions
import org.bson.Document
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object BattlePassWipeCommand {

    @Command(
        names = ["battlepass wipe", "junkiepass wipe", "jp wipe"],
        description = "Reset all user's BattlePass progress",
        permission = Permissions.BATTLE_PASS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        PrisonAIO.instance.database.getCollection("users").updateMany(Document(), Document("\$unset", Document("battlePassData", "")))

        for (user in UserHandler.getUsers()) {
            user.battlePassProgress = BattlePassProgress(user)
            user.requiresSave = true
        }

        sender.sendMessage("${ChatColor.GREEN}You have reset all users' BattlePass progress.")
    }

}