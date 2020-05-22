package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.enchant.menu.PurchaseEnchantMenu
import org.bukkit.entity.Player

object EnchantCommand {

    @Command(names = ["enchant", "ench"], description = "Enchant your pickaxe")
    @JvmStatic
    fun execute(player: Player) {
        PurchaseEnchantMenu.tryOpeningMenu(player)
    }

}