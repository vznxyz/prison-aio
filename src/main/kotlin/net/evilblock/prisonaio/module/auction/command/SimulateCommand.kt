/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.Duration
import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.listing.ListingType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigInteger
import java.util.concurrent.ThreadLocalRandom

object SimulateCommand {

    @Command(names = ["ah simulate"], permission = "op", async = true)
    @JvmStatic
    fun execute(player: Player, @Param(name = "amount") amount: Int) {
        ConfirmMenu { confirmed ->
            if (confirmed) {
                if (amount > 0) {
                    for (i in 0 until amount) {
                        val type = if (ThreadLocalRandom.current().nextBoolean()) {
                            ListingType.PURCHASE
                        } else {
                            ListingType.AUCTION
                        }

                        val listing = Listing(listingType = type, createdBy = player.uniqueId, duration = Duration(90000L), goods = ItemStack(Material.STONE))

                        listing.updateAskingPrice(BigInteger("25000"))
                        listing.updateBINPrice(BigInteger("1000000"))

                        if (ThreadLocalRandom.current().nextBoolean()) {
                            listing.updateBINEnabled(true)
                        }

                        if (listing.listingType == ListingType.AUCTION) {
                            listing.updateBidMinIncrease(BigInteger("5000"))
                            listing.updateBidMaxIncrease(BigInteger("100000"))
                        }

                        AuctionHouseHandler.trackListing(listing)
                    }
                }

                player.sendMessage("done")
            }
        }.openMenu(player)
    }

}