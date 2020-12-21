package net.evilblock.prisonaio.module.kit.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitHandler
import net.evilblock.prisonaio.module.kit.KitsModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SaveCommand {

    @Command(
        names = ["kit save", "kits save"],
        description = "Saves the kit's inventory as the executor's inventory",
        permission = "kits.admin"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "kit") kit: Kit) {
        kit.items.clear()
        kit.items = player.inventory.storageContents.filterNotNull().toMutableList()

        Tasks.async {
            KitHandler.saveData()
        }

        player.updateInventory()
        player.sendMessage("${KitsModule.getChatPrefix()}${ChatColor.GREEN}Saved your inventory as the ${kit.name} ${ChatColor.GREEN}kit!")
    }

}