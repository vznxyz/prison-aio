/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.listing.ListingType
import net.evilblock.prisonaio.module.auction.menu.button.AllListingsButton
import net.evilblock.prisonaio.module.auction.menu.display.BidHistoryDisplay
import net.evilblock.prisonaio.module.auction.menu.layout.AuctionHouseLayout
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class CollectItemsMenu(private val user: User) : BrowseListingsMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Collect Items"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return (super.getGlobalButtons(player) as MutableMap).also {
            it[4] = AllListingsButton()
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        val originalListings = getListings(player)
        var filteredListings = arrayListOf<Listing>()

        if (currentFilters.isEmpty()) {
            filteredListings = ArrayList(originalListings)
        } else {
            for (filter in currentFilters) {
                filteredListings.addAll(filter.apply(originalListings).filter { !filteredListings.contains(it) })
            }
        }

        filteredListings = ArrayList(selectedSort.apply(filteredListings))

        for (listing in filteredListings) {
            buttons[buttons.size] = CollectListingButton(listing)
        }

        return buttons
    }

    override fun getListings(player: Player): Collection<Listing> {
        return user.auctionHouseData.getUnclaimedListings()
    }

    inner class CollectListingButton(private val listing: Listing) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            val info = arrayListOf<String>()

            AuctionHouseLayout.renderListingInformation(player, listing, info)

            if (info.last() != "") {
                info.add("")
            }

            info.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to collect this item")

            return ItemBuilder.copyOf(listing.getGoods()).addToLore(*info.toTypedArray()).build().also {
                val itemMeta = it.itemMeta
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

                it.itemMeta = itemMeta
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (listing.listingType == ListingType.AUCTION && (clickType == ClickType.MIDDLE || clickType == ClickType.CREATIVE)) {
                BidHistoryDisplay.toggleBidHistoryDisplay(player, listing)
                return
            }

            if (clickType.isLeftClick) {
                if (player.inventory.firstEmpty() == -1) {
                    player.sendMessage("${ChatColor.RED}You don't have enough free inventory space!")
                    return
                }

                if (listing.isClaimed()) {
                    player.sendMessage("${ChatColor.RED}That item has already been collected!")
                    return
                }

                listing.setClaimed()

                Tasks.async {
                    AuctionHouseHandler.saveListing(listing)
                }

                val unreturnedItems = player.inventory.addItem(listing.getGoods())
                if (unreturnedItems.isNotEmpty()) {
                    player.sendMessage("${ChatColor.RED}You didn't have enough inventory space, so some of the items went into your enderchest!")

                    for (item in unreturnedItems.values) {
                        player.enderChest.addItem(item)
                    }
                }

                player.updateInventory()
            }
        }
    }

}