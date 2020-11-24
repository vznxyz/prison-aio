/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.layout

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListingType
import net.evilblock.prisonaio.module.exchange.menu.display.BidHistoryDisplay
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit
import kotlin.math.min

object GrandExchangeLayout {

    private val GLASS_SLOTS = listOf(0, 2, 4, 6, 8, 18, 27, 36)
    private const val BIDS_SHOWN_AT_ONCE = 5

    @JvmStatic
    fun renderLayout(buttons: MutableMap<Int, Button>) {
        for (i in GLASS_SLOTS) {
            buttons[i] = GlassButton(7)
        }
    }

    @JvmStatic
    fun renderListingExpiration(listing: GrandExchangeListing, list: MutableList<String>) {
        val durationMS = listing.duration.get()
        val remainingSeconds = (((listing.createdAt + durationMS) - System.currentTimeMillis()) / 1000.0).toInt()

        val expirationColor: ChatColor = when {
            remainingSeconds >= TimeUnit.MINUTES.toSeconds(30L) -> {
                ChatColor.GREEN
            }
            remainingSeconds >= TimeUnit.MINUTES.toSeconds(15L) -> {
                ChatColor.YELLOW
            }
            else -> {
                ChatColor.RED
            }
        }

        list.add("${ChatColor.GRAY}Expires: $expirationColor${ChatColor.BOLD}${TimeUtil.formatIntoAbbreviatedString(remainingSeconds)}")
    }

    @JvmStatic
    fun renderListingStatus(player: Player, listing: GrandExchangeListing, list: MutableList<String>) {
        val context = if (player.uniqueId == listing.createdBy) {
            "Your"
        } else {
            "This"
        }

        when {
            listing.wasPurchased() -> {
                when (listing.listingType) {
                    GrandExchangeListingType.AUCTION -> {
                        list.add("${ChatColor.GREEN}${ChatColor.BOLD}Auction Complete")

                        if (listing.wasPurchasedByBIN()) {
                            list.addAll(TextSplitter.split(text = "$context item was purchased via BIN by ${ChatColor.AQUA}${listing.getPurchasedByUsername()} ${ChatColor.GRAY}for ${listing.getCurrencyType().format(listing.getBINPrice())}${ChatColor.GRAY}."))
                        } else {
                            list.addAll(TextSplitter.split(text = "$context item auction was sold to ${ChatColor.AQUA}${listing.getPurchasedByUsername()} ${ChatColor.GRAY}for ${listing.getCurrencyType().format(listing.getBINPrice())}${ChatColor.GRAY}."))
                        }
                    }
                    GrandExchangeListingType.PURCHASE -> {
                        list.add("${ChatColor.GREEN}${ChatColor.BOLD}Listing Complete")
                        list.addAll(TextSplitter.split(text = "$context item was purchased by ${ChatColor.AQUA}${listing.getPurchasedByUsername()} ${ChatColor.GRAY}for ${listing.getCurrencyType().format(listing.getBINPrice())}${ChatColor.GRAY}."))
                    }
                }
            }
            listing.isDeleted() -> {
                list.add("${ChatColor.RED}${ChatColor.BOLD}Listing Deleted")

                if (listing.getDeletedBy() == listing.createdBy) {
                    list.addAll(TextSplitter.split(text = "This listing was deleted by it's creator."))
                } else {
                    val reason = listing.getDeletedReason() ?: "No reason provided"
                    list.addAll(TextSplitter.split(text = "This listing was deleted by ${ChatColor.RED}${listing.getDeletedByUsername()} ${ChatColor.GRAY}for: $reason"))
                }
            }
            listing.isExpired() -> {
                list.add("${ChatColor.RED}${ChatColor.BOLD}Listing Expired")

                if (player.uniqueId == listing.createdBy) {
                    list.addAll(TextSplitter.split(text = "Your listing has expired! You can retrieve your items by clicking the mail button at the top of the menu."))
                } else {
                    list.addAll(TextSplitter.split(text = "This listing has expired!"))
                }
            }
        }
    }

    @JvmStatic
    fun renderListingInformation(player: Player, listing: GrandExchangeListing, list: MutableList<String>) {
        list.add("${ChatColor.GRAY}${ChatColor.BOLD}${ChatColor.STRIKETHROUGH}-----------------------")
        list.add("${ChatColor.YELLOW}${ChatColor.BOLD}Listing Information")
        list.add("${ChatColor.GRAY}Created By: ${ChatColor.RED}${listing.getCreatorUsername()}")

        when (listing.listingType) {
            GrandExchangeListingType.AUCTION -> {
                if (listing.isBINEnabled()) {
                    list.add("${ChatColor.GRAY}BIN Price: ${listing.getCurrencyType().format(listing.getBINPrice())}")
                }

                list.add("${ChatColor.GRAY}Min Bid Increase: ${listing.getCurrencyType().format(listing.getBidMinIncrease())}")
                list.add("${ChatColor.GRAY}Max Bid Increase: ${listing.getCurrencyType().format(listing.getBidMaxIncrease())}")

                if (!listing.isCompleted()) {
                    renderListingExpiration(listing, list)
                }

                list.add("")
                list.add("${ChatColor.YELLOW}${ChatColor.BOLD}Bid History")

                if (listing.getBidHistory().isEmpty()) {
                    list.add("${ChatColor.GRAY}No bids have been placed!")
                } else {
                    if (BidHistoryDisplay.isDisplayingBidHistory(player, listing)) {
                        list.add("${ChatColor.AQUA}${ChatColor.BOLD}Press Q to Hide")
                        list.add("")

                        val latestBid = listing.getLatestBid()
                        val sortedBids = listing.getBidHistory().sortedBy { it.createdAt }.reversed()

                        for ((index, bid) in sortedBids.withIndex()) {
                            if (latestBid != null && latestBid.id == bid.id) {
                                if (listing.isCompleted() && listing.wasPurchased() && !listing.wasPurchasedByBIN()) {
                                    list.add("${ChatColor.GREEN}${ChatColor.BOLD}WINNING BID")
                                } else {
                                    list.add("${ChatColor.YELLOW}${ChatColor.BOLD}LATEST BID")
                                }
                            }

                            list.add("${ChatColor.AQUA}${bid.getCreatorUsername()} ${ChatColor.DARK_AQUA}- ${listing.getCurrencyType().format(bid.amount)}")

                            val secondsPassed = ((System.currentTimeMillis() - bid.createdAt) / 1000.0).toInt()
                            list.add("${ChatColor.GRAY}(Placed ${TimeUtil.formatIntoDetailedString(secondsPassed)} ago${ChatColor.GRAY})")

                            if (index != min(BIDS_SHOWN_AT_ONCE, sortedBids.size) - 1) {
                                list.add("")
                            }

                            if (index >= BIDS_SHOWN_AT_ONCE - 1) {
                                break
                            }
                        }
                    } else {
                        list.add("${ChatColor.AQUA}${ChatColor.BOLD}Press Q to Show")
                    }
                }

                if (listing.isCompleted()) {
                    list.add("")

                    renderListingStatus(player, listing, list)
                }
            }
            GrandExchangeListingType.PURCHASE -> {
                list.add("${ChatColor.GRAY}Price: ${listing.getCurrencyType().format(listing.getBINPrice())}")

                if (listing.isCompleted()) {
                    list.add("")

                    renderListingStatus(player, listing, list)
                } else {
                    renderListingExpiration(listing, list)
                }
            }
        }
    }

}