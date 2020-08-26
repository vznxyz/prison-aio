/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.prompt.PlayerPrompt
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangMember
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.time.Instant
import java.util.*

class ManageMembersMenu(private val gang: Gang) : PaginatedMenu() {

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Manage Members"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()
        buttons[0] = InvitePlayerButton()

        for (i in 9..17) {
            buttons[i] = GlassButton(0)
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (member in gang.getMembers().values) {
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
                JerryMenu(gang.guideNpc)
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
            if (!gang.isLeader(player.uniqueId)) {
                player.sendMessage("${ChatColor.RED}You must be the leader of the gang to invite other players.")
                return
            }

            player.closeInventory()

            ConversationUtil.startConversation(player, PlayerPrompt { invitedPlayer ->
                if (gang.isMember(invitedPlayer)) {
                    player.sendMessage("${ChatColor.RED}That player is already a member of the gang.")
                    return@PlayerPrompt
                }

                if (gang.isInvited(invitedPlayer)) {
                    player.sendMessage("${ChatColor.RED}That player has already been invited to the gang.")
                    return@PlayerPrompt
                }

                gang.invitePlayer(invitedPlayer, player.uniqueId)

                val playerInvitedName = Cubed.instance.uuidCache.name(invitedPlayer)
                player.sendMessage("${ChatColor.GREEN}Successfully invited $playerInvitedName to the gang.")
            })
        }
    }

    private inner class MemberButton(private val member: GangMember) : Button() {
        override fun getName(player: Player): String {
            val memberName = Cubed.instance.uuidCache.name(member.uuid)
            val memberPlayer = Bukkit.getPlayer(member.uuid)

            return if (memberPlayer == null) {
                "${ChatColor.RED}${ChatColor.BOLD}$memberName"
            } else {
                if (gang.isActivePlayer(memberPlayer)) {
                    "${ChatColor.GREEN}${ChatColor.BOLD}$memberName"
                } else {
                    "${ChatColor.YELLOW}${ChatColor.BOLD}$memberName"
                }
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}Role: ${member.role.color}${member.role.rendered}")
            description.add("${ChatColor.GRAY}Last Played: ${ChatColor.GREEN}${TimeUtil.formatIntoDetailedString(((System.currentTimeMillis() - member.lastPlayed) / 1000.0).toInt())} ago")
            description.add("")

            val invitedBy = if (member.invitedBy != null) {
                Cubed.instance.uuidCache.name(member.invitedBy!!)
            } else {
                "Unknown"
            }

            description.add("${ChatColor.GRAY}Invited By: ${ChatColor.YELLOW}$invitedBy")

            val invitedAt = if (member.invitedAt != null) {
                TimeUtil.formatIntoDateString(Date.from(Instant.ofEpochMilli(member.invitedAt!!)))
            } else {
                "N/A"
            }

            description.add("${ChatColor.GRAY}Invited At: ${ChatColor.YELLOW}$invitedAt")
            description.add("")
            description.add("${ChatColor.GRAY}Trophies Collected: ${ChatColor.GOLD}${NumberUtils.format(member.trophiesCollected)}")

            if (gang.isLeader(player.uniqueId)) {
                description.add("")

                if (member.role != GangMember.Role.CO_LEADER) {
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to promote member")
                }

                if (member.role != GangMember.Role.MEMBER) {
                    description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to demote member")
                }

                description.add("${ChatColor.DARK_RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.DARK_RED}to kick member")
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
            if (clickType.isLeftClick) {
                if (!gang.isLeader(player.uniqueId)) {
                    player.sendMessage("${ChatColor.RED}You must be the leader of the gang to promote members.")
                    return
                }

                if (player.uniqueId == member.uuid) {
                    player.sendMessage("${ChatColor.RED}You can't promote yourself!")
                    return
                }

                if (member.role.isAtLeast(GangMember.Role.CO_LEADER)) {
                    player.sendMessage("${ChatColor.RED}That member can't be promoted any higher.")
                    return
                }

                member.role = GangMember.Role.values()[member.role.ordinal + 1]
                gang.sendMessagesToMembers("${ChatColor.YELLOW}${member.getUsername()} has been promoted to ${member.role.rendered} by ${player.name}!")
            } else if (clickType.isRightClick) {
                if (clickType.isShiftClick) {
                    if (!gang.isLeader(player.uniqueId)) {
                        player.sendMessage("${ChatColor.RED}You must be the leader of the gang to kick members.")
                        return
                    }

                    if (player.uniqueId == member.uuid) {
                        player.sendMessage("${ChatColor.RED}You can't kick yourself from the gang.")
                        return
                    }

                    if (gang.isLeader(member.uuid)) {
                        player.sendMessage("${ChatColor.RED}You can't kick the leader from their own gang.")
                        return
                    }

                    ConfirmMenu("Are you sure?") { confirmed ->
                        if (confirmed) {
                            gang.kickMember(member.uuid)

                            val memberName = Cubed.instance.uuidCache.name(member.uuid)
                            player.sendMessage("${ChatColor.GREEN}Successfully kicked $memberName from the cell.")
                        } else {
                            player.sendMessage("${ChatColor.YELLOW}No changes made to members.")
                        }
                    }.openMenu(player)
                } else {
                    if (!gang.isLeader(player.uniqueId)) {
                        player.sendMessage("${ChatColor.RED}You must be the leader of the gang to demote members.")
                        return
                    }

                    if (player.uniqueId == member.uuid) {
                        player.sendMessage("${ChatColor.RED}You can't demote yourself!")
                        return
                    }

                    if (member.role == GangMember.Role.MEMBER) {
                        player.sendMessage("${ChatColor.RED}That member can't be demoted any lower.")
                        return
                    }

                    member.role = GangMember.Role.values()[member.role.ordinal - 1]
                    gang.sendMessagesToMembers("${ChatColor.YELLOW}${member.getUsername()} has been demoted to ${member.role.rendered} by ${player.name}!")
                }
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)
            val meta = item.itemMeta as SkullMeta
            meta.owner = Cubed.instance.uuidCache.name(member.uuid)
            item.itemMeta = meta
            return item
        }
    }

}