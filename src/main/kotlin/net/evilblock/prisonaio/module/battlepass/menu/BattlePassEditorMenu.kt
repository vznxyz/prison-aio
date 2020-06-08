package net.evilblock.prisonaio.module.battlepass.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.battlepass.challenge.menu.ChallengeEditorMenu
import net.evilblock.prisonaio.module.battlepass.tier.menu.TierEditorMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class BattlePassEditorMenu : Menu() {

    override fun getTitle(player: Player): String {
        return "BattlePass Editor"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[11] = ResetDailyChangesButton()
        buttons[13] = EditTiersButton()
        buttons[15] = EditChallengesButton()

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 27
    }

    private inner class ResetDailyChangesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Reset Daily Challenges"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "Re-generate the daily challenges. All currently tracked user progress for the day will be lost.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to reset daily changes")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.COMMAND
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ConfirmMenu("Reset Daily Challenges?") { confirmed ->
                    if (confirmed) {
                        player.sendMessage("${ChatColor.GREEN}You have reset the BattlePass's daily challenges!")
                        // TODO: reset daily challenges
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class EditTiersButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Edit Tiers"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "Tiers are BattlePass milestones that give rewards as you gain experience through completing challenges.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit tiers")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                TierEditorMenu().openMenu(player)
            }
        }
    }

    private inner class EditChallengesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Edit Challenges"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "Challenges reward players experience to level up their BattlePass tiers.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit tiers")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SADDLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ChallengeEditorMenu().openMenu(player)
            }
        }
    }

}