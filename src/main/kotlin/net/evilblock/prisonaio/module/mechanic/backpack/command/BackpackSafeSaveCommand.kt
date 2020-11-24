/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import org.bukkit.command.CommandSender

object BackpackSafeSaveCommand {

    @Command(
        names = ["backpack safe-save"],
        description = "Tries to safely save backpack data",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        BackpackHandler.safeSave()
    }

}