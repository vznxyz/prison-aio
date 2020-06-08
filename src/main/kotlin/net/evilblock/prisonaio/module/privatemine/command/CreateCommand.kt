package net.evilblock.prisonaio.module.privatemine.command

import net.evilblock.prisonaio.module.privatemine.PrivateMinesModule
import net.evilblock.prisonaio.module.privatemine.data.PrivateMineTier
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.lang.IllegalStateException
import java.util.*

object CreateCommand {

    @Command(
        names = ["privatemine create", "pmine create"],
        permission = "op",
        async = true
    )
    @JvmStatic fun execute(sender: CommandSender, @Param("player") uuid: UUID, @Param("tier") tier: Int) {
        val mineTier = PrivateMineTier.fromInt(tier)

        if (mineTier == null) {
            sender.sendMessage("${ChatColor.RED}Mine tier $tier isn't registered.")
            return
        }

        try {
            PrivateMineHandler.createMine(uuid, mineTier)

            val player = Bukkit.getPlayer(uuid) ?: return

            PrivateMinesModule.getNotificationLines("mine-created").forEach {
                player.sendMessage(it.replace("{tier}", mineTier.number.toString()))
            }
        } catch (e: IllegalStateException) {
            sender.sendMessage("${ChatColor.RED}Failed to generate private mine. Please contact an admin.")

            if (sender.isOp) {
                sender.sendMessage("${ChatColor.RED}${e.message}")
            }

            e.printStackTrace()
        }
    }

}