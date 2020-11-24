/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.upgrade.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.BackpackUpgrade
import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.impl.CapacityUpgrade
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class BackpackUpgradesMenu(private val backpack: Backpack) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Backpack Upgrades"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0 until 9) {
            buttons[i] = GlassButton(7)
        }

        buttons[1] = UpgradeButton(CapacityUpgrade)

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                backpack.open(player)
            }
        }
    }

    private inner class UpgradeButton(private val upgrade: BackpackUpgrade) : Button() {
        override fun getName(player: Player): String {
            val upgradeName = "${upgrade.getChatColor()}${ChatColor.BOLD}${upgrade.getName()}"

            val currentLevel = backpack.getUpgradeLevel(upgrade)
            return if (currentLevel >= upgrade.getMaxLevel()) {
                "$upgradeName ${ChatColor.GRAY}(Lvl ${NumberUtils.format(currentLevel)})"
            } else {
                "$upgradeName ${ChatColor.GRAY}(Lvl ${NumberUtils.format(currentLevel)} -> ${NumberUtils.format(currentLevel + 1)})"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val user = UserHandler.getUser(player.uniqueId)
            val currentLevel = backpack.getUpgradeLevel(upgrade)
            val nextLevel = currentLevel + 1

            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = upgrade.getDescription(), linePrefix = ChatColor.GRAY.toString()))
            description.add("")
            description.add("${ChatColor.GRAY}Max Level: ${upgrade.getChatColor()}${NumberUtils.format(upgrade.getMaxLevel())}")

            val isMaxed = currentLevel >= upgrade.getMaxLevel()
            if (isMaxed) {
                description.add("")
                description.add("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Maxed")
            } else {
                description.add("${ChatColor.GRAY}Price: ${Formats.formatTokens(upgrade.getCost(nextLevel))}")
                description.add("")

                if (user.hasTokenBalance(upgrade.getCost(nextLevel))) {
                    description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Click to purchase one level")
                    description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Press ${ChatColor.AQUA}${ChatColor.BOLD}Q ${ChatColor.YELLOW}${ChatColor.BOLD}to buy max")
                } else {
                    if (!isMaxed) {
                        description.add("${ChatColor.RED}${ChatColor.BOLD}Can't Afford")
                        description.add("${ChatColor.RED}You don't have enough tokens")
                        description.add("${ChatColor.RED}to purchase anymore levels.")
                    }
                }
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return upgrade.getIcon().type
        }

        override fun getDamageValue(player: Player): Byte {
            return upgrade.getIcon().durability.toByte()
        }

        override fun getButtonItem(player: Player): ItemStack {
            return super.getButtonItem(player).also {
                val currentLevel = backpack.getUpgradeLevel(upgrade)
                if (currentLevel >= upgrade.getMaxLevel()) {
                    GlowEnchantment.addGlow(it)
                }
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val currentLevel = backpack.getUpgradeLevel(upgrade)
            val nextLevel = currentLevel + 1

            if (currentLevel >= upgrade.getMaxLevel()) {
                player.sendMessage("${BackpackHandler.CHAT_PREFIX}${ChatColor.RED}Your backpack already has the max level for the ${ChatColor.BOLD}${upgrade.getName()} ${ChatColor.RED}upgrade.")
                return
            }

            val user = UserHandler.getUser(player.uniqueId)
            if (clickType == ClickType.LEFT || clickType == ClickType.DROP) {
                if (!user.hasTokenBalance(upgrade.getCost(nextLevel))) {
                    player.sendMessage("${BackpackHandler.CHAT_PREFIX}${ChatColor.RED}You don't have enough tokens to purchase the ${ChatColor.BOLD}${upgrade.getName()} ${ChatColor.RED}upgrade.")
                    return
                }
            }

            if (clickType == ClickType.LEFT) {
                val cost = upgrade.getCost(nextLevel)
                user.subtractTokensBalance(cost)

                backpack.addUpgradeLevel(upgrade)

                player.sendMessage("${BackpackHandler.CHAT_PREFIX}You purchased ${upgrade.getChatColor()}${ChatColor.BOLD}1 ${upgrade.getName()} ${ChatColor.GRAY}level for ${Formats.formatTokens(cost)}${ChatColor.GRAY}.")
            } else if (clickType == ClickType.DROP) {
                var levelsPurchased = 0
                var levelsCost = 0L

                for (level in currentLevel + 1 until upgrade.getMaxLevel() + 1) {
                    val cost = upgrade.getCost(level)
                    if (!user.hasTokenBalance(cost)) {
                        break
                    }

                    user.subtractTokensBalance(cost)
                    backpack.addUpgradeLevel(upgrade)

                    levelsPurchased++
                    levelsCost += cost
                }

                if (levelsPurchased == 0) {
                    player.sendMessage("${BackpackHandler.CHAT_PREFIX}${ChatColor.RED}You couldn't afford to purchase any levels.")
                    return
                }

                player.sendMessage("${BackpackHandler.CHAT_PREFIX}You purchased ${upgrade.getChatColor()}${ChatColor.BOLD}${NumberUtils.format(levelsPurchased)} ${upgrade.getName()} ${ChatColor.GRAY}${TextUtil.pluralize(levelsPurchased, "level", "levels")} for ${Formats.formatTokens(levelsCost)}${ChatColor.GRAY}.")
            }
        }
    }

}