/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorSet
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

object GiveArmorPieceCommand {

    @Command(
        names = ["ability-armor give piece"],
        description = "Give a piece of an armor set to a player",
        permission = Permissions.ABILITY_ARMOR_GIVE
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player") player: Player,
        @Param(name = "set") set: AbilityArmorSet,
        @Param(name = "slot") slot: EquipmentSlot
    ) {
        when (slot) {
            EquipmentSlot.FEET -> {
                player.inventory.addItem(set.getBoots())
            }
            EquipmentSlot.LEGS -> {
                player.inventory.addItem(set.getLeggings())
            }
            EquipmentSlot.CHEST -> {
                player.inventory.addItem(set.getChestplate())
            }
            EquipmentSlot.HEAD -> {
                player.inventory.addItem(set.getHelmet())
            }
            else -> {
                player.sendMessage("${ChatColor.RED}Invalid equipment slot provided!")
                return
            }
        }

        player.updateInventory()

        sender.sendMessage("${ChatColor.GREEN}You gave ${ChatColor.WHITE}${player.name} ${ChatColor.GREEN}the ${ChatColor.WHITE}${slot.name} ${ChatColor.GREEN}piece of the ${set.setName} ${ChatColor.GREEN}set!")
    }

}