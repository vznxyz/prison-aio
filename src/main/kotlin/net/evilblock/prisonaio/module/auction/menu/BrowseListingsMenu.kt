/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.listing.ListingType
import net.evilblock.prisonaio.module.auction.listing.bid.ListingBid
import net.evilblock.prisonaio.module.auction.menu.button.CollectionButton
import net.evilblock.prisonaio.module.auction.menu.button.CreateListingButton
import net.evilblock.prisonaio.module.auction.menu.button.MyListingsButton
import net.evilblock.prisonaio.module.auction.menu.button.NotificationsButton
import net.evilblock.prisonaio.module.auction.menu.display.BidHistoryDisplay
import net.evilblock.prisonaio.module.auction.menu.filter.ListingsFilter
import net.evilblock.prisonaio.module.auction.menu.filter.impl.*
import net.evilblock.prisonaio.module.auction.menu.layout.AuctionHouseLayout
import net.evilblock.prisonaio.module.auction.menu.layout.AuctionHouseLayoutMenu
import net.evilblock.prisonaio.module.auction.menu.sort.ListingsSort
import net.evilblock.prisonaio.module.auction.menu.sort.impl.NewestListingsSort
import net.evilblock.prisonaio.module.auction.menu.sort.impl.OldestListingsSort
import net.evilblock.prisonaio.module.auction.menu.sort.impl.PriceHighToLowListingsSort
import net.evilblock.prisonaio.module.auction.menu.sort.impl.PriceLowToHighListingsSort
import net.evilblock.prisonaio.module.auction.notification.AHNotification
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.MainMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList

abstract class BrowseListingsMenu : AuctionHouseLayoutMenu() {

    companion object {
        private val ALL_FILTERS = listOf(ResetFilter, FeaturedListingsFilter, ArmorListingsFilter, EnchantedBooksListingFilter, PickaxesListingsFilter, RobotsListingsFilter, CrateKeysListingsFilter)
        private val ALL_SORTS = listOf(NewestListingsSort, OldestListingsSort, PriceLowToHighListingsSort, PriceHighToLowListingsSort)

        private val SEARCH_REGEX = "[a-zA-Z0-9 ]*".toRegex()
    }

    internal var selectedFilter: ListingsFilter = ResetFilter
    internal var currentFilters: MutableSet<ListingsFilter> = hashSetOf()

    internal var selectedSort: ListingsSort = NewestListingsSort

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getAutoUpdateTicks(): Long {
        return 500L
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val user = UserHandler.getUser(player.uniqueId)

        return (super.getGlobalButtons(player) as MutableMap<Int, Button>).also {
            it[0] = BackButton { MainMenu(user).openMenu(player) }
            it[1] = GlassButton(7)
            it[2] = CreateListingButton()
            it[3] = MyListingsButton()
            it[4] = NotificationsButton(user)
            it[5] = CollectionButton(user)
            it[6] = SearchButton()
            it[7] = FilterButton()
            it[8] = SortButton()
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
            buttons[buttons.size] = ListingButton(listing)
        }

        return buttons
    }

    abstract fun getListings(player: Player): Collection<Listing>

    override fun getMaxItemsPerPage(player: Player): Int {
        return 40
    }

    private inner class ListingButton(private val listing: Listing) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            val info = arrayListOf<String>()

            AuctionHouseLayout.renderListingInformation(player, listing, info)

            if (!listing.isCompleted()) {
                if (info.last() != "") {
                    info.add("")
                }

                when (listing.listingType) {
                    ListingType.AUCTION -> {
                        if (player.uniqueId == listing.createdBy) {
                            info.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to manage listing")
                        } else {
                            info.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to place bid")

                            if (listing.isBINEnabled()) {
                                info.add("${ChatColor.AQUA}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.AQUA}to buy it now")
                            }
                        }
                    }
                    ListingType.PURCHASE -> {
                        if (player.uniqueId == listing.createdBy) {
                            info.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to manage listing")
                        } else {
                            info.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to buy it now")
                        }
                    }
                }

                if (player.hasPermission(Permissions.AUCTION_HOUSE_MOD)) {
                    info.add("${ChatColor.WHITE}${ChatColor.BOLD}MIDDLE-CLICK ${ChatColor.WHITE}to refund listing (mod)")
                }
            }

