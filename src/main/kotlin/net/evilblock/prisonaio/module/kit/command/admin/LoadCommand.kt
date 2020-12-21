package net.evilblock.prisonaio.module.kit.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitsModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object LoadCommand {

    @Command(
        names = ["kit load", "kits load"],
        description = "Loads the kit's inventory as the executor's inventory",
        permission = "kits.admin"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "kit") kit: Kit) {
        player.inventory.clear()
        player.inventory.armorContents = arrayOfNulls(4)

        kit.giveItems(player)

        player.updateInventory()
        player.sendMessage("${KitsModule.getChatPrefix()}Loaded the ${kit.name} ${ChatColor.GRAY}kit as your inventory!")
    }

}