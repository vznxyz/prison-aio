/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.button

import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.exchange.GrandExchangeHandler
import net.evilblock.prisonaio.module.exchange.GrandExchangeModule
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListingType
import net.evilblock.prisonaio.module.exchange.menu.UserListingsMenu
import net.evilblock.prisonaio.module.exchange.menu.creation.SelectDurationMenu
import net.evilblock.prisonaio.module.exchange.menu.creation.SelectListingTypeMenu
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.util.economy.menu.SelectCurrencyMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class CreateListingButton : AddButton() {

    override fun getName(player: Player): String {
        return "${ChatColor.GREEN}${ChatColor.BOLD}Create Listing"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also {
            it.add("")
            it.addAll(TextSplitter.split(text = "You can create a new listing by completing the procedure. You will be charged a small fee based on the duration you select."))
            it.add("")
            it.addAll(TextSplitter.split(text = "Once your listing has been created, your item is locked in, and you will not be able to retrieve it unless you pay a deletion fee of ${Formats.formatTokens(GrandExchangeHandler.DELETION_FEE)}${ChatColor.GRAY}."))
            it.add("")
            it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new listing")
        }
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            val uncompletedListings = GrandExchangeHandler.getPlayerListings(player.uniqueId).filter { !it.isCompleted() }.size
            if (uncompletedListings >= GrandExchangeModule.getPlayerMaxConcurrentListings()) {
                player.sendMessage("${ChatColor.RED}You've reached the maximum amount of listings! (${GrandExchangeModule.getPlayerMaxConcurrentListings()} listings)")
                return
            }

            SelectItemStackMenu("What item are you listing?") { selectedItemStack ->
                if (selectedItemStack.type == Material.AIR) {
                    return@SelectItemStackMenu
                }

                SelectListingTypeMenu { selectedListingType ->
                    SelectDurationMenu { selectedDuration ->
                        val user = UserHandler.getUser(player.uniqueId)

                        SelectCurrencyMenu("What currency do you prefer?") { selectedCurrency ->
                            val listing = GrandExchangeListing(selectedListingType, selectedItemStack, selectedDuration.second, selectedCurrency, player.uniqueId)

                            startSetupProcedure(player, listing) {
                                if (!user.hasTokenBalance(selectedDuration.first)) {
                                    player.sendMessage("${ChatColor.RED}You don't have enough tokens for that duration's fee!")
                                    return@startSetupProcedure
                                }

                                if (player.inventory.removeItem(selectedItemStack).isNotEmpty()) {
                                    player.sendMessage("${ChatColor.RED}That's weird... We couldn't remove the item in your inventory!")
                                    return@startSetupProcedure
                                }

                                GrandExchangeHandler.trackListing(listing)

                                Tasks.async {
                                    GrandExchangeHandler.saveListing(listing)
                                }

                                user.subtractTokensBalance(selectedDuration.first)
                                player.updateInventory()

                                if (listing.listingType == GrandExchangeListingType.AUCTION) {
                                    player.sendMessage("${ChatColor.GREEN}Your auction has been listed in The Grand Exchange!")
                                } else {
                                    player.sendMessage("${ChatColor.GREEN}Your item has been listed in The Grand Exchange!")
                                }

                                UserListingsMenu(user).openMenu(player)
                            }
                        }.openMenu(player)
                    }.openMenu(player)
                }.openMenu(player)
            }.openMenu(player)
        }
    }

    private fun startSetupProcedure(player: Player, listing: GrandExchangeListing, finish: () -> Unit) {
        if (listing.listingType == GrandExchangeListingType.PURCHASE) {
            NumberPrompt()
                .withText("${ChatColor.GREEN}How ${listing.getCurrencyType().getAmountContext()} ${listing.getCurrencyType().getName()} do you want the item to cost?")
                .acceptInput { binPriceInput ->
                    val price = NumberUtils.numberToBigInteger(binPriceInput)
                    listing.updateBINEnabled(true)
                    listing.updateBINPrice(price)

                    finish.invoke()
                }.start(player)
        } else {
            NumberPrompt()
                .withText("${ChatColor.GREEN}What's the asking price (in ${listing.getCurrencyType().getName()}) for your item?")
                .acceptInput { askingPrice ->
                    listing.updateAskingPrice(NumberUtils.numberToBigInteger(askingPrice))

                    Tasks.delayed(1L) {
                        NumberPrompt()
                            .withText("${ChatColor.GREEN}What's the minimum bid increase (in ${listing.getCurrencyType().getName()}) for your auction?")
                            .acceptInput { minIncrease ->
                                listing.updateBidMinIncrease(NumberUtils.numberToBigInteger(minIncrease))

                                Tasks.delayed(1L) {
                                    NumberPrompt()
                                        .withText("${ChatColor.GREEN}What's the maximum bid increase (in ${listing.getCurrencyType().getName()}) for your auction?")
                                        .acceptInput { maxIncrease ->
                                            listing.updateBidMaxIncrease(NumberUtils.numberToBigInteger(maxIncrease))

                                            ConfirmMenu(
                                                title = "Would you like to set a BIN?",
                                                extraInfo = TextSplitter.split(text = "Set a BIN price if you'd like to also allow players to instantly purchase your item. The BIN price is usually set a bit higher than what you want to auction off your item for."),
                                                confirm = false
                                            ) { confirmed ->
                                                if (confirmed) {
                                                    NumberPrompt()
                                                        .withText("${ChatColor.GREEN}How ${listing.getCurrencyType().getAmountContext()} ${listing.getCurrencyType().getName()} would you like to set the BIN price to?")
                                                        .acceptInput { binPrice ->
                                                            listing.updateBINEnabled(true)
                                                            listing.updateBINPrice(NumberUtils.numberToBigInteger(binPrice))

                                                            finish.invoke()
                                                        }.start(player)
                                                } else {
                                                    finish.invoke()
                                                }
                                            }.openMenu(player)
                                        }.start(player)
                                }
                        }.start(player)
                    }
                }.start(player)
        }
    }

}