            return ItemBuilder.copyOf(listing.getGoods()).addToLore(*info.toTypedArray()).build().also {
                val itemMeta = it.itemMeta
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

                it.itemMeta = itemMeta
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (listing.listingType == ListingType.AUCTION && (clickType == ClickType.DROP || clickType == ClickType.CONTROL_DROP)) {
                BidHistoryDisplay.toggleBidHistoryDisplay(player, listing)
                return
            }

            if (!listing.isCompleted()) {
                if (clickType == ClickType.MIDDLE && player.hasPermission(Permissions.AUCTION_HOUSE_MOD)) {
                    ConfirmMenu { confirmed ->
                        if (confirmed) {
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
                        }

                        this@BrowseListingsMenu.openMenu(player)
                    }.openMenu(player)
                    return
                }

                when (listing.listingType) {
                    ListingType.AUCTION -> {
                        if (player.uniqueId == listing.createdBy) {
                            if (clickType.isLeftClick) {
                                ManageListingMenu(this@BrowseListingsMenu, listing).openMenu(player)
                            }
                        } else {
                            if (clickType.isLeftClick) {
                                if (clickType.isShiftClick && listing.isBINEnabled()) {
                                    startPurchaseProcedure(player)
                                } else {
                                    startBidProcedure(player)
                                }
                            }
                        }
                    }
                    ListingType.PURCHASE -> {
                        if (player.uniqueId == listing.createdBy) {
                            if (clickType.isLeftClick) {
                                ManageListingMenu(this@BrowseListingsMenu, listing).openMenu(player)
                            }
                        } else {
                            if (clickType.isLeftClick) {
                                startPurchaseProcedure(player)
                            }
                        }
                    }
                }
            }
        }

