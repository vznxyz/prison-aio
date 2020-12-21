package net.evilblock.prisonaio.module.kit.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitHandler
import net.evilblock.prisonaio.module.kit.menu.EditKitMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CreateCommand {

    @Command(
        names = ["kit create", "kits create"],
        description = "Creates a new kit from the executor's inventory",
        permission = "kits.give"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "name") kitId: String) {
        var kit = KitHandler.getKitById(kitId)
        if (kit != null) {
            player.sendMessage("${ChatColor.RED}A kit with an ID of ${ChatColor.WHITE}`$kitId` ${ChatColor.RED}already exists!")
            return
        }

        kit = Kit(kitId)
        kit.items = player.inventory.storageContents.filterNotNull().toMutableList()

        KitHandler.trackKit(kit)

        Tasks.async {
            KitHandler.saveData()
        }

        EditKitMenu(kit).openMenu(player)
    }

}