package net.evilblock.prisonaio.module.cell.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object RefreshCellValueCommand {

    @Command(
        names = ["cell admin refresh-cell-value"],
        description = "Forcefully refresh each cell's value",
        permission = Permissions.CELLS_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        var count = 0
        for (cell in CellHandler.getAllCells()) {
            cell.updateCachedCellValue()
            count++
        }

        sender.sendMessage("${ChatColor.GREEN}Refreshed $count cells!")
    }

}