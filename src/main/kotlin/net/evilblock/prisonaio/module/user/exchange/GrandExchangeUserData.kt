/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.exchange

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.notification.GrandExchangeUserNotification
import net.evilblock.prisonaio.module.exchange.serializer.ListingsReferenceSerializer
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.ChatColor

class GrandExchangeUserData(@Transient var user: User) {

    companion object {
        private val NOTIFICATION_PREFIX = "${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Grand Exchange${ChatColor.GRAY}] "
    }

    private var notifications: MutableList<GrandExchangeUserNotification> = arrayListOf()

    @JsonAdapter(ListingsReferenceSerializer::class)
    private val returnedListings: MutableList<GrandExchangeListing> = arrayListOf()

    @JsonAdapter(ListingsReferenceSerializer::class)
    private val purchasedListings: MutableList<GrandExchangeListing> = arrayListOf()

    @JsonAdapter(ListingsReferenceSerializer::class)
    internal var bidListings: MutableList<GrandExchangeListing> = arrayListOf()

    fun getNotifications(): List<GrandExchangeUserNotification> {
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

    fun getUnreadNotifications(): List<GrandExchangeUserNotification> {
        return notifications.filter { !it.read }
    }

    fun addNotification(notification: GrandExchangeUserNotification) {
        notifications.add(notification)
        user.requiresSave = true

        if (user.settings.getSettingOption(UserSetting.GRAND_EXCHANGE_NOTIFICATIONS).getValue() as Boolean) {
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

    fun getReturnedListings(): List<GrandExchangeListing> {
        return returnedListings
    }

    fun addReturnedListing(listing: GrandExchangeListing) {
        if (listing.createdBy != user.uuid) {
            throw IllegalStateException("User didn't create listing")
        }

        if (!listing.isCompleted() || listing.wasPurchased()) {
            throw IllegalStateException("Can't return listing that was purchased")
        }

        returnedListings.add(listing)
        user.requiresSave = true
    }

    fun getPurchasedListings(): List<GrandExchangeListing> {
        return purchasedListings
    }

    fun getUnclaimedPurchases(): List<GrandExchangeListing> {
        return purchasedListings.filter { !it.isClaimed() }
    }

    fun getUnclaimedReturns(): List<GrandExchangeListing> {
        return returnedListings.filter { !it.isClaimed() }
    }

    fun getUnclaimedListings(): List<GrandExchangeListing> {
        return arrayListOf<GrandExchangeListing>().also {
            it.addAll(getUnclaimedPurchases())
            it.addAll(getUnclaimedReturns())
        }
    }

    fun addPurchasedListing(listing: GrandExchangeListing) {
        if (!listing.isCompleted() || !listing.wasPurchased() || listing.getPurchasedBy() != user.uuid) {
            throw IllegalStateException("User didn't purchase listing")
        }

        purchasedListings.add(listing)
        user.requiresSave = true
    }

    fun getBidListings(): List<GrandExchangeListing> {
        return bidListings
    }

    fun addBidListing(listing: GrandExchangeListing) {
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