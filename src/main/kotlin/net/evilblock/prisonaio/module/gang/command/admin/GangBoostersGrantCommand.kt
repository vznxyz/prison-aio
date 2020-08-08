/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object GangBoostersGrantCommand {

    @Command(
        names = ["gang boosters grant", "gangs boosters grant", "gang booster grant", "gangs booster grants"],
        description = "Grant a Gang Booster to a gang",
        permission = Permissions.GANGS_BOOSTERS_GRANT,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "gang") gang: Gang, @Param(name = "booster") boosterType: GangBooster.BoosterType, @Param(name = "seconds") seconds: Int, @Param(name = "purchasedBy") purchasedBy: UUID) {
        gang.grantBooster(GangBooster(boosterType = boosterType, purchasedBy = purchasedBy, expiration = System.currentTimeMillis() + (seconds * 1000)))
        sender.sendMessage("${ChatColor.GREEN}You've granted the ${ChatColor.GOLD}${ChatColor.BOLD}${boosterType.rendered} ${ChatColor.GREEN}booster to gang `${gang.name}` for ${TimeUtil.formatIntoDetailedString(seconds)}.")
    }

}