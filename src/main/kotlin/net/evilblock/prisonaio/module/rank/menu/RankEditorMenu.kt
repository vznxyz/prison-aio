package net.evilblock.prisonaio.module.rank.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class RankEditorMenu : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Rank Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()
        buttons[2] = AddRankButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        RankHandler.getSortedRanks().forEachIndexed { index, rank ->
            buttons[index] = RankButton(rank)
        }

        return buttons
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class AddRankButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Rank"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Create a new rank by completing",
                "${ChatColor.GRAY}the setup procedure.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new rank"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText(EzPrompt.IDENTIFIER_PROMPT)
                    .charLimit(16)
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .acceptInput { player, input ->
                        if (RankHandler.getRankById(input).isPresent) {
                            player.sendMessage("${ChatColor.RED}A rank's ID must be unique, and a rank with the ID `$input` already exists.")
                            return@acceptInput
                        }

                        val rank = Rank(input)

                        RankHandler.trackRank(rank)
                        RankHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully created a new rank.")

                        openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class RankButton(private val rank: Rank) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Rank ${ChatColor.RESET}${rank.displayName}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")

            val formattedPrice = NumberUtils.format(rank.price)
            description.add("${ChatColor.GRAY}Price: ${ChatColor.AQUA}$${ChatColor.GREEN}${formattedPrice}")
            description.add("${ChatColor.GRAY}Sort Order: ${ChatColor.GREEN}${rank.sortOrder}")
            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Console Commands")

            if (rank.getCommands().isEmpty()) {
                description.add("${ChatColor.GRAY}None attached")
            } else {
                for (command in rank.getCommands()) {
                    description.add(" ${ChatColor.RESET}$command")
                }
            }

            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Permissions")

            if (rank.getPermissions().isEmpty()) {
                description.add("${ChatColor.GRAY}None attached")
            } else {
                for (permission in rank.getPermissions()) {
                    description.add(" ${ChatColor.RESET}$permission")
                }
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit rank")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete rank")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENCHANTED_BOOK
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditRankMenu(rank).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        RankHandler.forgetRank(rank)
                        RankHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully deleted rank ${ChatColor.RESET}${rank.displayName}${ChatColor.GREEN}.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made to rank.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

}