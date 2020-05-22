package net.evilblock.prisonaio.module.cell.listener

import net.evilblock.prisonaio.module.cell.CellHandler
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object CellChatListeners : Listener {

    @EventHandler
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        if (event.message.startsWith("@")) {
            val visitingCell = CellHandler.getVisitingCell(event.player)
            if (visitingCell != null) {
                event.isCancelled = true

                val message = StringBuilder()
                    .append("${ChatColor.GRAY}[${ChatColor.YELLOW}${visitingCell.name}${ChatColor.GRAY}] ")
                    .append("${ChatColor.GREEN}${event.player.name}${ChatColor.GRAY}: ${event.message.substring(0, event.message.length - 1)}")
                    .toString()

                visitingCell.sendMessages(message)
            }
        }
    }

}