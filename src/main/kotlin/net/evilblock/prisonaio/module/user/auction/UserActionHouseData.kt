/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.auction

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.notification.AHNotification
import net.evilblock.prisonaio.module.auction.serializer.ListingsReferenceSerializer
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.ChatColor

class UserActionHouseData(@Transient var user: User) {

    companion object {
        private val NOTIFICATION_PREFIX = "${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Auction House${ChatColor.GRAY}] "
    }

    private var notifications: MutableList<AHNotification> = arrayListOf()

    @JsonAdapter(ListingsReferenceSerializer::class)
    private val returnedListings: MutableList<Listing> = arrayListOf()

    @JsonAdapter(ListingsReferenceSerializer::class)
    private val purchasedListings: MutableList<Listing> = arrayListOf()

    @JsonAdapter(ListingsReferenceSerializer::class)
    internal var bidListings: MutableList<Listing> = arrayListOf()

    fun getNotifications(): List<AHNotification> {
        return notifications
    }

    fun readNotifications() {
        var save = false

        val notifications = notifications.sortedBy { it.createdAt }.reversed()
        if (notifications.isNotEmpty()) {
            val onScreen = if (notifications.size > 20) {
                notifications.dropLast(notifications.size - 20)
            } else {
                notifications
            }

            if (onScreen.isNotEmpty()) {
                for (notification in onScreen) {
                    notification.read = true
                }

                save = true
            }
        }

        if (save) {
            user.requiresSave = true
        }
    }

    fun getUnreadNotifications(): List<AHNotification> {
        return notifications.filter { !it.read }
    }

    fun addNotification(notification: AHNotification) {
        notifications.add(notification)
        user.requiresSave = true

        if (user.settings.getSettingOption(UserSetting.AH_NOTIFICATIONS).getValue() as Boolean) {
            user.getPlayer()?.sendMessage("${NOTIFICATION_PREFIX}${notification.message}")
        }
    }

    fun expireNotifications() {
        var save = false

        val iterator = notifications.iterator()
        while (iterator.hasNext()) {
            val notification = iterator.next()
            if (notification.needsDeletion()) {
                iterator.remove()
                save = true
            }
        }

        if (save) {
            user.requiresSave = true
        }
    }

    fun getReturnedListings(): List<Listing> {
        return returnedListings
    }

    fun addReturnedListing(listing: Listing) {
        if (listing.createdBy != user.uuid) {
            throw IllegalStateException("User didn't create listing")
        }

        if (!listing.isCompleted() || listing.wasPurchased()) {
            throw IllegalStateException("Can't return listing that was purchased")
        }

        returnedListings.add(listing)
        user.requiresSave = true
    }

    fun getPurchasedListings(): List<Listing> {
        return purchasedListings
    }

    fun getUnclaimedPurchases(): List<Listing> {
        return purchasedListings.filter { !it.isClaimed() }
    }

    fun getUnclaimedReturns(): List<Listing> {
        return returnedListings.filter { !it.isClaimed() }
    }

    fun getUnclaimedListings(): List<Listing> {
        return arrayListOf<Listing>().also {
            it.addAll(getUnclaimedPurchases())
            it.addAll(getUnclaimedReturns())
        }
    }

    fun addPurchasedListing(listing: Listing) {
        if (!listing.isCompleted() || !listing.wasPurchased() || listing.getPurchasedBy() != user.uuid) {
            throw IllegalStateException("User didn't purchase listing")
        }

        purchasedListings.add(listing)
        user.requiresSave = true
    }

    fun getBidListings(): List<Listing> {
        return bidListings
    }

    fun addBidListing(listing: Listing) {
        bidListings.add(listing)
        user.requiresSave = true
    }

    fun cleanupBidListings() {
        val toRemove = bidListings.filter { it.isCompleted() }
        if (toRemove.isNotEmpty()) {
            for (listing in toRemove) {
                bidListings.remove(listing)
            }

            user.requiresSave = true
        }
    }

}