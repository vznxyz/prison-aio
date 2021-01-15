/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.damage.DamageTracker
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ClearDamageCacheCommand {

    @Command(
        names = ["clear-damage-cache"],
        description = Permissions.SYSTEM_ADMIN,
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        DamageTracker.clearDamageCache()
        sender.sendMessage("${ChatColor.GREEN}Cleared damage cache!")
    }

}