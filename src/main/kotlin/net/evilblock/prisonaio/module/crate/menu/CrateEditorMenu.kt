/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CrateHandler
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.text.NumberFormat

class CrateEditorMenu : PaginatedMenu() {

    init {
        autoUpdate = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Crate Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()
        buttons[2] = CreateCrateButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        CrateHandler.getCrates().forEachIndexed { index, crate ->
            buttons[buttons.size] = CrateButton(crate)
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

    private inner class CreateCrateButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Crate"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Create a new crate by completing",
                "${ChatColor.GRAY}the setup procedure.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new crate"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText(EzPrompt.IDENTIFIER_PROMPT)
                    .charLimit(16)
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .acceptInput { player, input ->
                        if (CrateHandler.findCrate(input) != null) {
                            player.sendMessage("${ChatColor.RED}A crate's ID must be unique, and a crate with the ID `$input` already exists.")
                            return@acceptInput
                        }

                        val crate = Crate(input)

                        CrateHandler.trackCrate(crate)
                        CrateHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully created a new crate.")

                        openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class CrateButton(private val crate: Crate) : Button() {
        override fun getName(player: Player): String {
            return crate.name
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("${ChatColor.GRAY}(ID: ${crate.id})")
            description.add("")

            val formattedRewardsSize = NumberFormat.getInstance().format(crate.rewards.size.toLong())
            description.add("${ChatColor.GRAY}Number of Rewards: ${ChatColor.GREEN}$formattedRewardsSize")

            val formattedRewardsRange = "${crate.rewardsRange.first}-${crate.rewardsRange.last}"
            description.add("${ChatColor.GRAY}Rewards Range: ${ChatColor.GREEN}$formattedRewardsRange")

            val formattedReRoll = if (crate.reroll) {
                "${ChatColor.GREEN}Enabled"
            } else {
                "${ChatColor.RED}Disabled"
            }

            description.add("${ChatColor.GRAY}Re-Roll: $formattedReRoll")

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit crate")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete crate")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return crate.keyItemStack.type
        }

        override fun getDamageValue(player: Player): Byte {
            return crate.keyItemStack.durability.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditCrateMenu(crate).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        CrateHandler.forgetCrate(crate)
                        CrateHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully deleted crate.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made to crate.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

}