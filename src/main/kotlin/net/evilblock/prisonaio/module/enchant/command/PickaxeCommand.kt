package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.type.Efficiency
import net.evilblock.prisonaio.module.enchant.type.Fortune
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object PickaxeCommand {

    @Command(names = ["pickaxe", "pick"], description = "Spawn a pickaxe with enchantments pre-applied", permission = "op")
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player", defaultValue = "self") target: Player,
        @Param(name = "efficiencyLevel", defaultValue = "0") efficiencyLevel: Int,
        @Param(name = "fortuneLevel", defaultValue = "0") fortuneLevel: Int,
        @Param(name = "name", wildcard = true) name: String
    ) {
        val pickaxe = ItemStack(Material.DIAMOND_PICKAXE)

        if (!name.equals("none", ignoreCase = true)) {
            val itemMeta = pickaxe.itemMeta
            itemMeta.displayName = ChatColor.translateAlternateColorCodes('&', name)
            pickaxe.itemMeta = itemMeta
        }

        if (efficiencyLevel > 0) {
            EnchantsManager.addEnchant(pickaxe, Efficiency, efficiencyLevel, true)
        }

        if (fortuneLevel > 0) {
            EnchantsManager.addEnchant(pickaxe, Fortune, fortuneLevel, true)
        }

        if (target.inventory.firstEmpty() == -1) {
            target.sendMessage("${ChatColor.RED}${ChatColor.BOLD}NOTICE: ${ChatColor.GRAY}You received a pickaxe but your inventory was full, so it has been added to your ender-chest.")
            sender.sendMessage("${ChatColor.GREEN}Pickaxe has been added to player's ender-chest.")
            target.enderChest.addItem(pickaxe)
        } else {
            sender.sendMessage("${ChatColor.GREEN}Pickaxe has been added to player's inventory.")
            target.inventory.addItem(pickaxe)
            target.updateInventory()
        }
    }

}