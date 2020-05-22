package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.shop.menu.ShopEditorMenu
import org.bukkit.entity.Player

object ShopEditorCommand {

    @Command(names = ["prison shop editor"], description = "Open the shop editor", permission = "prisonaio.shops.editor")
    @JvmStatic
    fun execute(player: Player) {
        ShopEditorMenu().openMenu(player)
    }

}