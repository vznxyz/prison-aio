package net.evilblock.kits.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.kits.Kit
import net.evilblock.kits.event.RedeemKitEvent
import net.evilblock.prisonaio.module.kit.KitsModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object KitCommand {

    @Command(
        names = ["kit"],
        description = "Redeem a specific kit"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "kit") kit: Kit) {
        if (kit.requiresPermission) {
            if (!kit.hasPermission(player)) {
                player.sendMessage("${ChatColor.RED}You don't have permission to use that kit!")
                return
            }
        }

        if (kit.isCooldownSet()) {
            if (kit.isOnCooldown(player)) {
                val formattedCooldown = TimeUtil.formatIntoDetailedString((kit.getRemainingCooldown(player) / 1000.0).toInt())
                player.sendMessage("${ChatColor.RED}You can't redeem this kit for another ${ChatColor.BOLD}$formattedCooldown${ChatColor.RED}!")
                return
            }
        }

        val event = RedeemKitEvent(player, kit)
        event.call()

        if (!event.isCancelled) {
            if (kit.isCooldownSet()) {
                kit.applyCooldown(player)
            }

            kit.giveItems(player)

            player.sendMessage("${KitsModule.getChatPrefix()}${ChatColor.GRAY}You have received the ${kit.name} ${ChatColor.GRAY}kit!")
        }
    }

}