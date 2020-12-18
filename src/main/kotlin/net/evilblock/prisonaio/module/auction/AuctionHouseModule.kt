/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.auction.command.BrowseCommand
import net.evilblock.prisonaio.module.auction.command.ListingsCommand
import net.evilblock.prisonaio.module.auction.command.WipeCommand
import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.menu.display.BidHistoryDisplay
import org.bukkit.event.Listener

object AuctionHouseModule : PluginModule() {

    override fun getName(): String {
        return "AuctionHouse"
    }

    override fun getConfigFileName(): String {
        return "auction-house"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        AuctionHouseHandler.initialLoad()
    }

    override fun onDisable() {

    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            BrowseCommand::class.java,
            ListingsCommand::class.java,
            WipeCommand::class.java
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Listing::class.java to Listing.ListingParameterType()
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            BidHistoryDisplay
        )
    }

    fun getPlayerMaxConcurrentListings(): Int {
        return config.getInt("settings.player-max-concurrent-listings", 20)
    }

    fun getFeatureListingPrice(): Long {
        return config.getLong("settings.feature-listing-price", 1_000_000L)
    }

}