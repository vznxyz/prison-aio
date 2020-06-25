/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.tier.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.battlepass.tier.Tier
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward
import net.evilblock.prisonaio.module.battlepass.tier.reward.menu.EditRewardMenu
import net.evilblock.prisonaio.util.Constants
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditTierMenu(private val tier: Tier) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "BattlePass - Edit Tier ${tier.number}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[1] = EditRequiredExperienceButton()
        buttons[3] = EditRewardButton(true)
        buttons[5] = EditRewardButton(false)

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                TierEditorMenu().openMenu(player)
            }
        }
    }

    private inner class EditRequiredExperienceButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Required Experience"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Current Requirement: ${ChatColor.GREEN}${tier.requiredExperience} XP")
            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "The amount of experience required to unlock this tier's challenges.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit required experience")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.EXP_BOTTLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt { numberInput ->
                    assert(numberInput > 0) { "The number must be more than 0." }
                    tier.requiredExperience = numberInput
                    TierHandler.saveData()

                    openMenu(player)
                }.start(player)
            }
        }
    }

    private inner class EditRewardButton(private val free: Boolean) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit ${if (free) "Free" else "Premium"} Reward"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            if (free) {
                description.addAll(TextSplitter.split(length = 40, text = "A free reward can be completed by any user.", linePrefix = "${ChatColor.GRAY}"))
            } else {
                description.addAll(TextSplitter.split(length = 40, text = "A premium reward can only be completed by users that have purchased the Premium JunkiePass.", linePrefix = "${ChatColor.GRAY}"))
            }

            description.add("")

            val reward: Reward? = if (free) {
                tier.freeReward
            } else {
                tier.premiumReward
            }

            val paidContext = if (free) "free" else "premium"

            if (reward == null) {
                description.addAll(TextSplitter.split(length = 40, text = "There is currently no $paidContext reward applied to this tier.", linePrefix = "${ChatColor.RED}"))
            } else {
                description.addAll(TextSplitter.split(length = 40, text = "There is currently a $paidContext reward applied to this tier.", linePrefix = "${ChatColor.GREEN}"))
                description.add("")

                for (line in reward.getTextLines()) {
                    description.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} $line")
                }
            }

            description.add("")

            if (reward == null) {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to add reward")
            } else {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.AQUA}to edit reward")
                description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to unset reward")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return if (free) {
                Material.COAL
            } else {
                Material.EMERALD
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (free) {
                    if (tier.freeReward == null) {
                        tier.freeReward = Reward(tier)
                        TierHandler.saveData()
                    }

                    EditRewardMenu(tier.freeReward!!).openMenu(player)
                } else {
                    if (tier.premiumReward == null) {
                        tier.premiumReward = Reward(tier)
                        TierHandler.saveData()
                    }

                    EditRewardMenu(tier.premiumReward!!).openMenu(player)
                }
            }

            if (clickType.isRightClick) {
                if ((free && tier.freeReward != null) || (!free && tier.premiumReward != null)) {
                    ConfirmMenu("Are you sure?") { confirmed ->
                        if (confirmed ) {
                            if (free) {
                                tier.freeReward = null
                            } else {
                                tier.premiumReward = null
                            }

                            TierHandler.saveData()
                        } else {
                            player.sendMessage("${ChatColor.YELLOW}No changes made.")
                        }

                        openMenu(player)
                    }.openMenu(player)
                }
            }
        }
    }

}