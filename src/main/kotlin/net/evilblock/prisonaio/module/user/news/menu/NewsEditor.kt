/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.news.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.user.news.News
import net.evilblock.prisonaio.module.user.news.NewsHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class NewsEditor : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "News Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = AddNewsPostButton()

        for (i in 9..17) {
            buttons[i] = GlassButton(0)
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (post in NewsHandler.getAllNews()) {
            buttons[buttons.size] = NewsPostButton(post)
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

    private inner class AddNewsPostButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Post"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(linePrefix = ChatColor.GRAY.toString(), text = "Create a new post by following the setup procedure."))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new post")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText("${ChatColor.GREEN}Please input a title for the post.")
                    .acceptInput { _, input ->
                        val news = News(title = ChatColor.translateAlternateColorCodes('&', input), createdBy = player.uniqueId)
                        NewsHandler.trackNews(news)

                        Tasks.async {
                            NewsHandler.saveData()
                        }

                        EditNewsMenu(news).openMenu(player)
                    }
                    .charLimit(128)
                    .build()
                    .start(player)
            }
        }
    }

    private inner class NewsPostButton(private val post: News) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RESET}${post.title}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")

            for (line in post.lines) {
                description.add(line)
            }

            if (player.hasPermission(Permissions.NEWS_VIEW_STATS)) {
                description.add("")
                description.add("${ChatColor.GRAY}(Read ${NumberUtils.format(post.reads)} times)")
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit post")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete post")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return post.icon.type
        }

        override fun getDamageValue(player: Player): Byte {
            return post.icon.durability.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditNewsMenu(post).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        NewsHandler.forgetNews(post)

                        Tasks.async {
                            NewsHandler.saveData()
                        }
                    }

                    this@NewsEditor.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}