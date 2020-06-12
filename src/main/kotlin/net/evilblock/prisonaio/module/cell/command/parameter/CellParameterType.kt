package net.evilblock.prisonaio.module.cell.command.parameter

import net.evilblock.cubed.Cubed
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
            val cell = CellHandler.getAssumedCell((sender as Player).uniqueId)
            if (cell == null) {
                sender.sendMessage("${ChatColor.RED}You are not in a cell right now.")
            }
            return cell
        }

        val cell = CellHandler.getCellByName(source)
        if (cell != null) {
            try {
                return CellHandler.getCellByUuid(UUID.fromString(source))
            } catch (e: Exception) { }
            return cell
        }

        val playerUuid = Cubed.instance.uuidCache.uuid(source)
        if (playerUuid != null) {
            val assumedCell = CellHandler.getAssumedCell(playerUuid)
            if (assumedCell != null) {
                return assumedCell
            }
        }

        sender.sendMessage("${ChatColor.RED}Couldn't find a cell with that name or ID.")

        return null
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return emptyList()
    }

}