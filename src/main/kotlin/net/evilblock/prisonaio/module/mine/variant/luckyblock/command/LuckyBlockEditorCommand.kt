/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mine.variant.luckyblock.menu.LuckyBlockEditor
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object LuckyBlockEditorCommand {

    @Command(
        names = ["luckyblock editor"],
        description = "Opens the LuckyBlock editor",
        permission = Permissions.SYSTEM_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        LuckyBlockEditor().openMenu(player)
    }

}