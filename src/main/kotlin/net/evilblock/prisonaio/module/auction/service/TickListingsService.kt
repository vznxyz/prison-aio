/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.service

import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.auction.listing.ListingType
import net.evilblock.prisonaio.module.auction.listing.bid.ListingBid
import net.evilblock.prisonaio.module.auction.notification.AHNotification
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.service.Service
import org.bukkit.ChatColor
import java.util.*

object TickListingsService : Service {

    override fun run() {
        for (listing in AuctionHouseHandler.getAllListings()) {
            try {
                if (!listing.isCompleted() && listing.isExpired()) {
                    if (listing.isFeatured()) {
                        if (System.currentTimeMillis() >= listing.getFeatureExpiration()!!) {
                            listing.expireFeature()
                        }
                    }

                    val vendor = UserHandler.getOrLoadAndCacheUser(listing.createdBy)
                    val goodsName = listing.getGoodsName()

                    if (listing.listingType == ListingType.AUCTION) {
                        val latestBid = listing.getLatestBid()
                        if (latestBid == null) {
                            listing.setCompleted()

                            vendor.auctionHouseData.addReturnedListing(listing)
                            vendor.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}Your listing for $goodsName ${ChatColor.GRAY}has expired!"))
                        } else {
                            listing.setPurchased(latestBid.createdBy)
                            listing.getCurrencyType().give(vendor.uuid, latestBid.amount)

                            val buyer = UserHandler.getOrLoadAndCacheUser(latestBid.createdBy)
                            buyer.auctionHouseData.addPurchasedListing(listing)
                            buyer.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}You won the auction for ${ChatColor.RED}${vendor.getUsername()}${ChatColor.GRAY}'s $goodsName ${ChatColor.GRAY}for ${listing.getCurrencyType().format(latestBid.amount)}${ChatColor.GRAY}!"))

                            vendor.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}Your auction listing for $goodsName ${ChatColor.GRAY}was sold to ${ChatColor.RED}${buyer.getUsername()}${ChatColor.GRAY}!"))

                            val returnedBids = hashMapOf<UUID, ListingBid>()
                            for (bid in listing.getBidHistory()) {
                                if (listing.wasPurchased() && listing.getPurchasedBy() == bid.createdBy) {
                                    continue
                                }

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
                                user.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}You were outbid on ${ChatColor.RED}${listing.getCreatorUsername()}${ChatColor.GRAY}'s auction and it has ended! ${ChatColor.GREEN}${ChatColor.BOLD}FUNDS RETURNED"))

                                listing.getCurrencyType().give(uuid, bid.amount)
                            }
                        }
                    } else {
                        listing.setCompleted()
                        vendor.auctionHouseData.addReturnedListing(listing)
                        vendor.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}Your listing for $goodsName ${ChatColor.GRAY}has expired!"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}