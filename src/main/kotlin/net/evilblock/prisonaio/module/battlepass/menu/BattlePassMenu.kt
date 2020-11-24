/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.battlepass.challenge.menu.BrowseChallengesMenu
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
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

    companion object {
        private val TIER_TOP_SLOTS = (27..35).toList()
    }

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

            buttons[slot] = TierButton(tier)

            if (tier.freeReward == null) {
                buttons[slot - 9] = EmptyRewardButton(tier)
            } else {
                buttons[slot - 9] = RewardButton(tier, tier.freeReward!!)
            }

            if (tier.premiumReward == null) {
                buttons[slot + 9] = EmptyRewardButton(tier)
            } else {
                buttons[slot + 9] = RewardButton(tier, tier.premiumReward!!)
            }
        }

        buttons[4] = InfoButton()

        buttons[45] = PreviousPageButton()
        buttons[53] = NextPageButton()

        buttons[2] = ChallengesMenuButton(true)
        buttons[6] = ChallengesMenuButton(false)

        for (i in 0 until 54) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
            }
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}BattlePass"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Current Tier: ${ChatColor.GOLD}${user.battlePassProgress.getCurrentTier()?.number ?: 0}")
            description.add("${ChatColor.GRAY}Current XP: ${ChatColor.GOLD}${NumberUtils.format(user.battlePassProgress.getExperience())}")

            if (!user.battlePassProgress.isPremium()) {
                description.add("")
                description.addAll(TextSplitter.split(length = 40, text = "The Premium JunkiePass gives you access to extra rewards and content through premium challenges. Quit missing out and purchase from our store at store.minejunkie.com.", linePrefix = "${ChatColor.RED}"))
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }
    }

    private inner class ChallengesMenuButton(private val daily: Boolean) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}${if (daily) "Daily" else "Premium"} Challenges"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (daily) {
                description.addAll(TextSplitter.split(text = "New daily challenges will be generated in ${DailyChallengeHandler.getSession().getTimeRemaining()}.", linePrefix = ChatColor.GRAY.toString()))
            }

            if (!daily && !user.battlePassProgress.isPremium()) {
                description.add("")
                description.addAll(TextSplitter.split(text = "You don't have access to the Premium JunkiePass challenges! Purchase on our store at store.minejunkie.com.", linePrefix = "${ChatColor.RED}"))
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOOK
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (!daily && !user.battlePassProgress.isPremium()) {
                    player.sendMessage("${ChatColor.RED}You don't have access to the Premium JunkiePass challenges! Purchase on our store at store.minejunkie.com.")
                    return
                }

                BrowseChallengesMenu(user, daily).openMenu(player)
            }
        }
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

    private inner class TierButton(private val tier: Tier) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}Tier ${tier.number}"
        }

        override fun getDescription(player: Player): List<String> {
            val formattedCurrentExp = NumberFormat.getInstance().format(user.battlePassProgress.getExperience().coerceAtMost(tier.requiredExperience))
            val formattedNeededExp = NumberFormat.getInstance().format(tier.requiredExperience)
            return listOf("${ChatColor.GOLD}${Constants.EXP_SYMBOL}${ChatColor.GRAY}$formattedCurrentExp${ChatColor.GOLD}/${ChatColor.GRAY}$formattedNeededExp")
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return when {
                user.battlePassProgress.isTierUnlocked(tier) -> {
                    5
                }
                user.battlePassProgress.getNextTier() == tier -> {
                    4
                }
                else -> {
                    14
                }
            }
        }
    }

    private inner class RewardButton(private val tier: Tier, private val reward: Reward) : Button() {
        override fun getName(player: Player): String {
            return if (user.battlePassProgress.isTierUnlocked(tier)) {
                if (user.battlePassProgress.hasClaimedReward(tier, reward)) {
                    "${ChatColor.GREEN}${ChatColor.BOLD}Claimed Reward"
                } else {
                    if (!reward.isFreeReward() && !user.battlePassProgress.isPremium()) {
                        "${ChatColor.RED}${ChatColor.BOLD}Reward Locked"
                    } else {
                        "${ChatColor.YELLOW}${ChatColor.BOLD}Claim Reward"
                    }
                }
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}Reward Locked"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            for (text in reward.textLines) {
                description.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} $text")
            }

            if (!reward.isFreeReward() && !user.battlePassProgress.isPremium()) {
                description.add("")
                description.addAll(TextSplitter.split(length = 34, linePrefix = "${ChatColor.RED}", text = "You don't have access to the Premium JunkiePass rewards! Purchase on our store at store.minejunkie.com."))
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return if (user.battlePassProgress.isTierUnlocked(tier)) {
                if (user.battlePassProgress.hasClaimedReward(tier, reward)) {
                    Material.MINECART
                } else {
                    Material.STORAGE_MINECART
                }
            } else {
                Material.HOPPER_MINECART
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (user.battlePassProgress.isTierUnlocked(tier) && !user.battlePassProgress.hasClaimedReward(tier, reward)) {
                if (!reward.isFreeReward() && !user.battlePassProgress.isPremium()) {
                    return
                }

                user.battlePassProgress.claimReward(player, tier, reward)
                reward.execute(player)
            }
        }
    }

    private inner class EmptyRewardButton(private val tier: Tier) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GRAY}${ChatColor.BOLD}No Reward"
        }

        override fun getMaterial(player: Player): Material {
            return Material.MINECART
        }
    }

}