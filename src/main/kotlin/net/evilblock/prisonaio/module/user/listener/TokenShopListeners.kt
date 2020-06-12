package net.evilblock.prisonaio.module.user.listener

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.mechanic.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

object TokenShopListeners : Listener {

    @JvmStatic
    private val TOKEN_SHOP_TAG = "${ChatColor.GRAY}[${ChatColor.GOLD}${ChatColor.BOLD}TokenShop${ChatColor.GRAY}]"

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.clickedBlock.type == Material.SIGN || event.clickedBlock.type == Material.SIGN_POST || event.clickedBlock.type == Material.WALL_SIGN) {
                val sign = event.clickedBlock.state as Sign
                if (sign.lines[0] == TOKEN_SHOP_TAG) {
                    try {
                        val owner = Cubed.instance.uuidCache.uuid(sign.lines[1])
                        if (owner != null) {
                            if (event.player.uniqueId == owner) {
                                event.player.sendMessage("${ChatColor.RED}You can't buy or sell to your own TokenShop!")
                                event.isCancelled = true
                                return
                            }

                            if (sign.lines[2].startsWith("-")) {
                                event.isCancelled = true
                                return
                            }

                            val quantity = NumberUtils.parseInput(sign.lines[2])
                            assert(quantity.toInt() > 0) { "Quantity must be more than 0." }

                            val priceLineSplit = ChatColor.stripColor(sign.lines[3]).split(" ")
                            if (priceLineSplit[1].startsWith("-")) {
                                event.isCancelled = true
                                return
                            }

                            val price = NumberUtils.parseInput(priceLineSplit[1])
                            assert(price.toInt() > 0) { "Price must be more than 0." }

                            Tasks.async {
                                val owningUser = if (UserHandler.isUserLoaded(owner)) {
                                    UserHandler.getUser(owner)
                                } else {
                                    UserHandler.fetchUser(owner)
                                }

                                val buying = when {
                                    priceLineSplit[0].equals("b", ignoreCase = true) -> {
                                        true
                                    }
                                    priceLineSplit[0].equals("s", ignoreCase = true) -> {
                                        false
                                    }
                                    else -> {
                                        throw IllegalStateException("Couldn't determine if buying or selling")
                                    }
                                }

                                if (buying) {
                                    val buyingPlayer = event.player

                                    if (!owningUser.hasTokensBalance(quantity.toLong())) {
                                        buyingPlayer.sendMessage("${ChatColor.RED}${owningUser.getUsername()} doesn't have enough tokens to sell you.")
                                        return@async
                                    }

                                    val playerBalance = VaultHook.useEconomyAndReturn { it.getBalance(event.player) }
                                    if (playerBalance < price.toLong()) {
                                        buyingPlayer.sendMessage("${ChatColor.RED}You don't have enough money to buy tokens from that TokenShop.")
                                        return@async
                                    }

                                    VaultHook.useEconomy { economy ->
                                        economy.withdrawPlayer(buyingPlayer, price.toDouble())
                                        economy.depositPlayer(Bukkit.getOfflinePlayer(owner), price.toDouble())
                                    }

                                    val buyingUser = UserHandler.getUser(buyingPlayer.uniqueId)
                                    buyingUser.addTokensBalance(quantity.toLong())
                                    UserHandler.saveUser(buyingUser)

                                    owningUser.subtractTokensBalance(quantity.toLong())
                                    UserHandler.saveUser(owningUser)

                                    buyingPlayer.sendMessage("$TOKEN_SHOP_TAG You bought ${Formats.formatTokens(quantity.toLong())} ${ChatColor.GRAY}for ${Formats.formatMoney(price.toDouble())}${ChatColor.GRAY}!")
                                    owningUser.getPlayer()?.sendMessage("$TOKEN_SHOP_TAG You sold ${Formats.formatTokens(quantity.toLong())} ${ChatColor.GRAY}for ${Formats.formatMoney(price.toDouble())}${ChatColor.GRAY}!")
                                } else {
                                    val sellingPlayer = event.player

                                    val ownerBalance = VaultHook.useEconomyAndReturn { it.getBalance(Bukkit.getOfflinePlayer(owner)) }
                                    if (ownerBalance < price.toLong()) {
                                        sellingPlayer.sendMessage("${ChatColor.RED}${owningUser.getUsername()} doesn't have enough money to buy your tokens.")
                                        return@async
                                    }

                                    val sellingUser = UserHandler.getUser(sellingPlayer.uniqueId)
                                    if (!sellingUser.hasTokensBalance(quantity.toLong())) {
                                        sellingPlayer.sendMessage("${ChatColor.RED}You don't have enough tokens to sell to that TokenShop.")
                                        return@async
                                    }

                                    VaultHook.useEconomy { economy ->
                                        economy.depositPlayer(sellingPlayer, price.toDouble())
                                        economy.withdrawPlayer(Bukkit.getOfflinePlayer(owner), price.toDouble())
                                    }

                                    sellingUser.subtractTokensBalance(quantity.toLong())
                                    UserHandler.saveUser(sellingUser)

                                    owningUser.addTokensBalance(quantity.toLong())
                                    UserHandler.saveUser(owningUser)

                                    sellingPlayer.sendMessage("$TOKEN_SHOP_TAG You sold ${Formats.formatTokens(quantity.toLong())} ${ChatColor.GRAY}for ${Formats.formatMoney(price.toDouble())}${ChatColor.GRAY}!")
                                    owningUser.getPlayer()?.sendMessage("$TOKEN_SHOP_TAG You bought ${Formats.formatTokens(quantity.toLong())} ${ChatColor.GRAY}for ${Formats.formatMoney(price.toDouble())}${ChatColor.GRAY}!")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        event.player.sendMessage("${ChatColor.RED}Failed to exchange with the TokenShop.")

                        if (event.player.isOp) {
                            event.player.sendMessage("${ChatColor.RED}[OP] Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSignChangeEvent(event: SignChangeEvent) {
        if (event.getLine(0).equals("[tokenshop]", ignoreCase = true)) {
            try {
                if (event.lines[1].startsWith("-")) {
                    event.isCancelled = true
                    event.player.sendMessage("${ChatColor.RED}Quantity cannot be negative.")
                    return
                }

                val quantity = NumberUtils.parseInput(event.lines[1])
                if (quantity.toInt() < 1) {
                    event.isCancelled = true
                    event.player.sendMessage("${ChatColor.RED}Quantity must be at least 1.")
                    return
                }

                val priceLineSplit = event.lines[2].split(" ")
                if (priceLineSplit[1].startsWith("-")) {
                    throw IllegalStateException("Price cannot be negative.")
                }

                val price = NumberUtils.parseInput(priceLineSplit[1])
                if (price.toInt() < 1) {
                    event.player.sendMessage("${ChatColor.RED}Price must be at least 1.")
                    event.isCancelled = true
                    return
                }

                val buying = when {
                    priceLineSplit[0].equals("b", ignoreCase = true) -> {
                        true
                    }
                    priceLineSplit[0].equals("s", ignoreCase = true) -> {
                        false
                    }
                    else -> {
                        throw IllegalStateException("Couldn't determine if buying or selling")
                    }
                }

                val formattedPrice = when (price) {
                    is Int -> {
                        NumberUtils.format(price.toDouble())
                    }
                    is Double -> {
                        NumberUtils.format(price)
                    }
                    else -> {
                        NumberUtils.format(price as Long)
                    }
                }

                val buyOrSell = if (buying) {
                    "${ChatColor.RED}${ChatColor.BOLD}B"
                } else {
                    "${ChatColor.GREEN}${ChatColor.BOLD}S"
                }

                event.setLine(0, TOKEN_SHOP_TAG)
                event.setLine(1, event.player.name)
                event.setLine(2, NumberUtils.format(quantity.toLong()))
                event.setLine(3, "$buyOrSell ${ChatColor.BLACK}$formattedPrice")
            } catch (e: Exception) {
                event.player.sendMessage("${ChatColor.RED}Failed to create a TokenShop because the format was incorrect. The format is as follows:")
                event.player.sendMessage("${ChatColor.GRAY}[TokenShop]")
                event.player.sendMessage("${ChatColor.GRAY}{quantity}")
                event.player.sendMessage("${ChatColor.GRAY}B/S {price}")
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.block.state is Sign) {
            val sign = event.block.state as Sign

            if (sign.getLine(0) != TOKEN_SHOP_TAG || sign.lines.size != 4) {
                return
            }

            if (RegionBypass.hasBypass(event.player)) {
                return
            }

            if (sign.getLine(1) != event.player.name) {
                event.isCancelled = true
                event.player.sendMessage("${ChatColor.RED}You can't destroy that TokenShop as it doesn't belong to you!")
            }
        }
    }

}