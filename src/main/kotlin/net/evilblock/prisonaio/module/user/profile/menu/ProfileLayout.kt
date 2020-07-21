/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.profile.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.SkullButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.profile.menu.tab.ProfileCommentsMenu
import net.evilblock.prisonaio.module.user.profile.menu.tab.ProfileStatisticsMenu
import net.evilblock.prisonaio.module.user.setting.menu.UserSettingsMenu
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class ProfileLayout(
    val user: User,
    val activeTab: ProfileMenuTab
) {

    fun renderTitle(player: Player): String {
        return if (user.uuid == player.uniqueId) {
            "Your Profile"
        } else {
            "${user.getUsername()}'s Profile"
        }
    }

    fun renderLayout(player: Player): MutableMap<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in GREY_SLOTS) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
        }

        for (i in RED_SLOTS) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 14)
        }

        buttons[0] = HeadButton()
        buttons[2] = StatisticsButton()
        buttons[4] = CommentsButton()

        if (player.uniqueId == user.uuid) {
            buttons[6] = SettingsButton()
        }

        return buttons
    }

    private inner class HeadButton : SkullButton(owner = user.uuid) {
        override fun getName(player: Player): String {
            val status = if (Bukkit.getPlayer(user.uuid) != null) {
                "${ChatColor.GREEN}${ChatColor.BOLD}${Constants.DOT_SYMBOL} ONLINE"
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}${Constants.DOT_SYMBOL} OFFLINE"
            }

            val text = if (user.uuid == player.uniqueId) {
                "${ChatColor.RED}${ChatColor.BOLD}Your Profile"
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}${user.getUsername()}'s Profile"
            }

            return "$text ${ChatColor.GRAY}($status${ChatColor.GRAY})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.RED}${Constants.CROSSED_SWORDS_SYMBOL} ${ChatColor.GRAY}Rank ${user.getRank().displayName}")

            if (user.getPrestige() == 0) {
                description.add("${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Not Prestiged")
            } else {
                description.add("${ChatColor.RED}${Constants.PRESTIGE_SYMBOL} ${ChatColor.GRAY}Prestige ${user.getPrestige()}")
            }

            val formattedMoneyBalance = NumberUtils.format(user.getMoneyBalance())
            description.add("${ChatColor.RED}${Constants.MONEY_SYMBOL} ${ChatColor.GRAY}$formattedMoneyBalance")

            val formattedTokensBalance = NumberUtils.format(user.getTokensBalance())
            description.add("${ChatColor.RED}${Constants.TOKENS_SYMBOL} ${ChatColor.GRAY}$formattedTokensBalance")

            return description
        }
    }

    private inner class StatisticsButton : TexturedHeadButton(texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBlMGE0ZDQ4Y2Q4MjlhNmQ1ODY4OTA5ZDY0M2ZhNGFmZmQzOWU4YWU2Y2FhZjZlYzc5NjA5Y2Y3NjQ5YjFjIn19fQ==") {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Statistics"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(
                TextSplitter.split(
                length = 36,
                text = "View ${user.getUsername()}'s game statistics.",
                linePrefix = "${ChatColor.GRAY}"
            ))

            description.add("")
            description.add("${ChatColor.YELLOW}Click to view ${user.getUsername()}'s statistics")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ProfileStatisticsMenu(user).openMenu(player)
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)

            if (activeTab == ProfileMenuTab.STATISTICS) {
                GlowEnchantment.addGlow(item)
            }

            return item
        }
    }

    private inner class CommentsButton : TexturedHeadButton(texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzM2ZmViZWNhN2M0ODhhNjY3MWRjMDcxNjU1ZGRlMmExYjY1YzNjY2IyMGI2ZThlYWY5YmZiMDhlNjRiODAifX19") {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Comments"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(
                TextSplitter.split(
                length = 40,
                text = "View comments left on ${user.getUsername()}'s profile, or leave your own.",
                linePrefix = "${ChatColor.GRAY}"
            ))

            description.add("")
            description.add("${ChatColor.YELLOW}Click to view ${user.getUsername()}'s profile comments")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ProfileCommentsMenu(user).openMenu(player)
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)

            if (activeTab == ProfileMenuTab.COMMENTS) {
                GlowEnchantment.addGlow(item)
            }

            return item
        }
    }

    private inner class SettingsButton : TexturedHeadButton(texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWMyZmYyNDRkZmM5ZGQzYTJjZWY2MzExMmU3NTAyZGM2MzY3YjBkMDIxMzI5NTAzNDdiMmI0NzlhNzIzNjZkZCJ9fX0=") {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Settings"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Manage your account settings and",
                "${ChatColor.GRAY}privacy options.",
                "",
                "${ChatColor.YELLOW}Click to manage your settings"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                UserSettingsMenu(user).openMenu(player)
            }
        }
    }

    enum class ProfileMenuTab {
        STATISTICS,
        COMMENTS
    }

    companion object {
        private val RED_SLOTS = listOf(1, 8, 9, 45)
        private val GREY_SLOTS = arrayListOf(9, 18, 27, 36, 45).also { list -> list.addAll(0..9) }
    }

}