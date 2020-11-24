/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.creation

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListingType
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class SelectListingTypeMenu(private val select: (GrandExchangeListingType) -> Unit) : Menu() {

    override fun getTitle(player: Player): String {
        return "What type of listing?"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[2] = PurchaseTypeButton()
            buttons[6] = AuctionTypeButton()

            for (i in 0 until 9) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    private inner class PurchaseTypeButton : TexturedHeadButton(PURCHASE_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Start Quick Sell"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.addAll(TextSplitter.split(text = "Quick sell means your item can be instantly purchased by any player for a fixed price that you set."))
                it.add("")
                it.addAll(TextSplitter.split(text = "If you prefer to sell the item you are listing quickly, then you should select this option."))
                it.add("")
                it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to start quick sell")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                select.invoke(GrandExchangeListingType.PURCHASE)
            }
        }
    }

    private inner class AuctionTypeButton : TexturedHeadButton(AUCTION_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Start an Auction"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.addAll(TextSplitter.split(text = "An auction listing is created with an initial price and duration. Players bid over each other by increased amounts until the countdown expires. The highest bid wins the item."))
                it.add("")
                it.addAll(TextSplitter.split(text = "If you wish to risk auctioning your item for more than what its worth, then you should select this option."))
                it.add("")
                it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to start auction")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                select.invoke(GrandExchangeListingType.AUCTION)
            }
        }
    }

    companion object {
        private const val AUCTION_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I5MjRiMzdmZTMyOTQyNzNhNzQzODZjODc4Y2EyMTBmYzg5ZjQ3ODcwMjk0M2EwMjcyZTcxMzk1NjMwYmVkYSJ9fX0="
        private const val PURCHASE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWRmYzkzZGE0MGNmM2ZjNDA1OTRjNWFmNDlmOGYyNWYwNzQxMzNmM2ZmMjZmODMxMTI3MWFkNGRhMTJkN2I4ZiJ9fX0="
    }

}