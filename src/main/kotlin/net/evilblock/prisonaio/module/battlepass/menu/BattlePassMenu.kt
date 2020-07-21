/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.menu

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.battlepass.challenge.menu.BrowseChallengesMenu
import net.evilblock.prisonaio.module.battlepass.tier.Tier
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.text.NumberFormat

class BattlePassMenu(private val user: User) : Menu() {

    init {
        updateAfterClick = true
    }

    var page: Int = 1

    override fun getTitle(player: Player): String {
        return "${ChatColor.GOLD}${ChatColor.BOLD}JunkiePass"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        val range = if (page == 1) {
            1..9
        } else {
            (((page - 1) * 9) + 1)..(page * 9)
        }

        for (tier in TierHandler.getTiers().filter { it.number in range }) {
            var tierSlotRemainder = (tier.number % 9) - 1
            if (tierSlotRemainder == -1) {
                tierSlotRemainder = 8
            }

            val slot = TIER_TOP_SLOTS[tierSlotRemainder]

            if (tier.freeReward == null) {
                buttons[slot] = EmptyRewardButton(tier)
            } else {
                buttons[slot] = RewardButton(tier, tier.freeReward!!)
            }

            buttons[slot + 9] = TierStatusButton(tier)

            if (tier.premiumReward == null) {
                buttons[slot + 18] = EmptyRewardButton(tier)
            } else {
                buttons[slot + 18] = RewardButton(tier, tier.premiumReward!!)
            }
        }

        if (!user.battlePassData.isPremium()) {
            buttons[4] = PremiumAdvertisementButton()
        }

        buttons[36] = PreviousPageButton()
        buttons[44] = NextPageButton()

        buttons[47] = ChallengesMenuButton(true)
        buttons[49] = ClaimRewardsButton()
        buttons[51] = ChallengesMenuButton(false)

        for (i in 0..8) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
        }

        for (i in 36 until 54) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
            }
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class PreviousPageButton : TexturedHeadButton(texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==") {
        override fun getName(player: Player): String {
            return if (page > 1) {
                "${ChatColor.YELLOW}${ChatColor.BOLD}Previous page"
            } else {
                "${ChatColor.GRAY}${ChatColor.BOLD}No previous page"
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && page > 1) {
                page -= 1
                openMenu(player)
            }
        }
    }

    private inner class NextPageButton : TexturedHeadButton(texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19") {
        override fun getName(player: Player): String {
            return if (page < 6) {
                "${ChatColor.YELLOW}${ChatColor.BOLD}Next page"
            } else {
                "${ChatColor.GRAY}${ChatColor.BOLD}No next page"
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && page < 6) {
                page += 1
                openMenu(player)
            }
        }
    }

    private inner class TierStatusButton(private val tier: Tier) : Button() {
        override fun getName(player: Player): String {
            val nextTier = user.battlePassData.getNextTier()

            return when {
                user.battlePassData.isTierUnlocked(tier) -> {
                    "${ChatColor.GREEN}${ChatColor.BOLD}Unlocked"
                }
                nextTier == tier -> {
                    val formattedCurrentExp = NumberFormat.getInstance().format(user.battlePassData.getExperience())
                    val formattedNeededExp = NumberFormat.getInstance().format(nextTier.requiredExperience)
                    "${ChatColor.GRAY}$formattedCurrentExp${ChatColor.GOLD}/${ChatColor.GRAY}$formattedNeededExp ${ChatColor.GOLD}${Constants.EXP_SYMBOL}"
                }
                else -> {
                    "${ChatColor.RED}${ChatColor.BOLD}Locked"
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return when {
                user.battlePassData.isTierUnlocked(tier) -> {
                    13
                }
                user.battlePassData.getNextTier() == tier -> {
                    4
                }
                else -> {
                    14
                }
            }
        }
    }

    private inner class EmptyRewardButton(private val tier: Tier) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}Tier ${tier.number}"
        }

        override fun getMaterial(player: Player): Material {
            return Material.MINECART
        }
    }

    private inner class RewardButton(private val tier: Tier, private val reward: Reward) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}Tier ${tier.number}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            for (text in reward.textLines) {
                description.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} $text")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return if (user.battlePassData.isTierUnlocked(tier)) {
                if (!user.battlePassData.hasClaimedReward(reward)) {
                    Material.STORAGE_MINECART
                } else {
                    Material.MINECART
                }
            } else {
                Material.STORAGE_MINECART
            }
        }
    }

    private inner class PremiumAdvertisementButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Premium Advertisement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "The Premium JunkiePass gives you access to extra rewards and content through premium challenges. Quit missing out and purchase from our store.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GRAY}Visit our store at ${ChatColor.RED}store.minejunkie.com")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.EMERALD
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                FancyMessage("${ChatColor.GRAY}${ChatColor.BOLD}CLICK TO VISIT ${ChatColor.RED}${ChatColor.BOLD}STORE.MINEJUNKIE.COM${ChatColor.GRAY}${ChatColor.BOLD}!")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to visit our store"))
                    .link("store.minejunkie.com")
                    .send(player)
            }
        }
    }

    private inner class ChallengesMenuButton(private val daily: Boolean) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}${if (daily) "Daily" else "Premium"} Challenges"
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOOK
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                BrowseChallengesMenu(user, daily).openMenu(player)
            }
        }
    }

    private inner class ClaimRewardsButton : Button() {
        override fun getName(player: Player): String {
            return if (user.battlePassData.getUnclaimedRewards().isEmpty()) {
                "${ChatColor.GRAY}${ChatColor.BOLD}No Rewards to Claim"
            } else {
                "${ChatColor.YELLOW}${ChatColor.BOLD}Claim Rewards"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (user.battlePassData.getUnclaimedRewards().isEmpty()) {
                description.addAll(TextSplitter.split(length = 40, text = "You don't have any rewards to claim.", linePrefix = "${ChatColor.GRAY}"))
            } else {
                for (reward in user.battlePassData.getUnclaimedRewards()) {
                    for (text in reward.textLines) {
                        description.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} $text")
                    }
                }

                description.add("")
                description.add("${ChatColor.YELLOW}Click to claim your rewards!")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return if (user.battlePassData.getUnclaimedRewards().isEmpty()) {
                Material.MINECART
            } else {
                Material.STORAGE_MINECART
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && user.battlePassData.getUnclaimedRewards().isNotEmpty()) {
                for (reward in user.battlePassData.getUnclaimedRewards()) {
                    user.battlePassData.claimReward(player, reward)
                }
            }
        }
    }

    companion object {
        private val TIER_TOP_SLOTS = (9..17).toList()
    }

}