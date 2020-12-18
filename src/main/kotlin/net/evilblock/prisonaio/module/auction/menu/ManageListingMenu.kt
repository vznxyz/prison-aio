/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.protocol.MenuCompatibility
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.auction.AuctionHouseModule
import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.listing.ListingType
import net.evilblock.prisonaio.module.auction.listing.bid.ListingBid
import net.evilblock.prisonaio.module.auction.notification.AHNotification
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.math.BigInteger
import java.util.*

class ManageListingMenu(private val previousMenu: Menu? = null, private val listing: Listing) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Manage Your Listing"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        val optionButtons = arrayListOf<Button>()
        if (listing.listingType == ListingType.AUCTION) {
            if (listing.isBINEnabled()) {
                optionButtons.add(UpdateBINPriceButton())
            }

            optionButtons.add(UpdateMinBidIncreaseButton())
            optionButtons.add(UpdateMaxBidIncreaseButton())
        } else {
            optionButtons.add(UpdateBINPriceButton())
        }

        optionButtons.add(FeatureListingButton())
        optionButtons.add(DeleteListingButton())

        var start = if (optionButtons.size > 4) {
            0
        } else {
            1
        }

        for (button in optionButtons) {
            buttons[start] = button
            start += 2
        }

        for (i in 0 until 9) {
            if (!buttons.containsKey(i)) {
                buttons[i] = GlassButton(7)
            }
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (previousMenu != null) {
            if (manualClose) {
                Tasks.delayed(1L) {
                    previousMenu.openMenu(player)
                }
            }
        }
    }

    private inner class UpdateBINPriceButton : TexturedHeadButton(texture = Constants.GOLD_DOLLAR_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Update BIN Price"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.addAll(TextSplitter.split(text = "Update the BIN price of your listing. The BIN price is the price that a player can instantly purchase your item for."))
                it.add("")

                if (listing.listingType == ListingType.AUCTION && listing.getLatestBid() != null) {
                    it.addAll(TextSplitter.split(linePrefix = ChatColor.RED.toString(), text = "You can't update your listing's BIN price as a bid has already been placed on it."))
                } else {
                    it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit BIN price")
                }
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (listing.listingType == ListingType.AUCTION && listing.getLatestBid() != null) {
                    player.sendMessage("${ChatColor.RED}You can't update your listing's BIN price as a bid has already been placed on it!")
                    return
                }

                val prompt = "${ChatColor.GREEN}How ${listing.getCurrencyType().getAmountContext()} ${listing.getCurrencyType().getName()} do you want the item to cost?"

                NumberPrompt().withText(prompt).acceptInput { number ->
                    try {
                        val bigNumber = NumberUtils.numberToBigInteger(number)
                        if (bigNumber <= BigInteger.ZERO) {
                            player.sendMessage("${ChatColor.RED}Your listing's BIN price must be at least ${listing.getCurrencyType().format(1)}.")
                            return@acceptInput
                        }

                        listing.updateBINPrice(bigNumber)

                        Tasks.async {
                            AuctionHouseHandler.saveListing(listing)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        player.sendMessage("${ChatColor.RED}Failed to update your listing's BIN price!")
                    }

                    this@ManageListingMenu.openMenu(player)
                }.start(player)
            }
        }
    }

    private inner class UpdateMinBidIncreaseButton : TexturedHeadButton(texture = Constants.WOOD_ARROW_DOWN_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Update Min Bid Increase"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.addAll(TextSplitter.split(text = "Update the amount of ${listing.getCurrencyType().getName()} the next bid should at least increase by."))
                it.add("")
                it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit min bid increase")
            }
        }
    }

    private inner class UpdateMaxBidIncreaseButton : TexturedHeadButton(texture = Constants.WOOD_ARROW_UP_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Update Max Bid Increase"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.addAll(TextSplitter.split(text = "Update the amount of ${listing.getCurrencyType().getName()} the next bid should at most increase by."))
                it.add("")
                it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit max bid increase")
            }
        }
    }

    private inner class FeatureListingButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Feature Listing"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.addAll(TextSplitter.split(text = "Want to sell your item fast? Feature your listing so more players see it more often."))
                it.add("")
                it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to feature your listing")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ITEM_FRAME
        }

        override fun getButtonItem(player: Player): ItemStack {
            return super.getButtonItem(player).also {
                GlowEnchantment.addGlow(it)
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (!canFeatureListing(player, listing)) {
                    return
                }

                ConfirmMenu(
                    title = "Feature for ${Formats.formatTokens(AuctionHouseModule.getFeatureListingPrice())}${ChatColor.RESET}?",
                    extraInfo = TextSplitter.split(text = "Pay to feature your listing for a fee of ${Formats.formatTokens(AuctionHouseModule.getFeatureListingPrice())}${ChatColor.GRAY}.")
                ) { confirmed ->
                    if (confirmed) {
                        listing.setFeatured()

                        Tasks.async {
                            AuctionHouseHandler.saveListing(listing)
                        }
                    }

                    this@ManageListingMenu.openMenu(player)
                }.openMenu(player)
            }
        }

        private fun canFeatureListing(player: Player, listing: Listing): Boolean {
            if (listing.isDeleted()) {
                player.sendMessage("${ChatColor.RED}That listing has been deleted!")
                return false
            }

            if (listing.isCompleted()) {
                player.sendMessage("${ChatColor.RED}That listing has been completed!")
                return false
            }

            if (listing.isFeatured()) {
                player.sendMessage("${ChatColor.RED}That listing is already featured!")
                return false
            }

            if (!Currency.Tokens.has(player.uniqueId, AuctionHouseModule.getFeatureListingPrice())) {
                player.sendMessage("${ChatColor.RED}You need ${Formats.formatTokens(AuctionHouseModule.getFeatureListingPrice())} ${ChatColor.RED}to feature your listing!")
                return false
            }

            return true
        }
    }

    private inner class DeleteListingButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Delete Listing"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.addAll(TextSplitter.split(text = "Delete your listing for a deletion fee of ${Formats.formatTokens(AuctionHouseHandler.DELETION_FEE)}${ChatColor.GRAY}. Your item will be safely returned and any bid(s) will be refunded."))
                it.add("")
                it.add("${ChatColor.RED}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.RED}to delete your listing")
            }
        }

        override fun getMaterial(player: Player): Material {
            return MenuCompatibility.getBarrierOrReplacement()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        if (listing.isDeleted()) {
                            player.sendMessage("${ChatColor.RED}That listing has been deleted!")
                            this@ManageListingMenu.openMenu(player)
                            return@ConfirmMenu
                        }

                        if (listing.isCompleted()) {
                            player.sendMessage("${ChatColor.RED}That listing has been completed!")
                            this@ManageListingMenu.openMenu(player)
                            return@ConfirmMenu
                        }

                        if (!Currency.Tokens.has(player.uniqueId, AuctionHouseHandler.DELETION_FEE)) {
                            player.sendMessage("${ChatColor.RED}You don't have enough tokens to pay the deletion fee of ${Formats.formatTokens(AuctionHouseHandler.DELETION_FEE)}${ChatColor.RED}!")
                            this@ManageListingMenu.openMenu(player)
                            return@ConfirmMenu
                        }

                        Currency.Tokens.take(player.uniqueId, AuctionHouseHandler.DELETION_FEE)

                        listing.setDeleted(player.uniqueId)

                        Tasks.async {
                            AuctionHouseHandler.saveListing(listing)

                            val vendor = UserHandler.getOrLoadAndCacheUser(listing.createdBy)
                            vendor.auctionHouseData.addReturnedListing(listing)

                            val returnedBids = hashMapOf<UUID, ListingBid>()
                            for (bid in listing.getBidHistory()) {
                                if (returnedBids.containsKey(bid.createdBy)) {
                                    if (bid.amount >= returnedBids[bid.createdBy]!!.amount) {
                                        returnedBids[bid.createdBy] = bid
                                    }
                                } else {
                                    returnedBids[bid.createdBy] = bid
                                }
                            }

                            for ((uuid, bid) in returnedBids) {
                                val user = UserHandler.getOrLoadAndCacheUser(uuid)
                                user.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}Your bid of ${listing.getCurrencyType().format(bid.amount)} ${ChatColor.GRAY}on ${ChatColor.RED}${listing.getCreatorUsername()}${ChatColor.GRAY}'s auction has been returned!"))

                                listing.getCurrencyType().give(uuid, bid.amount)
                            }
                        }

                        previousMenu?.openMenu(player)
                    } else {
                        this@ManageListingMenu.openMenu(player)
                    }
                }.openMenu(player)
            }
        }
    }

}