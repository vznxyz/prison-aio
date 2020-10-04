/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player

object ForgetPickaxeCommand {

    @Command(
        names = ["forget-pickaxe"],
        description = "Removes a pickaxe's attached ID",
        permission = "prisonaio.pickaxe.forget"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.inventory.itemInMainHand == null) {
            player.sendMessage("${ChatColor.RED}You need to hold the pickaxe in your hand!")
            return
        }

        if (!MechanicsModule.isPickaxe(player.inventory.itemInMainHand)) {
            player.sendMessage("${ChatColor.RED}The item in your hand is not a pickaxe!")
            return
        }

        val pickaxeData = PickaxeHandler.getPickaxeData(player.inventory.itemInMainHand)
        if (pickaxeData != null) {
            PickaxeHandler.forgetPickaxeData(pickaxeData)

            val nmsCopy = CraftItemStack.asNMSCopy(player.inventory.itemInMainHand)
            var tag: NBTTagCompound? = nmsCopy.tag

            if (tag == null) {
                tag = NBTTagCompound()
                nmsCopy.tag = tag
            }

            player.inventory.itemInMainHand = CraftItemStack.asBukkitCopy(nmsCopy)
            player.updateInventory()

            player.sendMessage("${ChatColor.GREEN}Removed the pickaxe's attached ID!")
        }
    }

}