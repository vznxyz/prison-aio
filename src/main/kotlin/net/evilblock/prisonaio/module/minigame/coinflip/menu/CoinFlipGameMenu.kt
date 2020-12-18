/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.coinflip.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.CountdownButton
import net.evilblock.cubed.menu.buttons.SkullButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipGame
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class CoinFlipGameMenu(val game: CoinFlipGame) : Menu() {

    init {
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        val formattedValue = game.currency.format(game.currencyAmount)

        return when (game.stage) {
            CoinFlipGame.Stage.WAITING_FOR_OPPONENT -> {
                "$formattedValue ${ChatColor.GRAY}${ChatColor.BOLD}WAITING"
            }
            CoinFlipGame.Stage.WAITING_FOR_SPECTATORS -> {
                "$formattedValue ${ChatColor.GREEN}${ChatColor.BOLD}STARTING"
            }
            CoinFlipGame.Stage.ROLLING -> {
                "${game.creator.getUsername()} vs. ${game.opponent!!.getUsername()} ($formattedValue${ChatColor.DARK_GRAY})"
            }
            CoinFlipGame.Stage.FINISHED -> {
                "${game.winner!!.getUsername()} ${ChatColor.GREEN}${ChatColor.BOLD}WINS"
            }
        }
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        when (game.stage) {
            CoinFlipGame.Stage.WAITING_FOR_OPPONENT,
            CoinFlipGame.Stage.WAITING_FOR_SPECTATORS -> {
                buttons[11] = PlayerButton(game.creator, true)

                if (game.opponent != null) {
                    buttons[15] = PlayerButton(game.opponent!!, false)
                } else {
                    if (game.creator.uuid == player.uniqueId) {
                        buttons[15] = WaitingButton()
                    } else {
                        buttons[15] = JoinGameButton()
                    }
                }

                if (game.isStartingSoon()) {
                    buttons[13] = CountdownButton(text = "${ChatColor.GREEN}${ChatColor.BOLD}Starting in ${game.getSecondsRemaining()}...", number = game.getSecondsRemaining())
                } else {
                    buttons[13] = ValueButton()
                }

                for (i in PRIMARY_SLOTS) {
                    buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, CoinFlipHandler.PRIMARY_COLOR_ID, " ")
                }

                for (i in SECONDARY_SLOTS) {
                    buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, CoinFlipHandler.SECONDARY_COLOR_ID, " ")
                }
            }
            CoinFlipGame.Stage.ROLLING -> {
                val inverted = game.getColorInvertAnimationSwitch()

                if (inverted) {
                    buttons[22] = PlayerButton(game.opponent!!, true)
                } else {
                    buttons[22] = PlayerButton(game.creator, false)
                }

                val primaryColor: Byte
                val secondaryColor: Byte

                if (inverted) {
                    primaryColor = CoinFlipHandler.SECONDARY_COLOR_ID
                    secondaryColor = CoinFlipHandler.PRIMARY_COLOR_ID
                } else {
                    primaryColor = CoinFlipHandler.PRIMARY_COLOR_ID
                    secondaryColor = CoinFlipHandler.SECONDARY_COLOR_ID
                }

                for (i in ANIMATION_PRIMARY) {
                    buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, primaryColor, " ")
                }

                for (i in ANIMATION_SECONDARY) {
                    buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, secondaryColor, " ")
                }
            }
            CoinFlipGame.Stage.FINISHED -> {
                val primaryColor: Byte
                val secondaryColor: Byte

                buttons[22] = PlayerButton(game.winner!!, false)

                if (game.creator == game.winner!!) {
                    primaryColor = CoinFlipHandler.PRIMARY_COLOR_ID
                    secondaryColor = CoinFlipHandler.SECONDARY_COLOR_ID
                } else {
                    primaryColor = CoinFlipHandler.SECONDARY_COLOR_ID
                    secondaryColor = CoinFlipHandler.PRIMARY_COLOR_ID
                }

                for (i in ANIMATION_PRIMARY) {
                    buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, primaryColor, " ")
                }

                for (i in ANIMATION_SECONDARY) {
                    buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, secondaryColor, " ")
                }
            }
        }

        return buttons
    }

    override fun onOpen(player: Player) {
        if (game.stage == CoinFlipGame.Stage.FINISHED && game.stageTicks >= 15) {
            CoinFlipBrowserMenu().openMenu(player)
            return
        }

        super.onOpen(player)
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        game.watchers.remove(player.uniqueId)

        if (manualClose) {
            Tasks.delayed(1L) {
                CoinFlipBrowserMenu().openMenu(player)
            }
        }
    }

    override fun getAutoUpdateTicks(): Long {
        return 500L
    }

    private inner class PlayerButton(private val user: User, private val white: Boolean) : SkullButton(owner = user.uuid) {
        override fun getName(player: Player): String {
            return if (white) {
                "${CoinFlipHandler.PRIMARY_COLOR}${ChatColor.BOLD}${user.getUsername()}"
            } else {
                "${CoinFlipHandler.SECONDARY_COLOR}${ChatColor.BOLD}${user.getUsername()}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Wins: ${ChatColor.GREEN}${NumberUtils.format(user.statistics.getCoinflipWins())}")
            description.add("${ChatColor.GRAY}Losses: ${ChatColor.RED}${NumberUtils.format(user.statistics.getCoinflipLosses())}")

            val moneyProfit = user.statistics.getCoinflipProfit(Currency.Type.MONEY)

            val moneyProfitStyle = when {
                moneyProfit.toDouble() == 0.0 -> {
                    "${ChatColor.YELLOW}="
                }
                moneyProfit.toDouble() > 0 -> {
                    "${ChatColor.GREEN}+"
                }
                else -> {
                    "${ChatColor.RED}-"
                }
            }

            description.add("${ChatColor.GRAY}Net Profit (Money): $moneyProfitStyle${Currency.Type.MONEY.format(moneyProfit)}")

            val tokensProfit = user.statistics.getCoinflipProfit(Currency.Type.TOKENS)

            val tokensProfitStyle = when {
                tokensProfit.toLong() == 0L -> {
                    "${ChatColor.YELLOW}="
                }
                tokensProfit.toLong() > 0 -> {
                    "${ChatColor.GREEN}+"
                }
                else -> {
                    "${ChatColor.RED}-"
                }
            }

            description.add("${ChatColor.GRAY}Net Profit (Tokens): $tokensProfitStyle${Currency.Type.TOKENS.format(moneyProfit)}")
            description.add("")
            description.add("${ChatColor.GRAY}Last 24 Hrs: ${Currency.Type.MONEY.format(0)} ${ChatColor.GRAY}/ ${Currency.Type.TOKENS.format(0)}")
            description.add("${ChatColor.GRAY}Last 7 Days: ${Currency.Type.MONEY.format(0)} ${ChatColor.GRAY}/ ${Currency.Type.TOKENS.format(0)}")

            return description
        }
    }

    private inner class WaitingButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GRAY}${ChatColor.BOLD}WAITING FOR OPPONENT"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}You're waiting on somebody to join",
                "${ChatColor.GRAY}your game."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.MINECART
        }
    }

    private inner class JoinGameButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Join Game"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Feeling lucky? Join this coinflip",
                "${ChatColor.GRAY}game as ${game.creator.getUsername()}'s opponent.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to join game"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (!game.isWaitingForOpponent()) {
                    return
                }

                if (game.creator.uuid == player.uniqueId || game.opponent?.uuid == player.uniqueId) {
                    return
                }

                if (!game.currency.has(player.uniqueId, game.currencyAmount)) {
                    player.sendMessage("${ChatColor.RED}You don't have enough money to match the game's bet.")
                    return
                }

                ConfirmMenu("${ChatColor.GREEN}${ChatColor.BOLD}ENTER GAME?") { confirmed ->
                    if (confirmed) {
                        if (game.isWaitingForOpponent()) {
                            game.currency.take(player.uniqueId, game.currencyAmount)
                            game.opponent = UserHandler.getUser(player.uniqueId)
                            game.sendMessage("${CoinFlipHandler.CHAT_PREFIX}${ChatColor.AQUA}${ChatColor.BOLD}${player.name} ${ChatColor.GRAY}has entered the game!")
                            game.sendMessage("${CoinFlipHandler.CHAT_PREFIX}Starting in 3 seconds...")
                        } else {
                            game.sendMessage("${ChatColor.RED}That game has already started!")
                        }
                    }

                    this@CoinFlipGameMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class ValueButton : Button() {
        override fun getName(player: Player): String {
            val formattedValue = game.currency.format(game.currencyAmount)
            return "$formattedValue ${ChatColor.GRAY}${ChatColor.BOLD}POT"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "Each player in the game must put up the amount above. The ${ChatColor.GREEN}${ChatColor.BOLD}winner ${ChatColor.GRAY}will take both the money they put up, and the ${ChatColor.RED}${ChatColor.BOLD}loser${ChatColor.GRAY}'s money.",
                linePrefix = "${ChatColor.GRAY}"
            ))

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "Each player has a ${ChatColor.YELLOW}${ChatColor.BOLD}50% ${ChatColor.GRAY}chance of winning, and the winner is picked ${ChatColor.AQUA}${ChatColor.BOLD}randomly${ChatColor.GRAY}.",
                linePrefix = "${ChatColor.GRAY}"
            ))

            return description
        }

        override fun getMaterial(player: Player): Material {
            return if (game.currency == Currency.Type.MONEY) {
                Material.DOUBLE_PLANT
            } else {
                Material.MAGMA_CREAM
            }
        }
    }

    companion object {
        private val PRIMARY_SLOTS = arrayListOf(1, 2, 3, 5, 6, 7, 10, 16, 19, 20, 21, 23, 24, 25)
        private val SECONDARY_SLOTS = arrayListOf(0, 8, 9, 17, 18, 26, 4, 12, 14, 22)

        private val ANIMATION_PRIMARY = arrayListOf(
            0, 2, 3, 4, 5, 6, 8,
            9, 11, 15, 17,
            18, 20, 24, 26,
            27, 29, 33, 35,
            36, 38, 39, 40, 41, 42, 44
        )

        private val ANIMATION_SECONDARY = arrayListOf(
            1, 7,
            10, 12, 13, 14, 16,
            19, 21, 23, 25,
            28, 30, 31, 32, 34,
            37, 43
        )
    }

}