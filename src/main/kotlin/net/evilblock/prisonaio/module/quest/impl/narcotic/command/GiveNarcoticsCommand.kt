package net.evilblock.prisonaio.module.quest.impl.narcotic.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.quest.impl.narcotic.Narcotic
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GiveNarcoticsCommand {

    @Command(
        names = ["quest item give narcotic"],
        description = "Give a narcotic to a player",
        permission = Permissions.QUESTS_ADMIN
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player", defaultValue = "self") player: Player,
        @Param(name = "narcotic") narcotic: Narcotic,
        @Param(name = "amount") amount: Int
    ) {
        assert(amount in 1..64) { "Amount must be within 1-64" }

        val itemStack = narcotic.toItemStack(amount)
        val formattedItem = "${narcotic.textColor}x$amount ${narcotic.displayName}"

        if (player.inventory.firstEmpty() == -1) {
            player.enderChest.addItem(itemStack)
            player.sendMessage("${ChatColor.RED}You received $formattedItem ${ChatColor.RED}but your inventory was full, so it has been added to your ${ChatColor.DARK_PURPLE}ender-chest${ChatColor.RED}.")
        } else {
            player.inventory.addItem(itemStack)
        }

        player.updateInventory()

        sender.sendMessage("${ChatColor.GREEN}You gave $formattedItem ${ChatColor.GREEN}to ${ChatColor.WHITE}${player.name}${ChatColor.GREEN}.")
    }

}