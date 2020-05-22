package net.evilblock.prisonaio.module.cell.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.CellHandler
import net.evilblock.prisonaio.module.mechanic.region.bypass.RegionBypass
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object CellResetCommand {

    @Command(
        names = ["cell reset"],
        description = "Reset a cell to its original state"
    )
    @JvmStatic
    fun execute(player: Player) {
        val visitingCell = CellHandler.getVisitingCell(player)
        if (visitingCell == null) {
            player.sendMessage("${ChatColor.RED}You must be inside a cell to reset it.")
            return
        }

        if (CellHandler.hasBypass(player)) {
            if (!RegionBypass.hasReceivedNotification(player)) {
                RegionBypass.sendNotification(player)
            }

            openConfirmationMenu(player, visitingCell)
        } else {
            if (!visitingCell.isOwner(player.uniqueId)) {
                player.sendMessage("${ChatColor.RED}You must be the owner of the cell to reset it.")
                return
            }

            openConfirmationMenu(player, visitingCell)
        }
    }

    private fun openConfirmationMenu(player: Player, cell: Cell) {
        ConfirmMenu("${ChatColor.DARK_RED}${ChatColor.BOLD}RESET CELL?") { confirmed ->
            if (confirmed) {
                try {
                    CellHandler.resetCell(cell) {
                        player.sendMessage("${ChatColor.GREEN}Successfully reset your cell.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    player.sendMessage("${ChatColor.RED}Failed to reset your cell.")

                    for (activePlayer in cell.getActivePlayers()) {
                        activePlayer.teleport(cell.homeLocation)
                    }
                }
            } else {
                player.sendMessage("${ChatColor.YELLOW}No changes made to cell.")
            }
        }.openMenu(player)
    }

}