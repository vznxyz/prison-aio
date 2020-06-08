package net.evilblock.prisonaio.module.battlepass.challenge.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeType
import net.evilblock.prisonaio.module.battlepass.menu.BattlePassEditorMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ChallengeEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "BattlePass Challenge Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = AddChallengeButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (challenge in ChallengeHandler.getChallenges().filter { !it.daily }) {
            buttons[buttons.size] = ChallengeButton(challenge)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                BattlePassEditorMenu().openMenu(player)
            }
        }
    }

    private inner class AddChallengeButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Challenge"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "Create a new challenge by following the setup procedure.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new challenge")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText("${ChatColor.GREEN}Please input an ID for the challenge.")
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .acceptInput { player, input ->
                        if (ChallengeHandler.getChallengeById(input) != null) {
                            player.sendMessage("${ChatColor.RED}A challenge's ID must be unique, and a challenge with the ID `$input` already exists.")
                            return@acceptInput
                        }

                        SelectChallengeTypeMenu { challengeType ->
                            challengeType.startSetupPrompt(player, input.toLowerCase()) { challenge ->
                                ChallengeHandler.trackChallenge(challenge)
                                ChallengeHandler.saveData()

                                player.sendMessage("${ChatColor.GREEN}Successfully created a new challenge.")

                                EditChallengeMenu(challenge).openMenu(player)
                            }
                        }.openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class ChallengeButton(private val challenge: Challenge) : Button() {
        override fun getName(player: Player): String {
            return challenge.name
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}(ID: ${challenge.id})")
            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = challenge.getText(), linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit challenge")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete challenge")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SADDLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditChallengeMenu(challenge).openMenu(player)
            }

            if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        ChallengeHandler.forgetChallenge(challenge)
                        ChallengeHandler.saveData()
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class SelectChallengeTypeMenu(private val lambda: (ChallengeType) -> Unit) : Menu() {
        override fun getTitle(player: Player): String {
            return "Select Challenge Type"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            for (challengeType in ChallengeHandler.CHALLENGE_TYPES) {
                buttons[buttons.size] = ChallengeTypeButton(challengeType)
            }

            return buttons
        }

        override fun onClose(player: Player, manualClose: Boolean) {
            if (manualClose) {
                Tasks.delayed(1L) {
                    this@ChallengeEditorMenu.openMenu(player)
                }
            }
        }

        private inner class ChallengeTypeButton(private val type: ChallengeType) : Button() {
            override fun getName(player: Player): String {
                return "${ChatColor.YELLOW}${ChatColor.BOLD}${type.getName()} Challenge"
            }

            override fun getDescription(player: Player): List<String> {
                val description = arrayListOf<String>()

                description.add("")
                description.addAll(TextSplitter.split(length = 40, text = type.getDescription(), linePrefix = "${ChatColor.GRAY}"))
                description.add("")
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to select challenge")

                return description
            }

            override fun getMaterial(player: Player): Material {
                return type.getIcon().type
            }

            override fun getDamageValue(player: Player): Byte {
                return type.getIcon().durability.toByte()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    lambda.invoke(type)
                }
            }
        }
    }

}