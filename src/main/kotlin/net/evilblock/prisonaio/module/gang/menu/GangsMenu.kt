/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.protocol.MenuCompatibility
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangsModule
import net.evilblock.prisonaio.module.gang.advertisement.menu.ViewAdvertisementsMenu
import net.evilblock.prisonaio.module.gang.permission.GangPermission
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.MainMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class GangsMenu : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Gangs"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = BackButton { MainMenu(UserHandler.getUser(player)).openMenu(player) }
            buttons[4] = InfoButton()
            buttons[8] = GangInvitesButton()

            val gang = GangHandler.getGangByPlayer(player)
            if (gang == null) {
                buttons[21] = CreateGangButton()
                buttons[23] = FindGangButton()
            } else {
                buttons[19] = ManageGangButton(gang)
                buttons[20] = VisitGangButton(gang)

                if (gang.testPermission(player, GangPermission.SPEND_TROPHIES))

                if (gang.isLeader(player.uniqueId)) {
                    buttons[21] = DisbandGangButton(gang)
                }
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 36
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Gangs"
        }

        override fun getMaterial(player: Player): Material {
            return Material.BEACON
        }
    }

    private inner class GangInvitesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.WHITE}${ChatColor.BOLD}Gang Invites"
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InvitesMenu().openMenu(player)
            }
        }
    }

    private inner class CreateGangButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Create a Gang"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Create your own gang and invite your friends. Grind to the top of the leaderboards to win rewards!"))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to create gang"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a name for your gang.")
                    .acceptInput { input ->
                        if (GangHandler.getGangByPlayer(player) != null) {
                            player.sendMessage("${ChatColor.RED}You already belong to a gang!")
                            return@acceptInput
                        }

                        try {
                            GangHandler.createNewGang(player, input) { gang ->
                                Tasks.sync {
                                    GangHandler.attemptJoinSession(player, gang)
                                    player.sendMessage("${ChatColor.YELLOW}You are now the leader of this gang. Use ${ChatColor.YELLOW}/gang home ${ChatColor.YELLOW}to teleport back to your gang headquarters.")
                                }
                            }
                        } catch (e: Exception) {
                            if (player.isOp) {
                                player.sendMessage("${ChatColor.RED}Failed to generate a new gang for you: ${e.message}")
                            } else {
                                player.sendMessage("${ChatColor.RED}Failed to generate a new gang for you. If this issue persists, please contact an administrator.")
                            }
                        }
                    }
                    .start(player)
            }
        }
    }

    private inner class FindGangButton : TexturedHeadButton(Constants.IB_ICON_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Find/Join a Gang"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Find or join a gang using our advertisement system."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to find/join gang"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ViewAdvertisementsMenu().openMenu(player)
            }
        }
    }

    private inner class ManageGangButton(private val gang: Gang) : Button() {
        override fun getName(player: Player): String {
            return if (gang.isLeader(player.uniqueId)) {
                "${ChatColor.RED}${ChatColor.BOLD}Your Gang"
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}${gang.getLeaderUsername()}'s Gang"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("${ChatColor.YELLOW}Members${ChatColor.WHITE}: ${ChatColor.AQUA}${NumberUtils.format(gang.getMembers().size)}/${NumberUtils.format(GangsModule.getMaxMembers())}")
                desc.add("${ChatColor.YELLOW}Trophies${ChatColor.WHITE}: ${ChatColor.GOLD}${NumberUtils.format(gang.getTrophies())}")
                desc.add("")
                desc.add("${ChatColor.YELLOW}Current Session")

                val visitingPlayers = gang.getVisitingPlayers()
                if (visitingPlayers.isEmpty()) {
                    desc.add("  ${ChatColor.GRAY}Nobody is playing right now")
                } else {
                    for (activePlayer in visitingPlayers) {
                        desc.add(" ${ChatColor.GRAY}${activePlayer.name}")
                    }
                }

                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to view gang options"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.BEACON
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                GangMenu(gang).openMenu(player)
            }
        }
    }

    private inner class VisitGangButton(private val gang: Gang) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Visit Headquarters"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Teleport to your gang's headquarters."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to teleport to HQ"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENDER_PEARL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                GangHandler.attemptJoinSession(player, gang)
            }
        }
    }

    private inner class DisbandGangButton(private val gang: Gang) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.DARK_RED}${ChatColor.BOLD}Disband Gang"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Disband your gang. All of your gang's progress, and items stored in your gang's HQ, will be gone forever if you disband."))
                desc.add("")
                desc.add(styleAction(ChatColor.DARK_RED, "LEFT-CLICK", "to disband gang"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return MenuCompatibility.getBarrierOrReplacement()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        GangHandler.disbandGang(gang)
                    }

                    this@GangsMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}