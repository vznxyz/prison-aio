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
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
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
        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.clickedBlock.type == Material.SIGN || event.clickedBlock.type == Material.SIGN_POST || event.clickedBlock.type == Material.WALL_SIGN) {
                val sign = event.clickedBlock.state as Sign
                if (sign.lines[0] == TOKEN_SHOP_TAG) {
                    if (useCooldown.isOnCooldown(event.player.uniqueId)) {
                        return
                    }

                    useCooldown.putOnCooldown(event.player.uniqueId)

                    val tokenShop: TokenShop
                    try {
                        tokenShop = readTokenShop(sign) ?: return
                    } catch (e: Exception) {
                        if (e is IllegalStateException) {
                            event.player.sendMessage("${ChatColor.RED}Failed to read token shop: ${e.message}")
                        } else {
                            event.player.sendMessage("${ChatColor.RED}Failed to read token shop!")
                            e.printStackTrace()
                        }
                        return
                    }

                    if (event.action == Action.LEFT_CLICK_BLOCK) {
                        if (event.player.uniqueId == tokenShop.owner) {
                            return
                        }

                        if (event.player.gameMode == GameMode.CREATIVE) {
                            return
                        }

                        event.isCancelled = true

                        val userContext = if (tokenShop.buying) {
                            "selling"
                        } else {
                            "buying"
                        }

                        event.player.sendMessage("$TOKEN_SHOP_TAG ${Formats.formatPlayer(tokenShop.owner)} ${ChatColor.GRAY}is $userContext ${Formats.formatTokens(tokenShop.quantity.toLong())} ${ChatColor.GRAY}for ${Formats.formatMoney(tokenShop.price as BigDecimal)}${ChatColor.GRAY}.")
                    } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
                        event.isCancelled = true

                        try {
                            handleTransaction(event.player, tokenShop, sign.location)
                        } catch (e: Exception) {
                            event.player.sendMessage("${ChatColor.RED}Failed to transact with that token shop!")

                            if (event.player.isOp) {
                                event.player.sendMessage("${ChatColor.RED}[OP] Error: ${e.message}")
                            }
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
                    event.player.sendMessage("${ChatColor.RED}Quantity can't be negative!")
                    return
                }

                val quantity = NumberUtils.parseInput(event.lines[1])
                if (quantity.toInt() < 1) {
                    event.isCancelled = true
                    event.player.sendMessage("${ChatColor.RED}Quantity must be at least 1!")
                    return
                }

                val priceLineSplit = event.lines[2].split(" ")
                if (priceLineSplit[1].startsWith("-")) {
                    throw IllegalStateException("Price can't be negative!")
                }

                val price = NumberUtils.parseInput(priceLineSplit[1])
                if (price.toLong() < 1) {
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
                event.setLine(2, Formats.formatTokens(quantity.toLong()))
                event.setLine(3, "$buyOrSell ${ChatColor.BLACK}$formattedPrice")
            } catch (e: Exception) {
                event.player.sendMessage("${ChatColor.RED}You used the wrong format! Try this:")
                event.player.sendMessage("${ChatColor.GRAY}Line 1: [TokenShop]")
                event.player.sendMessage("${ChatColor.GRAY}Line 2: {quantity}")
                event.player.sendMessage("${ChatColor.GRAY}Line 3: B/S {price}")

                if (event.player.isOp) {
                    println("${ChatColor.DARK_RED}DEBUG OUTPUT:")
                    e.printStackTrace()
                }
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
                event.player.sendMessage("${ChatColor.RED}You can't destroy that token shop because it doesn't belong to you!")
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        useCooldown.removeCooldown(event.player.uniqueId)
    }

    data class TokenShop(
        val owner: UUID,
        val quantity: Number,
        val buying: Boolean,
        val price: Number
    )

    private fun readTokenShop(sign: Sign): TokenShop? {
        val owner = Cubed.instance.uuidCache.uuid(sign.lines[1]) ?: throw IllegalStateException("Couldn't determine owner of sign")

        val quantity = NumberUtils.parseInput(ChatColor.stripColor(sign.lines[2]).replace(Constants.TOKENS_SYMBOL, ""))
        if (quantity.toInt() < 0) {
            throw IllegalStateException("Quantity is negative")
        }

        val transactionInfo = ChatColor.stripColor(sign.lines[3]).split(" ")

        val buying = when {
            transactionInfo[0].equals("b", ignoreCase = true) -> {
                true
            }
            transactionInfo[0].equals("s", ignoreCase = true) -> {
                false
            }
            else -> {
                throw IllegalStateException("Couldn't determine if buying or selling")
            }
        }

        if (transactionInfo[1].startsWith("-")) {
            throw IllegalStateException("Price is negative")
        }

        val price = NumberUtils.parseInput(transactionInfo[1])
        if (price.toLong() < 0) {
            throw IllegalStateException("Price is negative")
        }

        return TokenShop(owner, quantity, buying, BigDecimal(price.toString()))
    }

    private fun handleTransaction(player: Player, tokenShop: TokenShop, location: Location) {
        Tasks.async {
            if (player.uniqueId == tokenShop.owner) {
                player.sendMessage("${ChatColor.RED}You can't buy or sell to your own token shop!")
                return@async
            }

            val owningUser = UserHandler.getOrLoadAndCacheUser(tokenShop.owner)
            val interactingUser = UserHandler.getOrLoadAndCacheUser(player.uniqueId, lookup = false, throws = true)

            val price = tokenShop.price as BigDecimal
            val quantity = tokenShop.quantity.toLong()

            Tasks.sync {
                if (tokenShop.buying) {
                    if (!owningUser.hasTokenBalance(quantity)) {
                        player.sendMessage("${ChatColor.RED}${owningUser.getUsername()} doesn't have enough tokens to sell you.")
                        return@sync
                    }

                    if (!interactingUser.hasMoneyBalance(price)) {
                        player.sendMessage("${ChatColor.RED}You don't have enough money to buy tokens from that token shop.")
                        return@sync
                    }

                    owningUser.addMoneyBalance(price)
                    owningUser.subtractTokensBalance(quantity)
                    owningUser.requiresSave()

                    interactingUser.subtractMoneyBalance(price)
                    interactingUser.addTokensBalance(quantity)
                    interactingUser.requiresSave()

                    val owningPlayer = owningUser.getPlayer()

                    val ownerName = if (owningPlayer != null) {
                        Formats.formatPlayer(owningPlayer)
                    } else {
                        owningUser.getUsername()
                    }

                    player.sendMessage("$TOKEN_SHOP_TAG You bought ${Formats.formatTokens(quantity)} ${ChatColor.GRAY}from $ownerName ${ChatColor.GRAY}for ${Formats.formatMoney(price)}${ChatColor.GRAY}!")

                    if (owningUser.settings.getSettingOption(UserSetting.TOKEN_SHOP_NOTIFICATIONS).getValue() as Boolean) {
                        owningPlayer?.sendMessage("$TOKEN_SHOP_TAG You sold ${Formats.formatTokens(quantity)} ${ChatColor.GRAY}to ${Formats.formatPlayer(player)} ${ChatColor.GRAY}for ${Formats.formatMoney(price)}${ChatColor.GRAY}!")
                    }

                    UserHandler.tokenShopsLogFile.commit(buildString {
                        append("${player.name} (${player.uniqueId})")
                        append(" purchased ${NumberUtils.format(quantity)} tokens from ${owningUser.getUsername()} for $${NumberUtils.format(price)}")
                        append(Formats.formatPlayer(tokenShop.owner))
                        append(" (${location.blockX}, ${location.blockY}, ${location.blockZ})")
                    })
                } else {
                    if (!owningUser.hasMoneyBalance(price)) {
                        player.sendMessage("${ChatColor.RED}${owningUser.getUsername()} doesn't have enough money to buy your tokens.")
                        return@sync
                    }

                    if (!interactingUser.hasTokenBalance(quantity)) {
                        player.sendMessage("${ChatColor.RED}You don't have enough tokens to sell to that token shop.")
                        return@sync
                    }

                    interactingUser.addMoneyBalance(price)
                    interactingUser.subtractTokensBalance(quantity)
                    interactingUser.requiresSave()

                    owningUser.subtractMoneyBalance(price)
                    owningUser.addTokensBalance(quantity)
                    owningUser.requiresSave()

                    val owningPlayer = owningUser.getPlayer()

                    val ownerName = if (owningPlayer != null) {
                        Formats.formatPlayer(owningPlayer)
                    } else {
                        owningUser.getUsername()
                    }

                    player.sendMessage("$TOKEN_SHOP_TAG You sold ${Formats.formatTokens(quantity)} ${ChatColor.GRAY}to $ownerName ${ChatColor.GRAY}for ${Formats.formatMoney(price)}${ChatColor.GRAY}!")

                    if (owningUser.settings.getSettingOption(UserSetting.TOKEN_SHOP_NOTIFICATIONS).getValue() as Boolean) {
                        owningPlayer?.sendMessage("$TOKEN_SHOP_TAG You bought ${Formats.formatTokens(quantity)} ${ChatColor.GRAY}to ${Formats.formatPlayer(player)} ${ChatColor.GRAY}for ${Formats.formatMoney(price)}${ChatColor.GRAY}!")
                    }

                    UserHandler.tokenShopsLogFile.commit(buildString {
                        append("${player.name} (${player.uniqueId})")
                        append(" sold ${NumberUtils.format(quantity)} tokens to ${owningUser.getUsername()} for $${NumberUtils.format(price)}")
                        append(Formats.formatPlayer(tokenShop.owner))
                        append(" (${location.blockX}, ${location.blockY}, ${location.blockZ})")
                    })
                }
            }
        }
    }

}