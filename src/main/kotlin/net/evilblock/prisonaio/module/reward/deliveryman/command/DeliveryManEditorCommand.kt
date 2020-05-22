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