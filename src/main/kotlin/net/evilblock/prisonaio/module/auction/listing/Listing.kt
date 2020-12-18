/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.listing

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.auction.listing.bid.ListingBid
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigInteger
import java.util.*

class Listing(
    val listingType: ListingType,
    private val goods: ItemStack,
    val duration: Duration,
    private var currencyType: Currency.Type = Currency.Type.MONEY,
    val createdBy: UUID
) {

    val id: UUID = UUID.randomUUID()
    val createdAt: Long = System.currentTimeMillis()

    private var binEnabled: Boolean = false
    private var binPrice: BigInteger = BigInteger.ZERO

    private var bidHistory: LinkedList<ListingBid> = LinkedList()
    private var bidAskingPrice: BigInteger = BigInteger.ZERO
    private var bidIncreaseMin: BigInteger = BigInteger.ZERO
    private var bidIncreaseMax: BigInteger = BigInteger.ZERO

    private var completed: Boolean = false

    private var purchased: Boolean = false
    private var purchasedBIN: Boolean = false
    private var purchasedAt: Long? = null
    private var purchasedBy: UUID? = null

    private var claimed: Boolean = false

    private var deleted: Boolean = false
    private var deletedAt: Long? = null
    private var deletedBy: UUID? = null
    private var deletedReason: String? = null

    private var featured: Boolean = false
    private var featuredAt: Long? = null
    private var featureExpiration: Long? = null

    @Transient
    var requiresSave: Boolean = false

    fun getGoods(): ItemStack {
        return goods.clone()
    }

    fun getGoodsName(): String {
        val goods = getGoods()
        val goodsName = ItemUtils.getChatName(goods).replace(ChatColor.MAGIC.toString(), "")

        val goodsAmount = if (goods.amount > 1) {
            "${ChatColor.getLastColors(goodsName)}${goods.amount}x "
        } else {
            ""
        }

        return goodsAmount + goodsName
    }

    fun getCreatorUsername(): String {
        return Cubed.instance.uuidCache.name(createdBy)
    }

    fun isCompleted(): Boolean {
        return completed
    }

    fun setCompleted() {
        completed = true
        requiresSave = true
    }

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= createdAt + duration.get()
    }

    fun wasPurchased(): Boolean {
        return purchased
    }

    fun wasPurchasedByBIN(): Boolean {
        return purchasedBIN
    }

    internal fun setPurchased(buyer: UUID, bin: Boolean = false) {
        completed = true
        purchased = true
        purchasedBIN = bin
        purchasedAt = System.currentTimeMillis()
        purchasedBy = buyer
        requiresSave = true
    }

    fun getPurchasedAt(): Long? {
        return purchasedAt
    }

    fun getPurchasedBy(): UUID? {
        return purchasedBy
    }

    fun getPurchasedByUsername(): String {
        return Cubed.instance.uuidCache.name(purchasedBy!!)
    }

    fun isClaimed(): Boolean {
        return claimed
    }

    fun setClaimed() {
        claimed = true
    }

    fun isDeleted(): Boolean {
        return deleted
    }

    internal fun setDeleted(playerUuid: UUID) {
        completed = true
        deleted = true
        deletedAt = System.currentTimeMillis()
        deletedBy = playerUuid
        requiresSave = true
    }

    fun getDeletedAt(): Long? {
        return deletedAt
    }

    fun getDeletedBy(): UUID? {
        return deletedBy
    }

    fun getDeletedByUsername(): String {
        return Cubed.instance.uuidCache.name(deletedBy!!)
    }

    fun getDeletedReason(): String? {
        return deletedReason
    }

    fun isFeatured(): Boolean {
        return featured
    }

    fun setFeatured() {
        featured = true
        featuredAt = System.currentTimeMillis()
        featureExpiration = System.currentTimeMillis() + 50_000L
    }

    fun expireFeature() {
        featured = false
        featuredAt = null
        featureExpiration = null
    }

    fun getFeatureExpiration(): Long? {
        return featureExpiration
    }

    fun isBINOnly(): Boolean {
        return listingType == ListingType.PURCHASE
    }

    fun isAuction(): Boolean {
        return listingType == ListingType.AUCTION
    }

    fun getCurrencyType(): Currency {
        return currencyType
    }

    fun updateCurrencyType(currency: Currency.Type) {
        currencyType = currency
        requiresSave = true
    }

    fun isBINEnabled(): Boolean {
        return binEnabled
    }

    fun updateBINEnabled(enabled: Boolean) {
        binEnabled = enabled
        requiresSave = true
    }

    fun getBINPrice(): BigInteger {
        return binPrice
    }

    fun updateBINPrice(amount: BigInteger) {
        binPrice = amount
        requiresSave = true
    }

    fun getAskingPrice(): BigInteger {
        return bidAskingPrice
    }

    fun updateAskingPrice(amount: BigInteger) {
        bidAskingPrice = amount
        requiresSave = true
    }

    fun getBidMinIncrease(): BigInteger {
        return bidIncreaseMin
    }

    fun updateBidMinIncrease(amount: BigInteger) {
        bidIncreaseMin = amount
        requiresSave = true
    }

    fun getBidMaxIncrease(): BigInteger {
        return bidIncreaseMax
    }

    fun updateBidMaxIncrease(amount: BigInteger) {
        bidIncreaseMax = amount
        requiresSave = true
    }

    fun getBidHistory(): List<ListingBid> {
        return bidHistory
    }

    fun addBid(bid: ListingBid) {
        bidHistory.add(bid)
        requiresSave = true
    }

    fun getLatestBid(): ListingBid? {
        return bidHistory.maxBy { it.createdAt }
    }

    fun getLastBid(playerUuid: UUID): ListingBid? {
        return bidHistory.filter { it.createdBy == playerUuid }.maxBy { it.createdAt }
    }

    class ListingParameterType : ParameterType<Listing> {
        override fun transform(sender: CommandSender, source: String): Listing? {
            return try {
                AuctionHouseHandler.getListingById(UUID.fromString(source))
            } catch (e: Exception) {
                null
            }
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            return emptyList()
        }
    }

}