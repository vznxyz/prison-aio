/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.reward.deliveryman.menu.DeliveryManEditorMenu
import org.bukkit.entity.Player

object DeliveryManEditorCommand {

    @Command(
        names = ["deliveryman editor"],
        description = "Open the Delivery Man Editor",
        permission = "prisonaio.deliveryman.editor"
    )
    @JvmStatic
    fun execute(player: Player) {
        DeliveryManEditorMenu().openMenu(player)
    }

}