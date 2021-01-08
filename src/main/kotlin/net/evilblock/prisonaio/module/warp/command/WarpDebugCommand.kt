/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.warp.Warp
import net.evilblock.prisonaio.module.warp.WarpHandler
import org.bukkit.command.CommandSender

object WarpDebugCommand {

    @Command(
        names = ["warp debug", "warps debug"],
        description = "Fix warps",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        val broken = arrayListOf<Warp>()
        for (warp in WarpHandler.getWarps()) {
            if (warp.location.world == null) {
                broken.add(warp)
            }
        }

        for (warp in broken) {
            WarpHandler.forgetWarp(warp)
        }

        WarpHandler.saveData()

        sender.sendMessage("Fixed ${broken.size} broken warps")
    }

}