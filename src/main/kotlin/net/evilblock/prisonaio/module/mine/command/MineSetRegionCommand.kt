package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mechanic.region.selection.RegionSelection
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineSetRegionCommand {

    @Command(
        names = ["mine setregion"],
        description = "Set the region of a mine",
        permission = "prisonaio.mines.setregion"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "mine") mine: Mine) {
        if (!RegionSelection.hasSelection(player)) {
            player.sendMessage(RegionSelection.FINISH_SELECTION)
            return
        }

        // update the mine's region to the player's selection
        mine.region = RegionSelection.getSelection(player)

        // save changes to file
        MineHandler.recalculateCoordsMap()
        MineHandler.saveData()

        // send update message
        player.sendMessage("${ChatColor.GREEN}Updated mine ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}'s region.")
    }

}