/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Permissions
import org.bson.Document
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object UserResetCommand {

    @Command(
        names = ["user reset"],
        description = "Wipe a user's data",
        permission = Permissions.USERS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") user: User) {
        UserHandler.getCollection().deleteOne(Document("uuid", user.uuid.toString()))
        sender.sendMessage("${ChatColor.GREEN}Successfully reset ${user.getUsername()}'s user data.")
    }

}