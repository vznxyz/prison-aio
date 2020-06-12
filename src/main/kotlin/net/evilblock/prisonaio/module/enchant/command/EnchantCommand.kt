package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.PurchaseEnchantMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EnchantCommand {

    @Command(
        names = ["enchant", "ench"],
        description = "Enchant your pickaxe"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.inventory.itemInMainHand == null || !player.inventory.itemInHand.type.name.endsWith("_PICKAXE")) {
            player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You must be holding the pickaxe you would like to enchant.")
            return
        }

        PurchaseEnchantMenu(player.inventory.itemInMainHand).openMenu(player)
    }

}