        fun startPurchaseProcedure(player: Player) {
            ConfirmMenu("Purchase for ${listing.getCurrencyType().format(listing.getBINPrice())}${ChatColor.RESET}?") { confirmed ->
                if (confirmed) {
                    if (!UserHandler.isUserLoaded(player.uniqueId)) {
                        player.sendMessage("${ChatColor.RED}Your user data hasn't been loaded!")
                        this@BrowseListingsMenu.openMenu(player)
                        return@ConfirmMenu
                    }

                    if (listing.isDeleted()) {
                        player.sendMessage("${ChatColor.RED}That listing has been deleted!")
                        this@BrowseListingsMenu.openMenu(player)
                        return@ConfirmMenu
                    }

                    if (listing.isCompleted()) {
                        player.sendMessage("${ChatColor.RED}That listing has been completed!")
                        this@BrowseListingsMenu.openMenu(player)
                        return@ConfirmMenu
                    }

                    if (CombatTimerHandler.isOnTimer(player)) {
                        player.sendMessage("${ChatColor.RED}You can't purchase items while on combat timer!")
                        this@BrowseListingsMenu.openMenu(player)
                        return@ConfirmMenu
                    }

                    if (!listing.getCurrencyType().has(player.uniqueId, listing.getBINPrice())) {
                        player.sendMessage("${ChatColor.RED}You don't have enough ${listing.getCurrencyType().getName()} to purchase that item!")
                        this@BrowseListingsMenu.openMenu(player)
                        return@ConfirmMenu
                    }

                    listing.getCurrencyType().take(player.uniqueId, listing.getBINPrice())
                    listing.setPurchased(player.uniqueId, true)

                    Tasks.async {
                        val vendor = UserHandler.getOrLoadAndCacheUser(listing.createdBy)
                        listing.getCurrencyType().give(vendor.uuid, listing.getBINPrice())

                        val buyer = UserHandler.getUser(player.uniqueId)
                        buyer.auctionHouseData.addPurchasedListing(listing)

                        val goodsName = listing.getGoodsName()

                        when (listing.listingType) {
                            ListingType.AUCTION -> {
                                buyer.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}You purchased $goodsName ${ChatColor.GRAY}from ${ChatColor.RED}${vendor.getUsername()}${ChatColor.GRAY}'s auction!"))
                                vendor.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}Your auction listing for $goodsName ${ChatColor.GRAY}was purchased by ${ChatColor.RED}${buyer.getUsername()}${ChatColor.GRAY}!"))
                            }
                            ListingType.PURCHASE -> {
                                buyer.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}You purchased $goodsName ${ChatColor.GRAY}from ${ChatColor.RED}${vendor.getUsername()}${ChatColor.GRAY}!"))
                                vendor.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}Your listing for $goodsName ${ChatColor.GRAY}was purchased by ${ChatColor.RED}${buyer.getUsername()}${ChatColor.GRAY}!"))
                            }
                        }
                    }

                    return@ConfirmMenu
                } else {
                    this@BrowseListingsMenu.openMenu(player)
                }
            }.openMenu(player)
        }

        fun startBidProcedure(player: Player) {
            val bidPrompt = buildString {
                val minBid: BigInteger
                val maxBid: BigInteger

                val latestBid = listing.getLatestBid()
                if (latestBid != null) {
                    minBid = latestBid.amount + listing.getBidMinIncrease()
                    maxBid = latestBid.amount + listing.getBidMaxIncrease()
                } else {
                    minBid = listing.getAskingPrice()
                    maxBid = listing.getAskingPrice() + listing.getBidMaxIncrease()
                }

                append("${ChatColor.GREEN}How ${listing.getCurrencyType().getAmountContext()} ${listing.getCurrencyType().getName()} would you like to bid?")
                append("\n")
                append("${ChatColor.GRAY}(Min. ${listing.getCurrencyType().format(minBid)}${ChatColor.GRAY}, Max. ${listing.getCurrencyType().format(maxBid)}${ChatColor.GRAY})")
            }

            NumberPrompt().withText(bidPrompt).acceptInput { amount ->
                val bigAmount = NumberUtils.numberToBigInteger(amount)

                if (!isValidBid(player, listing, bigAmount)) {
                    return@acceptInput
                }

                ConfirmMenu(
                    title = "Place bid for ${listing.getCurrencyType().format(bigAmount)}${ChatColor.RESET}?",
                    extraInfo = TextSplitter.split(text = "Once your bid has been placed, it is locked in, and will not be returned to you unless the auction ends and you are not the winner.")
                ) { confirmed ->
                    if (confirmed) {
                        if (!isValidBid(player, listing, bigAmount)) {
                            return@ConfirmMenu
                        }

                        val previousBid = listing.getLatestBid()
                        val lastBid = listing.getLastBid(player.uniqueId)

                        val lastBidDifference = if (lastBid != null) {
                            bigAmount - lastBid.amount
                        } else {
                            bigAmount
                        }

                        val bid = ListingBid(player.uniqueId, bigAmount)
                        listing.addBid(bid)
                        listing.getCurrencyType().take(player.uniqueId, lastBidDifference)

                        Tasks.async {
                            val goodsName = listing.getGoodsName()

                            val vendor = UserHandler.getOrLoadAndCacheUser(listing.createdBy)
                            vendor.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.RED}${player.name} ${ChatColor.GRAY}placed a ${listing.getCurrencyType().format(bid.amount)} ${ChatColor.GRAY}bid on your listing for $goodsName${ChatColor.GRAY}!"))

                            val bidder = UserHandler.getUser(player.uniqueId)
                            bidder.auctionHouseData.addBidListing(listing)
                            bidder.auctionHouseData.addNotification(AHNotification(message = "${ChatColor.GRAY}You bid ${listing.getCurrencyType().format(bid.amount)} ${ChatColor.GRAY}on ${ChatColor.RED}${vendor.getUsername()}${ChatColor.GRAY}'s listing for $goodsName${ChatColor.GRAY}!"))

                            if (previousBid != null) {
                                UserHandler.getOrLoadAndCacheUser(previousBid.createdBy).auctionHouseData.addNotification(
                                    AHNotification(message = "${ChatColor.GRAY}You've been outbid on ${ChatColor.RED}${listing.getCreatorUsername()}${ChatColor.GRAY}'s auction for $goodsName ${ChatColor.GRAY}by ${ChatColor.AQUA}${player.name}${ChatColor.GRAY}for ${listing.getCurrencyType().format(bid.amount)}${ChatColor.GRAY}!")
                                )
                            }
                        }
                    }

                    this@BrowseListingsMenu.openMenu(player)
                }.openMenu(player)
            }.start(player)
        }

        private fun isValidBid(player: Player, listing: Listing, amount: BigInteger): Boolean {
            val latestBid = listing.getLatestBid()
            if (latestBid == null) {
                if (amount < listing.getAskingPrice()) {
                    player.sendMessage("${ChatColor.RED}The seller is asking for a minimum bid of ${listing.getCurrencyType().format(listing.getAskingPrice())}${ChatColor.RED}!")
                    return false
                }
            } else {
                if (latestBid.createdBy == player.uniqueId) {
                    player.sendMessage("${ChatColor.RED}You can't outbid yourself!")
                    return false
                }

                val minimumBid = latestBid.amount + listing.getBidMinIncrease()
                if (amount < minimumBid) {
                    player.sendMessage("${ChatColor.RED}You must bid at least ${listing.getCurrencyType().format(listing.getBidMinIncrease())} ${ChatColor.RED}to outmatch the last bid!")
                    return false
                }

                val maximumBid = latestBid.amount + listing.getBidMaxIncrease()
                if (amount > maximumBid) {
                    player.sendMessage("${ChatColor.RED}You must bid no more than ${listing.getCurrencyType().format(listing.getBidMaxIncrease())} ${ChatColor.RED}over the last bid!")
                    return false
                }
            }

            val difference: BigInteger

            val lastBid = listing.getLastBid(player.uniqueId)
            if (lastBid != null) {
                difference = amount - lastBid.amount
                if (difference <= BigInteger.ZERO) {
                    player.sendMessage("${ChatColor.RED}Your bid must be more than the last!")
                    return false
                }
            } else {
                difference = amount
            }

            if (!listing.getCurrencyType().has(player.uniqueId, difference)) {
                player.sendMessage("${ChatColor.RED}You don't have enough ${listing.getCurrencyType().getName()} to place that bid!")
                return false
            }

            return true
        }
    }

    private inner class SearchButton : TexturedHeadButton(Constants.IB_ICON_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Search"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")
                it.addAll(TextSplitter.split(text = "Search for listings by their display name, lore, or creator (username or uuid)."))
                it.add("")
                it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to search")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.sendMessage("${ChatColor.RED}The search feature is currently disabled!")
