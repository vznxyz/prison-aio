package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import org.bukkit.entity.Player

object ShopReceiptCommand {

    @Command(
        names = ["receipt"],
        description = "Display a receipt"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "receipt") receipt: ShopReceipt) {
        receipt.sendDetailed(player)
    }

}