/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.modifier.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.TextUtil
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifierType
import net.evilblock.prisonaio.module.generator.modifier.ModifierItemUtil
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GiveModifierCommand {

    @Command(
        names = ["generator give modifier"],
        description = "Give a Modifier item to a player",
        permission = Permissions.GENERATORS_GIVE
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player") player: Player,
        @Param(name = "modifier") modifierType: GeneratorModifierType,
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
            player.enderChest.addItem(ModifierItemUtil.toItemStack(modifierType, amount, value, duration))
        } else {
            player.inventory.addItem(ModifierItemUtil.toItemStack(modifierType, amount, value, duration))
        }

        sender.sendMessage("${GeneratorHandler.CHAT_PREFIX}You've given ${player.name} ${modifierType.color}${ChatColor.BOLD}${amount}x ${modifierType.getColoredName()} ${ChatColor.GRAY}${TextUtil.pluralize(amount, "modifier", "modifiers")}!")
        player.sendMessage("${GeneratorHandler.CHAT_PREFIX}You've been given ${modifierType.color}${ChatColor.BOLD}${amount}x ${modifierType.getColoredName()} ${ChatColor.GRAY}${TextUtil.pluralize(amount, "modifier", "modifiers")}!")
        player.updateInventory()
    }

}