//                EzPrompt.Builder()
//                    .promptText("${ChatColor.GREEN}What would you like to search? ${ChatColor.GRAY}(80 character limit)")
//                    .charLimit(80)
//                    .regex(SEARCH_REGEX)
//                    .acceptInput { input ->
//                        Tasks.async {
//
//                        }
//                    }
//                    .build()
//                    .start(player)
            }
        }
    }

    private inner class FilterButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Filter"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")

                for (filter in ALL_FILTERS) {
                    it.add(buildString {
                        if (filter == selectedFilter) {
                            append(" ${ChatColor.BLUE}${ChatColor.BOLD}» ")
                        } else {
                            append("    ")
                        }

                        if (currentFilters.contains(filter)) {
                            append("${ChatColor.GREEN}${ChatColor.BOLD}")
                        } else {
                            append("${ChatColor.GRAY}")
                        }

                        append(filter.getName())
                    })
                }

                it.add("")
                it.add("${ChatColor.BLUE}${Constants.ARROW_UP} ${ChatColor.YELLOW}${ChatColor.BOLD}LEFT-CLICK")
                it.add("${ChatColor.BLUE}${ChatColor.BOLD}${Constants.DOT_SYMBOL} ${ChatColor.YELLOW}${ChatColor.BOLD}MIDDLE-CLICK ${ChatColor.YELLOW}to toggle")
                it.add("${ChatColor.BLUE}${Constants.ARROW_DOWN} ${ChatColor.YELLOW}${ChatColor.BOLD}RIGHT-CLICK")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.HOPPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                selectedFilter = if (selectedFilter == ALL_FILTERS.first()) {
                    ALL_FILTERS.last()
                } else {
                    ALL_FILTERS[ALL_FILTERS.indexOf(selectedFilter) - 1]
                }
            } else if (clickType.isRightClick) {
                selectedFilter = if (selectedFilter == ALL_FILTERS.last()) {
                    ALL_FILTERS.first()
                } else {
                    ALL_FILTERS[ALL_FILTERS.indexOf(selectedFilter) + 1]
                }
            } else if (clickType == ClickType.MIDDLE || clickType == ClickType.CREATIVE) {
                if (currentFilters.contains(selectedFilter)) {
                    currentFilters.remove(selectedFilter)
                } else {
                    if (selectedFilter is ResetFilter) {
                        currentFilters.clear()
                    } else {
                        currentFilters.add(selectedFilter)
                    }
                }
            }
        }
    }

    private inner class SortButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Sort"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")

                for (sort in ALL_SORTS) {
                    val builder = StringBuilder()

                    if (sort == selectedSort) {
                        builder.append(" ${ChatColor.BLUE}${ChatColor.BOLD}» ${ChatColor.GREEN}${sort.getName()}")
                    } else {
                        builder.append("    ${ChatColor.GRAY}${sort.getName()}")
                    }

                    it.add(builder.toString())
                }

                it.add("")
                it.add("${ChatColor.BLUE}${Constants.ARROW_UP} ${ChatColor.YELLOW}${ChatColor.BOLD}LEFT-CLICK")
                it.add("${ChatColor.BLUE}${Constants.ARROW_DOWN} ${ChatColor.YELLOW}${ChatColor.BOLD}RIGHT-CLICK")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIODE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                selectedSort = if (selectedSort == ALL_SORTS.first()) {
                    ALL_SORTS.last()
                } else {
                    ALL_SORTS[ALL_SORTS.indexOf(selectedSort) - 1]
                }
            } else if (clickType.isRightClick) {
                selectedSort = if (selectedSort == ALL_SORTS.last()) {
                    ALL_SORTS.first()
                } else {
                    ALL_SORTS[ALL_SORTS.indexOf(selectedSort) + 1]
                }
            }
        }
    }

}