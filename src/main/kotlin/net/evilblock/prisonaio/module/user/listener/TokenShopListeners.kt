/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.listener

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.Cooldown
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.math.BigDecimal
import java.util.*

object TokenShopListeners : Listener {

    private val useCooldown = Cooldown<UUID>(500L)

    @JvmStatic
    private val TOKEN_SHOP_TAG = "${ChatColor.GRAY}[${ChatColor.GOLD}${ChatColor.BOLD}TokenShop${ChatColor.GRAY}]"

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.clickedBlock.type == Material.SIGN || event.clickedBlock.type == Material.SIGN_POST || event.clickedBlock.type == Material.WALL_SIGN) {
                val sign = event.clickedBlock.state as Sign
                if (sign.lines[0] == TOKEN_SHOP_TAG) {
                    event.isCancelled = true

                    val player = event.player

                    if (useCooldown.isOnCooldown(player.uniqueId)) {
                        return
                    }

                    useCooldown.putOnCooldown(player.uniqueId)

                    try {
                        handleTransaction(player, sign)
                    } catch (e: Exception) {
                        event.player.sendMessage("${ChatColor.RED}Failed to transact with that TokenShop!")

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
                event.player.sendMessage("${ChatColor.RED}You can't destroy that TokenShop because it doesn't belong to you!")
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        useCooldown.removeCooldown(event.player.uniqueId)
    }

    private fun handleTransaction(player: Player, sign: Sign) {
        Tasks.async {
            val owner = Cubed.instance.uuidCache.uuid(sign.lines[1]) ?: throw IllegalStateException("Couldn't determine owner of sign")

            if (player.uniqueId == owner) {
                player.sendMessage("${ChatColor.RED}You can't buy or sell to your own TokenShop!")
                return@async
            }

            if (sign.lines[2].startsWith("-")) {
                return@async
            }

            val quantity = NumberUtils.parseInput(sign.lines[2])
            assert(quantity.toInt() > 0) { "Quantity must be more than 0." }

            val priceLineSplit = ChatColor.stripColor(sign.lines[3]).split(" ")
            if (priceLineSplit[1].startsWith("-")) {
                return@async
            }

            val price = NumberUtils.parseInput(priceLineSplit[1])
            assert(price.toInt() > 0) { "Price must be more than 0." }

            val bigPrice = BigDecimal(price.toString())

            val owningUser = UserHandler.getOrLoadAndCacheUser(owner)
            val interactingUser = UserHandler.getOrLoadAndCacheUser(player.uniqueId, lookup = false, throws = true)

            Tasks.sync {
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
                    if (!owningUser.hasTokenBalance(quantity.toLong())) {
                        player.sendMessage("${ChatColor.RED}${owningUser.getUsername()} doesn't have enough tokens to sell you.")
                        return@sync
                    }

                    if (!interactingUser.hasMoneyBalance(bigPrice)) {
                        player.sendMessage("${ChatColor.RED}You don't have enough money to buy tokens from that TokenShop.")
                        return@sync
                    }

                    owningUser.addMoneyBalance(bigPrice)
                    owningUser.subtractTokensBalance(quantity.toLong())
                    owningUser.requiresSave()

                    interactingUser.subtractMoneyBalance(bigPrice)
                    interactingUser.addTokensBalance(quantity.toLong())
                    interactingUser.requiresSave()

                    player.sendMessage("$TOKEN_SHOP_TAG You bought ${Formats.formatTokens(quantity.toLong())} ${ChatColor.GRAY}from ${Formats.formatPlayer(owningUser.getPlayer()!!)} ${ChatColor.GRAY}for ${Formats.formatMoney(bigPrice)}${ChatColor.GRAY}!")
                    owningUser.getPlayer()?.sendMessage("$TOKEN_SHOP_TAG You sold ${Formats.formatTokens(quantity.toLong())} ${ChatColor.GRAY}to ${Formats.formatPlayer(player)} ${ChatColor.GRAY}for ${Formats.formatMoney(bigPrice)}${ChatColor.GRAY}!")
                } else {
                    if (!owningUser.hasMoneyBalance(bigPrice)) {
                        player.sendMessage("${ChatColor.RED}${owningUser.getUsername()} doesn't have enough money to buy your tokens.")
                        return@sync
                    }

                    if (!interactingUser.hasTokenBalance(quantity.toLong())) {
                        player.sendMessage("${ChatColor.RED}You don't have enough tokens to sell to that token shop.")
                        return@sync
                    }

                    interactingUser.addMoneyBalance(bigPrice)
                    interactingUser.subtractTokensBalance(quantity.toLong())
                    interactingUser.requiresSave()

                    owningUser.subtractMoneyBalance(bigPrice)
                    owningUser.addTokensBalance(quantity.toLong())
                    owningUser.requiresSave()

                    player.sendMessage("$TOKEN_SHOP_TAG You sold ${Formats.formatTokens(quantity.toLong())} ${ChatColor.GRAY}to ${Formats.formatPlayer(owningUser.getPlayer()!!)} ${ChatColor.GRAY}for ${Formats.formatMoney(bigPrice)}${ChatColor.GRAY}!")
                    owningUser.getPlayer()?.sendMessage("$TOKEN_SHOP_TAG You bought ${Formats.formatTokens(quantity.toLong())} ${ChatColor.GRAY}to ${Formats.formatPlayer(player)} ${ChatColor.GRAY}for ${Formats.formatMoney(bigPrice)}${ChatColor.GRAY}!")
                }
            }
        }
    }

}