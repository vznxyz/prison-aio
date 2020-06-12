package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.PurchaseEnchantMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RemoveEnchantCommand {

    @Command(
        names = ["remove-enchant", "remove-ench"],
        description = "Remove an enchant from a pickaxe",
        permission = "op"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "enchant") enchant: AbstractEnchant) {
        if (player.inventory.itemInMainHand == null || !player.inventory.itemInHand.type.name.endsWith("_PICKAXE")) {
            player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You must be holding the pickaxe you would like to remove an enchant from.")
            return
        }

        EnchantsManager.removeEnchant(player.inventory.itemInMainHand, enchant)
        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}Removed enchant!")
    }

}