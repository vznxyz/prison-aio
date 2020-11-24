/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.perk.Perk
import net.evilblock.prisonaio.module.user.perk.PerkGrant
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object PerksGrantCommand {

    private const val NO_METADATA = "__NONE__"

    @Command(
        names = ["user perks grant"],
        description = "Grant a user a perk",
        permission = "prisonaio.user.perks.grant",
        async = true
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player") user: User,
        @Param(name = "perk") perk: Perk,
        @Param(name = "duration") duration: Duration,
        @Param(name = "metadata", defaultValue = NO_METADATA) metadata: String,
        @Param(name = "reason", defaultValue = "Unspecified", wildcard = true) reason: String
    ) {
        val issuedBy: UUID? = if (sender is Player) {
            sender.uniqueId
        } else {
            null
        }

        val grant = PerkGrant(perk = perk, issuedBy = issuedBy, duration = duration.get(), reason = reason)
        user.perks.trackPerkGrant(grant)

        if (metadata == NO_METADATA) {
            if (perk == Perk.SALES_BOOST) {
                sender.sendMessage("${ChatColor.RED}To grant a shop multiplier, you must provide the multiplier as the metadata.")
                return
            }
        } else {
            if (perk == Perk.SALES_BOOST) {
                try {
                    grant.metadata.addProperty("multiplier", metadata.toDouble())
                } catch (e: Exception) {
                    sender.sendMessage("${ChatColor.RED}Failed to parse metadata into multiplier.")
                    return
                }
            }
        }

        val player = Bukkit.getPlayer(user.uuid)
        if (player != null) {
            val messages = arrayListOf<String>()

            messages.add("")
            messages.add(" ${ChatColor.GOLD}${ChatColor.BOLD}${perk.displayName} Perk Granted")

            if (duration.isPermanent()) {
                messages.addAll(TextSplitter.split(
                    length = 50,
                    text = "You have been granted the ${perk.displayName} perk for a period of ${ChatColor.YELLOW}forever${ChatColor.GRAY}.",
                    linePrefix = ChatColor.GRAY.toString()
                ))
            } else {
                val formattedDuration = TimeUtil.formatIntoDetailedString((duration.get() / 1000.0).toInt())
                messages.addAll(TextSplitter.split(
                    length = 50,
                    text = "You have been granted the ${perk.displayName} perk for a period of ${ChatColor.YELLOW}$formattedDuration${ChatColor.GRAY}.",
                    linePrefix = ChatColor.GRAY.toString()
                ))
            }

            messages.add("")
            messages.forEach { player.sendMessage(it) }
        }

        if (duration.isPermanent()) {
            sender.sendMessage("${ChatColor.GREEN}You granted the ${ChatColor.YELLOW}${perk.displayName} ${ChatColor.GREEN}perk to ${ChatColor.WHITE}${user.getUsername()} ${ChatColor.GREEN}for a period of ${ChatColor.YELLOW}forever${ChatColor.GREEN}.")
        } else {
            val formattedDuration = TimeUtil.formatIntoDetailedString((duration.get() / 1000.0).toInt())
            player.sendMessage("${ChatColor.GREEN}You granted the ${ChatColor.YELLOW}${perk.displayName} ${ChatColor.GREEN}perk to ${ChatColor.WHITE}${user.getUsername()} ${ChatColor.GREEN}for a period of ${ChatColor.YELLOW}$formattedDuration${ChatColor.GREEN}.")
        }
    }

}