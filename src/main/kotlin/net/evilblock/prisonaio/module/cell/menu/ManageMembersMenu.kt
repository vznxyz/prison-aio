/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.prompt.PlayerPrompt
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.cell.Cell
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class ManageMembersMenu(private val cell: Cell) : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Manage Members"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()
        buttons[0] = InvitePlayerButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (member in cell.getMembers()) {
            buttons[buttons.size] = MemberButton(member)
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
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                JerryMenu(cell.guideNpc)
            }, 1L)
        }
    }

    private inner class InvitePlayerButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Invite Player"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Invite a player to join your cell.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to invite a player"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!cell.isOwner(player.uniqueId)) {
                player.sendMessage("${ChatColor.RED}You must be the owner of the cell to invite other players.")
                return
            }

            player.closeInventory()

            ConversationUtil.startConversation(player, PlayerPrompt() { invitedPlayer ->
                if (cell.isMember(invitedPlayer)) {
                    player.sendMessage("${ChatColor.RED}That player is already a member of the cell.")
                    return@PlayerPrompt
                }

                if (cell.isInvited(invitedPlayer)) {
                    player.sendMessage("${ChatColor.RED}That player has already been invited to the cell.")
                    return@PlayerPrompt
                }

                cell.invitePlayer(invitedPlayer, player.uniqueId)

                val playerInvitedName = Cubed.instance.uuidCache.name(invitedPlayer)
                player.sendMessage("${ChatColor.GREEN}Successfully invited $playerInvitedName to the cell.")
            })
        }
    }

    private inner class MemberButton(private val member: UUID) : Button() {
        override fun getName(player: Player): String {
            val memberName = Cubed.instance.uuidCache.name(member)
            val memberPlayer = Bukkit.getPlayer(member)
            return if (memberPlayer == null) {
                "${ChatColor.RED}${ChatColor.BOLD}$memberName"
            } else {
                if (cell.isActivePlayer(memberPlayer)) {
                    "${ChatColor.GREEN}${ChatColor.BOLD}$memberName"
                } else {
                    "${ChatColor.YELLOW}${ChatColor.BOLD}$memberName"
                }
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}Invited by: ${ChatColor.YELLOW}Unknown")
            description.add("${ChatColor.GRAY}Last played: ${ChatColor.YELLOW}Unknown")

            if (cell.isOwner(player.uniqueId)) {
                description.add("")
                description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to kick member")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isRightClick) {
                if (!cell.isOwner(player.uniqueId)) {
                    player.sendMessage("${ChatColor.RED}You must be the owner of the cell to kick members.")
                    return
                }

                if (player.uniqueId == member) {
                    player.sendMessage("${ChatColor.RED}You can't kick yourself from the cell.")
                    return
                }

                if (cell.isOwner(member)) {
                    player.sendMessage("${ChatColor.RED}You can't kick the owner from their own cell.")
                    return
                }

                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        cell.kickMember(member)

                        val memberName = Cubed.instance.uuidCache.name(member)
                        player.sendMessage("${ChatColor.GREEN}Successfully kicked $memberName from the cell.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made to members.")
                    }
                }.openMenu(player)
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)
            val meta = item.itemMeta as SkullMeta
            meta.owner = Cubed.instance.uuidCache.name(member)
            item.itemMeta = meta
            return item
        }
    }

}