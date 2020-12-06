package net.evilblock.prisonaio.module.robot.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import net.evilblock.prisonaio.module.robot.impl.statistic.GraphicalTable
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class ManageRobotMenu(private val robot: MinerRobot) : Menu() {

    init {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Manage ${ChatColor.stripColor(robot.getTierName())}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[4] = YourRobotButton()
        buttons[20] = UpgradesButton()
        buttons[22] = CosmeticsButton()
        buttons[24] = PickupRobotButton()
        buttons[40] = CollectButton()

        for (i in 0..44) {
            if (!buttons.containsKey(i)) {
                buttons[i] = GlassButton(7)
            }
        }

        return buttons
    }

    private inner class YourRobotButton : Button() {
        override fun getName(player: Player): String {
            return robot.getTierName()
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val tierName = robot.getTierName()
                val tierColor = ChatColor.getLastColors(tierName)

                val tier = if (robot.tier == 0) {
                    "None"
                } else {
                    robot.tier.toString()
                }

                val uptimeSeconds = (robot.uptime / 1000.0).toInt()
                val moneyPerHour = (robot.getMoneyPerTick() * robot.getTicksPerSecond()) * TimeUnit.HOURS.toSeconds(1L)
                val tokensPerHour = (robot.getTokensPerTick() * robot.getTicksPerSecond()) * TimeUnit.HOURS.toSeconds(1L)

                desc.add("${ChatColor.GRAY}Tier: $tierColor$tier")
                desc.add("${ChatColor.GRAY}Uptime: $tierColor${TimeUtil.formatIntoAbbreviatedString(uptimeSeconds)}")
                desc.add("")
                desc.add("${ChatColor.GRAY}Money/HR: ${Formats.formatMoney(moneyPerHour)}")
                desc.add("${ChatColor.GRAY}Tokens/HR: ${Formats.formatTokens(tokensPerHour.toLong())}")
                desc.add("")

                val table = GraphicalTable()
                    .addEntry(0, 0, "")
                    .addEntry(0, 1, "${ChatColor.GREEN}${ChatColor.BOLD}MONEY")
                    .addEntry(0, 2, "${ChatColor.YELLOW}${ChatColor.BOLD}TOKENS")

                    .addEntry(1, 0, "${ChatColor.GRAY}1H")
                    .addEntry(1, 1, Formats.formatMoney(robot.moneyEarnings.lastHour))
                    .addEntry(1, 2, Formats.formatTokens(robot.tokenEarnings.lastHour.toBigInteger()))

                    .addEntry(2, 0, "${ChatColor.GRAY}24H")
                    .addEntry(2, 1, Formats.formatMoney(robot.moneyEarnings.lastDay))
                    .addEntry(2, 2, Formats.formatTokens(robot.tokenEarnings.lastDay.toBigInteger()))

                    .addEntry(3, 0, "${ChatColor.GRAY}7D")
                    .addEntry(3, 1, Formats.formatMoney(robot.moneyEarnings.lastWeek))
                    .addEntry(3, 2, Formats.formatTokens(robot.tokenEarnings.lastWeek.toBigInteger()))

                    .addEntry(4, 0, "${ChatColor.GRAY}ALL")
                    .addEntry(4, 1, Formats.formatMoney(robot.moneyEarnings.allTime))
                    .addEntry(4, 2, Formats.formatTokens(robot.tokenEarnings.allTime.toBigInteger()))

                    .title("${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}Revenue History")
                    .borders(true)
                    .render()

                desc.addAll(table)
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ARMOR_STAND
        }
    }

    private inner class UpgradesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}Upgrades"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Upgrades give your robot extra abilities to earn you more money faster."))
                desc.add("")

                if (robot.getAppliedUpgrades().isEmpty()) {
                    desc.add("${ChatColor.GRAY}This robot has no applied upgrades.")
                } else {
                    desc.add("${ChatColor.YELLOW}${ChatColor.BOLD}Applied Upgrades")

                    for (upgrade in robot.getAppliedUpgrades()) {
                        val upgradeLevel = robot.getUpgradeLevel(upgrade)
                        var text = "${ChatColor.GRAY}${upgrade.getColoredName()} ${ChatColor.GRAY}(Level ${NumberFormat.getInstance().format(upgradeLevel)})"

                        if (upgradeLevel >= upgrade.getMaxLevel()) {
                            text = "$text ${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}MAXED"
                        }

                        desc.add(text)
                    }
                }

                desc.add("")
                desc.add("${ChatColor.YELLOW}Click to manage upgrades")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_PICKAXE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ManageUpgradesMenu(robot).openMenu(player)
            }
        }
    }

    private inner class CosmeticsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Cosmetics"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val ownedCosmetics = CosmeticHandler.getRegisteredCosmetics().filter { CosmeticHandler.hasBeenGrantedCosmetic(player.uniqueId, it) }.size
                val availableCosmetics = CosmeticHandler.getRegisteredCosmetics().size

                desc.addAll(TextSplitter.split(text = "Cosmetics allow you to style your robot the way you want. They provide no game advantage."))
                desc.add("")
                desc.add("${ChatColor.GRAY}Purchase cosmetics on our store:")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}store.minejunkie.com")
                desc.add("")
                desc.add("${ChatColor.GRAY}You own ${ChatColor.GREEN}$ownedCosmetics${ChatColor.GRAY}/${ChatColor.BOLD}$availableCosmetics ${ChatColor.GRAY}cosmetics.")
                desc.add("")
                desc.add("${ChatColor.YELLOW}Click to manage cosmetics")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            ManageCosmeticsMenu(robot).openMenu(player)
        }
    }

    private inner class PickupRobotButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Pickup Robot"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("${ChatColor.GRAY}Pickup the robot to your inventory.")
                desc.add("")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}Warning!")
                desc.addAll(TextSplitter.split(text = "Robots lose all stats when they are picked up."))
                desc.add("")
                desc.add("${ChatColor.YELLOW}Click to pickup this robot")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.MONSTER_EGG
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemUtils.setMonsterEggType(super.getButtonItem(player), EntityType.VILLAGER)
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (player.uniqueId != robot.owner) {
                if ((player.gameMode != GameMode.CREATIVE || !RegionBypass.hasBypass(player))) {
                    player.sendMessage("${ChatColor.RED}You can't pickup a robot that doesn't belong to you!")
                    return
                }
            }

            ConfirmMenu { confirmed ->
                if (confirmed) {
                    val robotItemStack = RobotUtils.makeRobotItem(1, robot.tier)

                    if (player.inventory.firstEmpty() == -1) {
                        val existingSlot = player.inventory.first(robotItemStack)
                        if (existingSlot == -1 || player.inventory.getItem(existingSlot)?.amount ?: 0 < 64) {
                            player.sendMessage("${ChatColor.RED}You need a free slot in your inventory to pickup the robot.")
                            return@ConfirmMenu
                        }
                    }

                    if (!RobotHandler.getRobots().contains(robot)) {
                        player.closeInventory()
                        return@ConfirmMenu
                    }

                    RobotsModule.getPluginFramework().logger.info("${player.name} is picking up a Tier ${robot.tier} Robot from ${robot.location.world.name}, ${robot.location.x}, ${robot.location.y}, ${robot.location.z}")
                    RobotHandler.forgetRobot(robot)

                    robot.clearFakeBlock()
                    robot.destroyForCurrentWatchers()

                    player.inventory.addItem(robotItemStack)
                    player.sendMessage("${ChatColor.GREEN}The robot has been picked up to your inventory.")
                } else {
                    player.sendMessage("${ChatColor.YELLOW}No changes made to robot.")
                }
            }.openMenu(player)
        }
    }

    private inner class CollectButton : Button() {
        override fun getName(player: Player): String {
            return if (robot.moneyOwed > BigDecimal.ZERO || robot.tokensOwed > BigDecimal.ZERO) {
                "${ChatColor.YELLOW}${ChatColor.BOLD}Collect Earnings"
            } else {
                "${ChatColor.GREEN}${ChatColor.BOLD}All caught up!"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val owedMoney = robot.moneyOwed > BigDecimal.ZERO
                val owedTokens = robot.tokensOwed > BigDecimal.ZERO

                if (owedMoney && owedTokens) {
                    desc.addAll(TextSplitter.split(text = "You have ${Formats.formatMoney(robot.moneyOwed)} ${ChatColor.GRAY}and ${Formats.formatTokens(robot.tokensOwed.toBigInteger())} ${ChatColor.GRAY}waiting to be collected."))
                } else if (owedMoney) {
                    desc.addAll(TextSplitter.split(text = "You have ${Formats.formatMoney(robot.moneyOwed)} ${ChatColor.GRAY}waiting to be collected."))
                } else if (owedTokens) {
                    desc.addAll(TextSplitter.split(text = "You have ${Formats.formatTokens(robot.tokensOwed.toBigInteger())} ${ChatColor.GRAY}waiting to be collected."))
                } else {
                    desc.add("${ChatColor.GRAY}You don't have anything to collect.")
                }

                if (owedMoney || owedTokens) {
                    desc.add("")
                    desc.add("${ChatColor.YELLOW}Click to collect earnings")
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return if (robot.moneyOwed > BigDecimal.ZERO || robot.tokensOwed > BigDecimal.ZERO) {
                Material.STORAGE_MINECART
            } else {
                Material.MINECART
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                robot.collectEarnings(player)
            }
        }
    }

}