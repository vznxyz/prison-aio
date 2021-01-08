/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.TextUtil
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.impl.modifier.RobotModifierType
import net.evilblock.prisonaio.module.robot.impl.modifier.RobotModifierUtils
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GiveModifierCommand {

    @Command(
        names = ["robot give modifier", "robots give modifier"],
        description = "Give a Modifier item to a player",
        permission = Permissions.GENERATORS_GIVE
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player") player: Player,
        @Param(name = "modifier") modifierType: RobotModifierType,
        @Param(name = "amount") amount: Int,
        @Param(name = "value") value: Double,
        @Param(name = "duration", defaultValue = "NOT_PROVIDED") duration: Duration
    ) {
        if (modifierType.durationBased && duration.get() == -1L) {
            sender.sendMessage("${ChatColor.RED}You must provide a duration for the ${modifierType.displayName} Modifier!")
            return
        }

        if (amount < 1 || amount > 64) {
            sender.sendMessage("${ChatColor.RED}The quantity must be in the range 1-64!")
            return
        }

        if (player.inventory.firstEmpty() == -1) {
            player.enderChest.addItem(RobotModifierUtils.makeModifierItemStack(modifierType, amount, value, duration))
        } else {
            player.inventory.addItem(RobotModifierUtils.makeModifierItemStack(modifierType, amount, value, duration))
        }

        sender.sendMessage("${RobotsModule.CHAT_PREFIX}You've given ${player.name} ${modifierType.color}${ChatColor.BOLD}${amount}x ${modifierType.getColoredName()} ${ChatColor.GRAY}${TextUtil.pluralize(amount, "modifier", "modifiers")}!")
        player.sendMessage("${RobotsModule.CHAT_PREFIX}You've been given ${modifierType.color}${ChatColor.BOLD}${amount}x ${modifierType.getColoredName()} ${ChatColor.GRAY}${TextUtil.pluralize(amount, "modifier", "modifiers")}!")
        player.updateInventory()
    }

}