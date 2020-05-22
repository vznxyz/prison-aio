package net.evilblock.prisonaio.module.crate.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CratesModule
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CratePlaceCommand {

    @Command(
        names = ["crate place", "prison crate place"],
        description = "Give yourself a crate to place",
        permission = Permissions.CRATES_ADMIN
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "crate") crate: Crate) {
        player.inventory.addItem(crate.toItemStack())
        player.updateInventory()
        player.sendMessage("${CratesModule.getChatPrefix()}You have given yourself a ${crate.name} ${ChatColor.GRAY}crate.")
    }

}