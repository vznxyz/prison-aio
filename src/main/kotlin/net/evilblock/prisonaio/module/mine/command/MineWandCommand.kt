package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mechanic.region.selection.RegionSelection
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineWandCommand {

    @Command(names = ["mine wand"], description = "Give yourself the region selection wand", permission = "prisonaio.mines.wand")
    @JvmStatic
    fun execute(player: Player) {
        player.inventory.addItem(RegionSelection.SELECTION_ITEM)
        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}You have given yourself the region selection wand.")
    }

}