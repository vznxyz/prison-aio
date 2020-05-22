package net.evilblock.prisonaio.module.cell.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object CellParameterType : ParameterType<Cell> {

    override fun transform(sender: CommandSender, source: String): Cell? {
        if (source == "self") {
            val visitingCell = CellHandler.getVisitingCell(sender as Player)
            if (visitingCell != null) {
                return visitingCell
            }

            sender.sendMessage("${ChatColor.RED}You are not in a cell right now.")
            return null
        }

        try {
            val optionalCell = CellHandler.getCellByUuid(UUID.fromString(source))
            if (optionalCell.isPresent) {
                return optionalCell.get()
            }

            sender.sendMessage("${ChatColor.RED}Couldn't find a cell with that ID")
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Couldn't parse UUID.")
        }

        return null
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return emptyList()
    }

}