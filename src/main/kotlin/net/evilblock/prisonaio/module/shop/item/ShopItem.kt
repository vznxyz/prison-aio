/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.item

import net.evilblock.cubed.util.Duration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class ShopItem(
    /**
     * The actual item representing this shop item.
     */
    var itemStack: ItemStack,
    /**
     * If the item should be given to players.
     */
    var giveItem: Boolean = true
) {

    /**
     * The order of this shop item.
     */
    var order: Int = 0

    /**
     * The item stack amount.
     */
    val amount: Int = 1

    /**
     * The price per unit the shop buys for.
     */
    var buyPricePerUnit: Double = 0.0

    /**
     * The price per unit the shop sells for.
     */
    var sellPricePerUnit: Double = 0.0

    /**
     * The commands that should be executed when a player buys this item.
     */
    var commands: MutableList<String> = arrayListOf() // TODO: turn into val and remove from Shop#init

    var purchaseCooldown: Duration? = null
    var purchaseTimestamps: ConcurrentHashMap<UUID, Long> = ConcurrentHashMap() // TODO: turn into val and remove from Shop#init

    /**
     * If the shop is buying this item.
     */
    fun isBuying(): Boolean {
        return buyPricePerUnit != 0.0
    }

    /**
     * If the shop is selling this item.
     */
    fun isSelling(): Boolean {
        return sellPricePerUnit != 0.0
    }

    fun hasPurchaseCooldown(): Boolean {
        return purchaseCooldown != null
    }

    /**
     * If the given [player] is on purchase cooldown.
     */
    fun isOnPurchaseCooldown(player: Player): Boolean {
        return isOnPurchaseCooldown(player.uniqueId)
    }

    /**
     * If the given [uuid] (Player UUID) is on purchase cooldown.
     */
    fun isOnPurchaseCooldown(uuid: UUID): Boolean {
        if (purchaseCooldown == null) {
            return false
        }
        return purchaseTimestamps.containsKey(uuid) && System.currentTimeMillis() < purchaseTimestamps[uuid]!! + purchaseCooldown!!.get()
    }

    /**
     * Calculates and returns the remaining cooldown for the given [uuid] (Player UUID).
     */
    fun getRemainingPurchaseCooldown(player: Player): Long {
        return getRemainingPurchaseCooldown(player.uniqueId)
    }

    /**
     * Calculates and returns the remaining cooldown for the given [uuid] (Player UUID).
     */
    fun getRemainingPurchaseCooldown(uuid: UUID): Long {
        return (purchaseTimestamps[uuid]!! + purchaseCooldown!!.get()) - System.currentTimeMillis()
    }

    /**
     * Inserts the current epoch timestamp into the [purchaseTimestamps] map for the given [player].
     */
    fun trackPurchaseTimestamp(player: Player) {
        trackPurchaseTimestamp(player.uniqueId)
    }

    /**
     * Inserts the current epoch timestamp into the [purchaseTimestamps] map for the given [uuid] (Player UUID).
     */
    fun trackPurchaseTimestamp(uuid: UUID) {
        purchaseTimestamps[uuid] = System.currentTimeMillis()
    }

}