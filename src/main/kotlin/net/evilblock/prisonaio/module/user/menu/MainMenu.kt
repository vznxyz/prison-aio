/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.menu

import mkremins.fanciful.FancyMessage
import net.evilblock.cosmetics.menu.CategoriesMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.auction.menu.AuctionHouseMenu
import net.evilblock.prisonaio.module.battlepass.menu.BattlePassMenu
import net.evilblock.prisonaio.module.gang.menu.GangsMenu
import net.evilblock.prisonaio.module.leaderboard.menu.LeaderboardsMenu
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.profile.menu.tab.ProfileStatisticsMenu
import net.evilblock.prisonaio.module.user.setting.menu.UserSettingsMenu
import net.evilblock.prisonaio.module.warp.menu.WarpsMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

class MainMenu(private val user: User) : Menu() {

    companion object {
        private val BUTTON_SLOTS = arrayListOf<Int>().also {
            it.addAll(19..25)
            it.addAll(28..34)
        }
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.GRAY}${ChatColor.BOLD}MINE${ChatColor.RED}${ChatColor.BOLD}JUNKIE"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = CosmeticsButton()
            buttons[4] = InfoButton()

            var slots = 0
            buttons[BUTTON_SLOTS[slots++]] = ProfileButton()
            buttons[BUTTON_SLOTS[slots++]] = RankupsButton()
            buttons[BUTTON_SLOTS[slots++]] = PlotsButton()
            buttons[BUTTON_SLOTS[slots++]] = GangButton()
            buttons[BUTTON_SLOTS[slots++]] = WarpsButton()
            buttons[BUTTON_SLOTS[slots++]] = BattlePassButton()
            buttons[BUTTON_SLOTS[slots++]] = ShopsButton()
            buttons[BUTTON_SLOTS[slots++]] = AuctionHouseButton()
            buttons[BUTTON_SLOTS[slots]] = LeaderboardsButton()

            buttons[45] = SettingsButton()

            buttons[48] = DiscordButton()
            buttons[49] = StoreButton()
            buttons[50] = ForumsButton()
        }
    }

    private inner class CosmeticsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.BLUE}${ChatColor.BOLD}Cosmetics"
        }

        override fun getMaterial(player: Player): Material {
            return Material.TRIPWIRE_HOOK
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                CategoriesMenu().openMenu(player)
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GRAY}${ChatColor.BOLD}MINE${ChatColor.RED}${ChatColor.BOLD}JUNKIE"
        }

        override fun getMaterial(player: Player): Material {
            return Material.BED
        }
    }

    private inner class ProfileButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}My Profile"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "View your profile, which shows your statistics and comments other players have left."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to view profile"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            return (itemMeta as SkullMeta).also { it.owner = player.name }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ProfileStatisticsMenu(user).openMenu(player)
            }
        }
    }

    private inner class RankupsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Rankups"
        }

        override fun getMaterial(player: Player): Material {
            return Material.EXP_BOTTLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                UserRankupsMenu(user).openMenu(player)
            }
        }
    }

    private inner class PlotsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.DARK_GREEN}${ChatColor.BOLD}Plots"
        }

        override fun getMaterial(player: Player): Material {
            return Material.GRASS
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                PlotsMenu().openMenu(player)
            }
        }
    }

    private inner class GangButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Gangs"
        }

        override fun getMaterial(player: Player): Material {
            return Material.BEACON
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                GangsMenu().openMenu(player)
            }
        }
    }

    private inner class WarpsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Warps"
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENDER_PEARL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                WarpsMenu().openMenu(player)
            }
        }
    }

    private inner class BattlePassButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}JunkiePass"
        }

        override fun getMaterial(player: Player): Material {
            return Material.SADDLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                BattlePassMenu(UserHandler.getUser(player)).openMenu(player)
            }
        }
    }

    private inner class ShopsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.BLUE}${ChatColor.BOLD}Shops"
        }

        override fun getMaterial(player: Player): Material {
            return Material.EMERALD
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                val defaultShop = ShopHandler.getDefaultShop()
                if (defaultShop.isPresent) {
                    defaultShop.get().openMenu(player)
                } else {
                    player.sendMessage("${ChatColor.RED}No main shop has been set!")
                }
            }
        }
    }

    private inner class AuctionHouseButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Auction House"
        }

        override fun getMaterial(player: Player): Material {
            return Material.STORAGE_MINECART
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                AuctionHouseMenu().openMenu(player)
            }
        }
    }

    private inner class LeaderboardsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Leaderboards"
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                LeaderboardsMenu().openMenu(player)
            }
        }
    }

    private inner class SettingsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Settings"
        }

        override fun getMaterial(player: Player): Material {
            return Material.REDSTONE_COMPARATOR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                UserSettingsMenu(UserHandler.getUser(player)).openMenu(player)
            }
        }
    }

    private inner class DiscordButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.BLUE}${ChatColor.BOLD}Discord"
        }

        override fun getMaterial(player: Player): Material {
            return Material.CLAY_BALL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                FancyMessage("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}!${ChatColor.GRAY}] ")
                    .then("${ChatColor.RED}Click to visit ${ChatColor.AQUA}${ChatColor.BOLD}${ChatColor.UNDERLINE}https://www.discord.gg/minejunkie")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to join our Discord!"))
                    .link("https://www.discord.gg/minejunkie")
                    .send(player)
            }
        }
    }

    private inner class StoreButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.WHITE}${ChatColor.BOLD}Store"
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                FancyMessage("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}!${ChatColor.GRAY}] ")
                    .then("${ChatColor.RED}Click to visit ${ChatColor.AQUA}${ChatColor.BOLD}${ChatColor.UNDERLINE}https://store.minejunkie.com")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to visit our store!"))
                    .link("https://store.minejunkie.com")
                    .send(player)
            }
        }
    }

    private inner class ForumsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Forums"
        }

        override fun getMaterial(player: Player): Material {
            return Material.MAGMA_CREAM
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                FancyMessage("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}!${ChatColor.GRAY}] ")
                    .then("${ChatColor.RED}Click to visit ${ChatColor.AQUA}${ChatColor.BOLD}${ChatColor.UNDERLINE}https://www.minejunkie.com")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to visit our website!"))
                    .link("https://www.minejunkie.com")
                    .send(player)
            }
        }
    }

}