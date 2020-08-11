/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.enchant.type.Efficiency
import net.evilblock.prisonaio.module.enchant.type.Fortune
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PickaxeCommand {

    @Command(
        names = ["pickaxe", "pick"],
        description = "Spawn a pickaxe with enchantments pre-applied",
        permission = "prisonaio.enchants.pickaxe"
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player", defaultValue = "self") target: Player,
        @Param(name = "efficiencyLevel", defaultValue = "0") efficiencyLevel: Int,
        @Param(name = "fortuneLevel", defaultValue = "0") fortuneLevel: Int,
        @Param(name = "name", wildcard = true) name: String
    ) {
        var pickaxe = ItemBuilder.of(Material.DIAMOND_PICKAXE).also {
            if (!name.equals("none", ignoreCase = true)) {
                it.name(name)
            }
        }.build()

        val pickaxeData = PickaxeData()
        PickaxeHandler.trackPickaxeData(pickaxeData)

        pickaxe = pickaxeData.applyNBT(pickaxe)
        pickaxeData.applyMeta(pickaxe)

        if (efficiencyLevel > 0) {
            EnchantsManager.upgradeEnchant(target, pickaxeData, pickaxe, Efficiency, efficiencyLevel, true)
        }

        if (fortuneLevel > 0) {
            EnchantsManager.upgradeEnchant(target, pickaxeData, pickaxe, Fortune, fortuneLevel, true)
        }

        if (target.inventory.firstEmpty() == -1) {
            target.sendMessage("${ChatColor.RED}${ChatColor.BOLD}NOTICE: ${ChatColor.GRAY}You received a pickaxe but your inventory was full, so it has been added to your ender-chest.")
            sender.sendMessage("${ChatColor.GREEN}Pickaxe has been added to the player's ender-chest.")
            target.enderChest.addItem(pickaxe)
        } else {
            sender.sendMessage("${ChatColor.GREEN}Pickaxe has been added to the player's inventory.")
            target.inventory.addItem(pickaxe)
            target.updateInventory()
        }
    }

}