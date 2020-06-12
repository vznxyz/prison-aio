package net.evilblock.prisonaio.module.minigame.coinflip.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.HelpButton
import net.evilblock.cubed.menu.buttons.SkullButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipGame
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat

class CoinFlipBrowserMenu : PaginatedMenu() {

    init {
        autoUpdate = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return CoinFlipHandler.getMenuTitle()
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = CreateGameButton()
        buttons[4] = GuideButton()

        for (i in 9..17) {
            buttons[i] = if (i % 2 == 0) {
                Button.placeholder(Material.STAINED_GLASS_PANE, CoinFlipHandler.PRIMARY_COLOR_ID, " ")
            } else {
                Button.placeholder(Material.STAINED_GLASS_PANE, CoinFlipHandler.SECONDARY_COLOR_ID, " ")
            }
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (game in CoinFlipHandler.getGames()) {
            buttons[buttons.size] = GameButton(game)
        }

        return buttons
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getAutoUpdateTicks(): Long {
        return 10L
    }

    private inner class CreateGameButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Create Game"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Create a coinflip game that anyone",
                "${ChatColor.GRAY}can join, using money or tokens.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a game"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectCurrencyMenu { useMoney ->
                    val prompt = EzPrompt.Builder()

                    if (useMoney) {
                        prompt.promptText("${ChatColor.GREEN}Please insert the amount of money you would like to bet.")
                            .regex("[0-9]*(\\.?[0-9]*)?".toRegex())
                            .acceptInput { player, input ->
                                val amount = input.toDouble()
                                if (amount < CoinFlipHandler.getMinBetMoney().coerceAtLeast(1.0)) {
                                    player.sendMessage("${ChatColor.RED}You must bet at least $${NumberFormat.getInstance().format(CoinFlipHandler.getMinBetMoney())}.")
                                    return@acceptInput
                                }

                                val currency = Currency.Money(amount)
                                if (!currency.has(player)) {
                                    player.sendMessage("${ChatColor.RED}You don't have that much money to bet.")
                                    return@acceptInput
                                }

                                currency.take(player)

                                val game = CoinFlipGame(
                                    creator = UserHandler.getUser(player.uniqueId),
                                    value = currency
                                )

                                CoinFlipHandler.trackGame(game)
                                CoinFlipGameMenu(game).openMenu(player)

                                player.sendMessage("${ChatColor.GREEN}You've created a Coinflip game for ${CoinFlipHandler.formatMoney(game.value.double())}${ChatColor.GREEN}.")
                            }
                    } else {
                        prompt.promptText("${ChatColor.GREEN}Please insert the amount of tokens you would like to bet.")
                            .acceptInput { player, input ->
                                val amount = input.toLong()
                                if (amount < CoinFlipHandler.getMinBetTokens().coerceAtLeast(1L)) {
                                    player.sendMessage("${ChatColor.RED}You must bet at least ${NumberFormat.getInstance().format(CoinFlipHandler.getMinBetMoney())} tokens.")
                                    return@acceptInput
                                }

                                val currency = Currency.Tokens(amount)
                                if (!currency.has(player)) {
                                    player.sendMessage("${ChatColor.RED}You don't have that many tokens to bet.")
                                    return@acceptInput
                                }

                                currency.take(player)

                                val game = CoinFlipGame(
                                    creator = UserHandler.getUser(player.uniqueId),
                                    value = currency
                                )

                                CoinFlipHandler.trackGame(game)
                                CoinFlipGameMenu(game).openMenu(player)

                                player.sendMessage("${ChatColor.GREEN}You've created a Coinflip game for ${CoinFlipHandler.formatTokens(game.value.long())}${ChatColor.GREEN}.")
                            }
                    }

                    prompt.build().start(player)
                }.openMenu(player)
            }
        }
    }

    private inner class GameButton(private val game: CoinFlipGame) : SkullButton(owner = game.creator.uuid) {
        override fun getName(player: Player): String {
            val title = StringBuilder().append("${CoinFlipHandler.PRIMARY_COLOR}${ChatColor.BOLD}${game.creator.getUsername()}")

            if (game.isWaitingForOpponent()) {
                if (game.value.isMoney()) {
                    title.append(" ${CoinFlipHandler.formatMoney(game.value.double())}")
                } else {
                    title.append(" ${CoinFlipHandler.formatTokens(game.value.long())}")
                }
            } else {
                title.append("${ChatColor.GRAY} vs ${CoinFlipHandler.SECONDARY_COLOR}${ChatColor.BOLD}${game.opponent!!.getUsername()}")
            }

            return title.toString()
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            when (game.stage) {
                CoinFlipGame.Stage.WAITING_FOR_OPPONENT -> {
                    description.add("${ChatColor.GRAY}${ChatColor.BOLD}WAITING FOR OPPONENT")
                }
                CoinFlipGame.Stage.WAITING_FOR_SPECTATORS -> {
                    description.add("${ChatColor.YELLOW}STARTING SOON")
                }
                CoinFlipGame.Stage.ROLLING,
                CoinFlipGame.Stage.FINISHED -> {
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}PLAYING NOW")
                }
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to spectate this game")

            if (game.isCreator(player) && game.isWaitingForOpponent()) {
                description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to cancel this game")
            }

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                CoinFlipGameMenu(game).openMenu(player)
            }

            if (clickType.isRightClick) {
                if (game.isCreator(player) && game.isWaitingForOpponent()) {
                    ConfirmMenu("Are you sure?") { confirmed ->
                        if (confirmed) {
                            game.finishGame()
                        }

                        this@CoinFlipBrowserMenu.openMenu(player)
                    }.openMenu(player)
                }
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)

            if (game.stage != CoinFlipGame.Stage.WAITING_FOR_OPPONENT) {
                GlowEnchantment.addGlow(item)
            }

            return item
        }
    }

    private inner class GuideButton : HelpButton() {
        override fun getName(player: Player): String {
            return CoinFlipHandler.getGuideTitle()
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "Coinflip games that are highlighted are currently on-going and can be spectated by ${ChatColor.YELLOW}${ChatColor.BOLD}left-clicking${ChatColor.GRAY}.",
                linePrefix = "${ChatColor.GRAY}"
            ))

            return description
        }
    }

    private inner class SelectCurrencyMenu(private val lambda: (Boolean) -> Unit) : Menu() {
        init {
            placeholder = true
        }

        override fun getTitle(player: Player): String {
            return "Select Currency"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            buttons[1] = Button.placeholder(Material.STAINED_GLASS_PANE, 5, " ")
            buttons[3] = Button.placeholder(Material.STAINED_GLASS_PANE, 5, " ")

            buttons[5] = Button.placeholder(Material.STAINED_GLASS_PANE, 1, " ")
            buttons[7] = Button.placeholder(Material.STAINED_GLASS_PANE, 1, " ")

            buttons[2] = object : Button() {
                override fun getName(player: Player): String {
                    return "${ChatColor.GREEN}${ChatColor.BOLD}Money"
                }

                override fun getDescription(player: Player): List<String> {
                    return TextSplitter.split(length = 40, text = "Use money as your coinflip currency for this game.", linePrefix = "${ChatColor.GRAY}")
                }

                override fun getMaterial(player: Player): Material {
                    return Material.DOUBLE_PLANT
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                    player.closeInventory()
                    lambda.invoke(true)
                }
            }

            buttons[6] = object : Button() {
                override fun getName(player: Player): String {
                    return "${ChatColor.GOLD}${ChatColor.BOLD}Tokens"
                }

                override fun getDescription(player: Player): List<String> {
                    return TextSplitter.split(length = 40, text = "Use tokens as your coinflip currency for this game.", linePrefix = "${ChatColor.GRAY}")
                }

                override fun getMaterial(player: Player): Material {
                    return Material.MAGMA_CREAM
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                    player.closeInventory()
                    lambda.invoke(false)
                }
            }

            return buttons
        }

        override fun onClose(player: Player, manualClose: Boolean) {
            if (manualClose) {
                Tasks.delayed(1L) {
                    this@CoinFlipBrowserMenu.openMenu(player)
                }
            }
        }
    }

}