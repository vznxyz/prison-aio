package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitTask

object DropCommand {

    @Command(
        names = ["drop confirm"],
        description = "Drop your pickaxe"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.hasMetadata("CONFIRM_DROP")) {
            (player.getMetadata("CONFIRM_DROP")[0].value() as BukkitTask).cancel()
        }

        player.setMetadata("CONFIRM_DROP", FixedMetadataValue(PrisonAIO.instance, Tasks.asyncDelayed(20L * 10) {
            player.removeMetadata("CONFIRM_DROP", PrisonAIO.instance)
        }))

        player.sendMessage("${ChatColor.DARK_GRAY}[${ChatColor.RED}${ChatColor.BOLD}!${ChatColor.DARK_GRAY}] ${ChatColor.GRAY}You can drop your ${ChatColor.RED}pickaxe ${ChatColor.GRAY}for the next ${ChatColor.GREEN}10 seconds${ChatColor.GRAY}!")
    }

}