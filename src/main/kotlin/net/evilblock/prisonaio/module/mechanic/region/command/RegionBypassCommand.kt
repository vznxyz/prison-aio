package net.evilblock.prisonaio.module.mechanic.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mechanic.region.bypass.RegionBypass
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RegionBypassCommand {

    @Command(["region bypass"], description = "Grants bypass for interactions in regions owned by others", permission = "prisonaio.regions.bypass")
    @JvmStatic
    fun execute(player: Player) {
        val bypass = !RegionBypass.hasBypass(player)
        RegionBypass.setBypass(player, bypass)

        val color = if (bypass) { ChatColor.GREEN } else { ChatColor.RED }
        player.sendMessage("${ChatColor.YELLOW}You have toggled your region bypass to: $color$bypass")
    }

}