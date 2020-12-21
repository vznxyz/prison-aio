package net.evilblock.prisonaio.module.kit.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ResetCooldownCommand {

    @Command(
        names = ["kit reset-cooldown", "kits reset-cooldown"],
        description = "Reset a player's kit cooldown",
        permission = "kits.reset-cooldown"
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") player: Player, @Param(name = "kit") kit: Kit) {
        if (!kit.isCooldownSet()) {
            sender.sendMessage("${ChatColor.RED}That kit doesn't have a cooldown set!")
            return
        }

        if (!kit.cooldowns.containsKey(player.uniqueId)) {
            sender.sendMessage("${ChatColor.RESET}${player.name} ${ChatColor.RED}isn't on cooldown from redeeming the ${kit.name} ${ChatColor.RED}!")
            return
        }

        kit.cooldowns.remove(player.uniqueId)

        player.sendMessage("${KitsModule.getChatPrefix()}${ChatColor.GREEN}Your ${kit.name} ${ChatColor.GREEN}kit cooldown has been reset!")
        sender.sendMessage("${KitsModule.getChatPrefix()}${ChatColor.GREEN}You've reset ${ChatColor.RESET}${player.name}${ChatColor.GREEN}'s ${kit.name} ${ChatColor.GREEN}kit cooldown!")
    }

}