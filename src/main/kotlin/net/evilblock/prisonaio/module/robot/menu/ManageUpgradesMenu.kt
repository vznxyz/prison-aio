package net.evilblock.prisonaio.module.robot.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.economy.Currency
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import net.evilblock.prisonaio.module.robot.impl.upgrade.Upgrade
import net.evilblock.prisonaio.module.robot.impl.upgrade.impl.EfficiencyUpgrade
import net.evilblock.prisonaio.module.robot.impl.upgrade.impl.FortuneUpgrade
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ManageUpgradesMenu(private val robot: MinerRobot) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Robot Upgrades"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = UpgradeButton(EfficiencyUpgrade)
        buttons[6] = UpgradeButton(FortuneUpgrade)

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                ManageRobotMenu(robot).openMenu(player)
            }
        }
    }

    private inner class UpgradeButton(private val upgrade: Upgrade) : Button() {
        override fun getName(player: Player): String {
            val currentLevel = if (robot.hasUpgradeApplied(upgrade)) {
                robot.getUpgradeLevel(upgrade)
            } else {
                0
            }

            return if (currentLevel >= upgrade.getMaxLevel()) {
                upgrade.getColoredName() + ChatColor.GRAY + " (Lvl " + currentLevel + ")"
            } else {
                upgrade.getColoredName() + ChatColor.GRAY + " (Lvl " + currentLevel + " -> " + (currentLevel + 1) + ")"
            }
        }

        override fun getDescription(player: Player): List<String> {
            if (!UserHandler.isUserLoaded(player.uniqueId)) {
                return emptyList()
            }

            val description = arrayListOf<String>()
            description.addAll(upgrade.getDescription())
            description.add("")

            val currentLevel = if (robot.hasUpgradeApplied(upgrade)) {
                robot.getUpgradeLevel(upgrade)
            } else {
                0
            }

            val nextLevel = currentLevel + 1
            val currency = RobotsModule.getUpgradesCurrency()

            val nextLevelPrice = upgrade.getPrice(player, robot.tier, nextLevel)
            val formattedPrice = currency.format(nextLevelPrice)

            description.add("${ChatColor.GRAY}Level: ${upgrade.getColor()}${ChatColor.BOLD}${NumberUtils.format(currentLevel)}${ChatColor.GRAY}/${ChatColor.BOLD}${NumberUtils.format(upgrade.getMaxLevel())}")

            val isMaxed = currentLevel >= upgrade.getMaxLevel()
            if (!isMaxed) {
                description.add("${ChatColor.GRAY}Next Level Price: $formattedPrice")
            }

            description.add("")

            if (isMaxed) {
                description.add("${ChatColor.RED}${ChatColor.BOLD}Maximum level reached!")
            } else {
                val canAfford = currency.has(player.uniqueId, nextLevelPrice)
                if (canAfford) {
                    description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Click to purchase next level")
                    description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Press ${ChatColor.AQUA}${ChatColor.BOLD}Q ${ChatColor.YELLOW}${ChatColor.BOLD}to purchase max levels")
                } else {
                    if (currency == Currency.Type.MONEY) {
                        description.addAll(TextSplitter.split(text = "You don't have enough money to purchase any more levels!", linePrefix = ChatColor.RED.toString()))
                    } else {
                        description.addAll(TextSplitter.split(text = "You don't have enough tokens to purchase any more levels!", linePrefix = ChatColor.RED.toString()))
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

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!UserHandler.isUserLoaded(player.uniqueId)) {
                return
            }

            val currentLevel = robot.getUpgradeLevel(upgrade)

            val nextLevel = currentLevel + 1
            if (nextLevel > upgrade.getMaxLevel()) {
                player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You can't purchase anymore ${ChatColor.BOLD}${upgrade.getName()} ${ChatColor.RED}levels!")
                return
            }

            val nextLevelPrice = upgrade.getPrice(player, robot.tier, nextLevel)
            val currency = RobotsModule.getUpgradesCurrency()

            val canAfford = currency.has(player.uniqueId, nextLevelPrice)
            if (!canAfford) {
                if (currency == Currency.Type.MONEY) {
                    player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You don't have enough money to purchase anymore ${ChatColor.BOLD}${upgrade.getName()} ${ChatColor.RED}levels!")
                } else {
                    player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You don't have enough tokens to purchase anymore ${ChatColor.BOLD}${upgrade.getName()} ${ChatColor.RED}levels!")
                }

                return
            }

            if (clickType.isLeftClick) {
                val formattedPrice = currency.format(nextLevelPrice)

                currency.take(player.uniqueId, nextLevelPrice)

                if (nextLevel == 1) {
                    robot.applyUpgrade(upgrade)
                } else {
                    robot.setUpgradeLevel(upgrade, nextLevel)
                }

                player.sendMessage("${RobotsModule.CHAT_PREFIX}You purchased ${ChatColor.RED}${ChatColor.BOLD}1 ${ChatColor.RED}${upgrade.getName()} ${ChatColor.GRAY}level for $formattedPrice${ChatColor.GRAY}.")
            } else if (clickType == ClickType.DROP) {
                var levelsPurchased = 0
                var totalCost = 0.0

                val balance = currency.get(player.uniqueId)

                for (level in currentLevel + 1 .. upgrade.getMaxLevel()) {
                    if (totalCost + upgrade.getPrice(player, robot.tier, level) > balance.toLong()) {
                        break
                    } else {
                        levelsPurchased++
                        totalCost += upgrade.getPrice(player, robot.tier, level)
                    }
                }

                if (levelsPurchased == 0) {
                    if (currency == Currency.Type.MONEY) {
                        player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You don't have enough money to purchase anymore ${ChatColor.BOLD}${upgrade.getName()} ${ChatColor.RED}levels!")
                    } else {
                        player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You don't have enough tokens to purchase anymore ${ChatColor.BOLD}${upgrade.getName()} ${ChatColor.RED}levels!")
                    }

                    return
                }

                val formattedPrice = currency.format(totalCost)

                currency.take(player.uniqueId, totalCost)

                if (nextLevel == 1) {
                    robot.applyUpgrade(upgrade)
                }

                robot.setUpgradeLevel(upgrade, currentLevel + levelsPurchased)

                player.sendMessage("${RobotsModule.CHAT_PREFIX}You purchased ${ChatColor.RED}${ChatColor.BOLD}$levelsPurchased ${ChatColor.RED}${upgrade.getName()} ${ChatColor.GRAY}levels for $formattedPrice${ChatColor.GRAY}.")
            }
        }

    }

}