package net.evilblock.prisonaio.module.privatemine.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.command.CommandSender

object ResetCommand {

    @Command(
        names = ["pmine reset-all"],
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        for (pmine in PrivateMineHandler.getAllMines()) {
            pmine.resetRegion()
        }
        sender.sendMessage("done!")
    }

}