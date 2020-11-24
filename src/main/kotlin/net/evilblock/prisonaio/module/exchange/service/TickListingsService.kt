/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.service

import net.evilblock.prisonaio.module.exchange.GrandExchangeHandler
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListingType
import net.evilblock.prisonaio.module.exchange.listing.bid.GrandExchangeListingBid
import net.evilblock.prisonaio.module.exchange.notification.GrandExchangeUserNotification
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.service.Service
import org.bukkit.ChatColor
import java.util.*

object TickListingsService : Service {

    override fun run() {
        for (listing in GrandExchangeHandler.getAllListings()) {
            try {
                if (!listing.isCompleted() && listing.isExpired()) {
                    if (listing.isFeatured()) {
                        if (System.currentTimeMillis() >= listing.getFeatureExpiration()!!) {
                            listing.expireFeature()
                        }
                    }

                    val vendor = UserHandler.getOrLoadAndCacheUser(listing.createdBy)
                    val goodsName = listing.getGoodsName()

                    if (listing.listingType == GrandExchangeListingType.AUCTION) {
                        val latestBid = listing.getLatestBid()
                        if (latestBid == null) {
                            listing.setCompleted()

                            vendor.grandExchangeData.addReturnedListing(listing)
                            vendor.grandExchangeData.addNotification(GrandExchangeUserNotification(message = "${ChatColor.GRAY}Your listing for $goodsName ${ChatColor.GRAY}has expired!"))
                        } else {
                            listing.setPurchased(latestBid.createdBy)
                            listing.getCurrencyType().give(vendor.uuid, latestBid.amount)

                            val buyer = UserHandler.getOrLoadAndCacheUser(latestBid.createdBy)
                            buyer.grandExchangeData.addPurchasedListing(listing)
                            buyer.grandExchangeData.addNotification(GrandExchangeUserNotification(message = "${ChatColor.GRAY}You won the auction for ${ChatColor.RED}${vendor.getUsername()}${ChatColor.GRAY}'s $goodsName ${ChatColor.GRAY}for ${listing.getCurrencyType().format(latestBid.amount)}${ChatColor.GRAY}!"))

                            vendor.grandExchangeData.addNotification(GrandExchangeUserNotification(message = "${ChatColor.GRAY}Your auction listing for $goodsName ${ChatColor.GRAY}was sold to ${ChatColor.RED}${buyer.getUsername()}${ChatColor.GRAY}!"))

                            val returnedBids = hashMapOf<UUID, GrandExchangeListingBid>()
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
                                user.grandExchangeData.addNotification(GrandExchangeUserNotification(message = "${ChatColor.GRAY}You were outbid on ${ChatColor.RED}${listing.getCreatorUsername()}${ChatColor.GRAY}'s auction and it has ended! ${ChatColor.GREEN}${ChatColor.BOLD}FUNDS RETURNED"))

                                listing.getCurrencyType().give(uuid, bid.amount)
                            }
                        }
                    } else {
                        listing.setCompleted()
                        vendor.grandExchangeData.addReturnedListing(listing)
                        vendor.grandExchangeData.addNotification(GrandExchangeUserNotification(message = "${ChatColor.GRAY}Your listing for $goodsName ${ChatColor.GRAY}has expired!"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}