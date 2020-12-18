/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.invite.GangInvite
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class InvitesMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "${ChatColor.BLUE}${ChatColor.BOLD}Your Invites"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = BackButton { GangsMenu().openMenu(player) }
            buttons[4] = InfoButton()
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (gang in GangHandler.getGangsInvitedTo(player)) {
                val invite = gang.getInviteInfo(player.uniqueId)
                if (invite != null) {
                    buttons[buttons.size] = InviteButton(gang, invite)
                }
            }
        }
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.WHITE}${ChatColor.BOLD}Gang Invites"
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }
    }

    private inner class InviteButton(private val gang: Gang, private val invite: GangInvite) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${gang.name}"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val formattedTime = TimeUtil.formatIntoAbbreviatedString((invite.getRemainingTime() / 1000.0).toInt())

                desc.add("${ChatColor.GRAY}(Invites expires in ${ChatColor.RED}${ChatColor.BOLD}$formattedTime${ChatColor.GRAY})")
                desc.add("")
                desc.addAll(TextSplitter.split(text = "You were invited to join this gang by ${ChatColor.YELLOW}${gang.getLeaderUsername()}${ChatColor.GRAY}!"))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to accept invite"))
                desc.add(styleAction(ChatColor.RED, "RIGHT-CLICK", "to discard invite"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                GangHandler.attemptJoinGang(player, gang)
            } else if (clickType.isRightClick) {
                gang.removeInvite(player)
            }
        }
    }

}