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
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.PlayerPrompt
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangMember
import net.evilblock.prisonaio.module.gang.permission.GangPermission
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
        return "Gang Members"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[0] = InvitePlayerButton()

            for (i in 9..17) {
                buttons[i] = GlassButton(0)
            }
        }
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
            Tasks.delayed(1L) {
                GangMenu(gang).openMenu(player)
            }
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

        override fun clicked(sender: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!gang.testPermission(sender, GangPermission.INVITE_MEMBERS)) {
                return
            }

            sender.closeInventory()

            PlayerPrompt { invitedPlayer ->
                if (!gang.testPermission(sender, GangPermission.INVITE_MEMBERS)) {
                    return@PlayerPrompt
                }

                if (gang.isMember(invitedPlayer)) {
                    sender.sendMessage("${ChatColor.RED}That player is already a member of the gang!")
                    return@PlayerPrompt
                }

                if (gang.isInvited(invitedPlayer)) {
                    sender.sendMessage("${ChatColor.RED}That player has already been invited to the gang!")
                    return@PlayerPrompt
                }

                if (gang.isPastMember(invitedPlayer)) {
                    if (gang.getForceInvites() <= 0) {
                        sender.sendMessage("${ChatColor.RED}You must use a force-invite on that player, as they were once a member of your gang!")
                        sender.sendMessage("${ChatColor.RED}You have no progress force-invites!")
                    } else {
                        ConfirmMenu("Use Force Invite?") { confirmed ->
                            if (confirmed) {
                                if (!gang.testPermission(sender, GangPermission.INVITE_MEMBERS)) {
                                    return@ConfirmMenu
                                }

                                if (gang.isMember(invitedPlayer)) {
                                    sender.sendMessage("${ChatColor.RED}That player is already a member of the gang!")
                                    return@ConfirmMenu
                                }

                                if (gang.isInvited(invitedPlayer)) {
                                    sender.sendMessage("${ChatColor.RED}That player has already been invited to the gang!")
                                    return@ConfirmMenu
                                }

                                if (gang.getForceInvites() <= 0) {
                                    sender.sendMessage("${ChatColor.RED}You have no progress force-invites!")
                                    return@ConfirmMenu
                                }

                                gang.useForceInvite()
                                gang.invitePlayer(invitedPlayer, sender.uniqueId)

                                val playerInvitedName = Cubed.instance.uuidCache.name(invitedPlayer)
                                sender.sendMessage("${ChatColor.GREEN}You've invited $playerInvitedName to the gang!")
                            }
                        }.openMenu(sender)
                    }
                } else {
                    gang.invitePlayer(invitedPlayer, sender.uniqueId)

                    val playerInvitedName = Cubed.instance.uuidCache.name(invitedPlayer)
                    sender.sendMessage("${ChatColor.GREEN}You've invited $playerInvitedName to the gang!")
                }
            }.start(sender)
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
                if (member.role.isAtLeast(GangMember.Role.CO_LEADER)) {
                    player.sendMessage("${ChatColor.RED}That member can't be promoted any higher.")
                    return
                }

                val promotedBy = gang.getMemberInfo(player.uniqueId)!!
                val nextRole = GangMember.Role.values()[member.role.ordinal + 1]

                if (nextRole.isAtLeast(promotedBy.role)) {
                    player.sendMessage("${ChatColor.RED}You can't promote that member!")
                    return
                }

                if (player.uniqueId == member.uuid) {
                    player.sendMessage("${ChatColor.RED}You can't promote yourself!")
                    return
                }

                member.role = nextRole
                gang.sendMessagesToMembers("${member.role.color}${member.getUsername()} ${ChatColor.YELLOW}has been promoted to ${member.role.color}${member.role.rendered} ${ChatColor.YELLOW}by ${promotedBy.role.color}${player.name}${ChatColor.YELLOW}!")
            } else if (clickType.isRightClick) {
                if (clickType.isShiftClick) {
                    if (gang.getMemberInfo(player.uniqueId)?.role?.isAtLeast(GangMember.Role.CO_LEADER) == false) {
                        player.sendMessage("${ChatColor.RED}You must be at least a co-leader to kick members from the gang.")
                        return
                    }

                    if (player.uniqueId == member.uuid) {
                        player.sendMessage("${ChatColor.RED}You can't kick yourself from your gang.")
                        return
                    }

                    if (member.role.isAtLeast(gang.getMemberInfo(player.uniqueId)!!.role)) {
                        player.sendMessage("${ChatColor.RED}You can't kick a member that is the same role as you.")
                        return
                    }

                    if (gang.isLeader(member.uuid)) {
                        player.sendMessage("${ChatColor.RED}You can't kick the leader of your gang!")
                        return
                    }

                    ConfirmMenu { confirmed ->
                        if (confirmed) {
                            gang.kickMember(member.uuid)

                            val memberName = Cubed.instance.uuidCache.name(member.uuid)
                            player.sendMessage("${ChatColor.GREEN}Successfully kicked $memberName from the cell.")
                        } else {
                            player.sendMessage("${ChatColor.YELLOW}No changes made to members.")
                        }
                    }.openMenu(player)
                } else {
                    if (member.role == GangMember.Role.MEMBER) {
                        player.sendMessage("${ChatColor.RED}That member can't be demoted any lower.")
                        return
                    }

                    val demotedBy = gang.getMemberInfo(player.uniqueId)!!

                    val previousRole = GangMember.Role.values()[member.role.ordinal - 1]
                    if (previousRole.isAtLeast(demotedBy.role)) {
                        player.sendMessage("${ChatColor.RED}You can't demote that player!")
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
                    gang.sendMessagesToMembers("${member.role.color}${member.getUsername()} ${ChatColor.YELLOW}has been demoted to ${member.role.color}${member.role.rendered} ${ChatColor.YELLOW}by ${demotedBy.role.color}${player.name}${ChatColor.YELLOW}!")
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