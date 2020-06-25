/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.profile.menu.tab

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.profile.ProfileComment
import net.evilblock.prisonaio.module.user.profile.menu.PaginatedProfileLayoutMenu
import net.evilblock.prisonaio.module.user.profile.menu.ProfileLayout
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.option.CommentsRestrictionOption
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.time.Instant
import java.util.*

class ProfileCommentsMenu(user: User) : PaginatedProfileLayoutMenu(layout = ProfileLayout(user = user, activeTab = ProfileLayout.ProfileMenuTab.COMMENTS)) {

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        if (!layout.user.hasPostedProfileComment(player.uniqueId)) {
            val allowingComments = (layout.user.getSettingOption(UserSetting.PROFILE_COMMENTS_RESTRICTION) as CommentsRestrictionOption).restriction == CommentsRestrictionOption.RestrictionOptionValue.ALLOWED
            if (allowingComments) {
                buttons[0] = AddCommentButton()
            } else {
                buttons[0] = NotAllowingCommentsButton()
            }
        }

        layout.user.getProfileComments().sortedByDescending { it.createdAt }.forEach {
            buttons[buttons.size] = CommentButton(it)
        }

        return buttons
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return LIST_SLOTS.size
    }

    override fun getAllPagesButtonSlots(): List<Int> {
        return LIST_SLOTS
    }

    override fun getPageButtonSlots(): Pair<Int, Int> {
        return Pair(10, 17)
    }

    private inner class AddCommentButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Post New Comment"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "Post a new comment to ${layout.user.getUsername()}'s profile.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "Posting unsolicited links or content deemed as harassment will result in punishment.", linePrefix = "${ChatColor.RED}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to post a comment")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                val allowingComments = (layout.user.getSettingOption(UserSetting.PROFILE_COMMENTS_RESTRICTION) as CommentsRestrictionOption).restriction == CommentsRestrictionOption.RestrictionOptionValue.ALLOWED
                if (allowingComments && !layout.user.hasPostedProfileComment(player.uniqueId)) {
                    EzPrompt.Builder()
                        .promptText("${ChatColor.GREEN}Please type the message you'd like to post on ${layout.user.getUsername()}'s profile. ${ChatColor.GRAY}(Limited to 120 characters)")
                        .acceptInput { player, input ->
                            layout.user.addProfileComment(ProfileComment(creator = player.uniqueId, message = input))
                            openMenu(player)
                        }
                        .charLimit(120)
                        .build()
                        .start(player)
                }
            }
        }
    }

    private inner class NotAllowingCommentsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GRAY}${ChatColor.BOLD}Comments Disabled"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "${layout.user.getUsername()} has disabled other players from leaving comments on their profile.", linePrefix = "${ChatColor.GRAY}"))
            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.BARRIER
        }
    }

    private inner class CommentButton(private val comment: ProfileComment) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${Cubed.instance.uuidCache.name(comment.creator)} - ${TimeUtil.formatIntoDateString(Date.from(Instant.ofEpochMilli(comment.createdAt)))}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.addAll(TextSplitter.split(length = 40, text = comment.message, linePrefix = "${ChatColor.GRAY}"))

            if (comment.creator == player.uniqueId) {
                description.add("")
                description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to remove your comment")
            } else if (layout.user.uuid == player.uniqueId) {
                description.add("")
                description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to remove ${Cubed.instance.uuidCache.name(comment.creator)}'s comment")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.EMPTY_MAP
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isRightClick) {
                if (comment.creator == player.uniqueId || layout.user.uuid == player.uniqueId) {
                    ConfirmMenu("Remove comment?") { confirmed ->
                        if (confirmed) {
                            layout.user.removeProfileComment(comment)
                        }

                        openMenu(player)
                    }.openMenu(player)
                }
            }
        }
    }

    companion object {
        private val LIST_SLOTS = arrayListOf(
            20, 21, 22, 23, 24, 25,
            29, 30, 31, 32, 33, 34,
            38, 39, 40, 41, 42, 43
        )
    }

}