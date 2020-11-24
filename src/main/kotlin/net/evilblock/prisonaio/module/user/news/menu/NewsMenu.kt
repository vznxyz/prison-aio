/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.news.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.news.News
import net.evilblock.prisonaio.module.user.news.NewsHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Instant
import java.util.*

class NewsMenu : PaginatedMenu() {

    companion object {
        private val RED_SLOTS = arrayListOf(0, 8, 18, 26, 36, 44)

        private val POSTS_SLOTS = arrayListOf<Int>().also {
            it.addAll(10..16)
            it.addAll(19..25)
            it.addAll(28..34)
        }
    }

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "News / Announcements"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        val latestPost = NewsHandler.getLatestNews()
        if (latestPost != null) {
            val user = UserHandler.getUser(player.uniqueId)
            if (!user.hasReadNewsPost(latestPost)) {
                user.markNewsPostAsRead(latestPost)
                latestPost.reads++
            }

            buttons[4] = NewsPostButton(latestPost, true)
        }

        for (i in RED_SLOTS) {
            buttons[i] = GlassButton(14)
        }

        for (i in 0..44) {
            if (i != 4 && !RED_SLOTS.contains(i) && !POSTS_SLOTS.contains(i)) {
                buttons[i] = GlassButton(7)
            }
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        val user = UserHandler.getUser(player.uniqueId)
        for (post in NewsHandler.getPublicNews()) {
            if (!user.hasReadNewsPost(post)) {
                user.markNewsPostAsRead(post)
                post.reads++
            }

            buttons[buttons.size] = NewsPostButton(post, false)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 21
    }

    override fun getAllPagesButtonSlots(): List<Int> {
        return POSTS_SLOTS
    }

    private inner class NewsPostButton(private val post: News, private val latest: Boolean) : Button() {
        override fun getName(player: Player): String {
            return post.title + if (latest) " ${ChatColor.GRAY}(${ChatColor.YELLOW}${ChatColor.BOLD}LATEST${ChatColor.GRAY})" else ""
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("${ChatColor.GRAY}Posted by ${Cubed.instance.uuidCache.name(post.createdBy)} on ${TimeUtil.formatIntoCalendarString(Date.from(Instant.ofEpochMilli(post.createdAt)))}")
            description.add("")

            for (line in post.lines) {
                description.add(line)
            }

            if (player.hasPermission(Permissions.NEWS_VIEW_STATS)) {
                description.add("")
                description.add("${ChatColor.GRAY}(Read ${NumberUtils.format(post.reads)} times)")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return post.icon.type
        }

        override fun getDamageValue(player: Player): Byte {
            return post.icon.durability.toByte()
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)
            if (latest) {
                GlowEnchantment.addGlow(item)
            }
            return item
        }
    }

}