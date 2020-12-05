package net.evilblock.prisonaio.module.robot.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.math.BigInteger
import java.text.NumberFormat

class ManageRobotMenu(private val robot: MinerRobot) : Menu() {

    init {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return if (robot.owner == player.uniqueId) {
            "Your Robot"
        } else {
            val ownerName = Cubed.instance.uuidCache.name(robot.owner)
            "$ownerName's Robot"
        }
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
                buttons[i] = GlassButton(15)
            }
        }

        return buttons
    }

    private inner class YourRobotButton : Button() {
        override fun getName(player: Player): String {
            val tierName = robot.getTierName()
            val tierColor = ChatColor.getLastColors(tierName)

            return if (robot.owner == player.uniqueId) {
                "$tierColor${ChatColor.BOLD}Your $tierName"
            } else {
                "$tierColor${ChatColor.BOLD}${robot.getOwnerName()}'s $tierName"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            val tierName = robot.getTierName()
            val tierColor = ChatColor.getLastColors(tierName)

            if (robot.tier > 0) {
                description.add("${ChatColor.GRAY}Tier: $tierColor${robot.tier}")
            }

            val uptimeMs = System.currentTimeMillis() - robot.getCreatedAt()
            val formattedUptime = TimeUtil.formatIntoDetailedString((uptimeMs / 1000).toInt())
            description.add("${ChatColor.GRAY}Uptime: $tierColor$formattedUptime")

            val moneyLastHour = if (robot.moneyEarnings.isHourViewComplete()) {
                robot.moneyEarnings.getLastHourRealTime() + BigInteger(robot.moneyEarnings.getEarnings().toString())
            } else {
                BigInteger(robot.moneyEarnings.getEarnings().toString())
            }

            val moneyLastDay = if (robot.moneyEarnings.isDayViewComplete()) {
                robot.moneyEarnings.getLastDayRealTime() + BigInteger(robot.moneyEarnings.getEarnings().toString())
            } else {
                BigInteger(robot.moneyEarnings.getEarnings().toString())
            }

            val moneyLastWeek = if (robot.moneyEarnings.isWeekViewComplete()) {
                robot.moneyEarnings.getLastWeekRealTime() + BigInteger(robot.moneyEarnings.getEarnings().toString())
            } else {
                BigInteger(robot.moneyEarnings.getEarnings().toString())
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}Money Earnings")
            description.add("${ChatColor.GRAY}Last Hour: ${Formats.formatMoney(moneyLastHour.toBigDecimal())}")
            description.add("${ChatColor.GRAY}Last 24 Hrs: ${Formats.formatMoney(moneyLastDay.toBigDecimal())}")
            description.add("${ChatColor.GRAY}Last 7 Days: ${Formats.formatMoney(moneyLastWeek.toBigDecimal())}")
            description.add("${ChatColor.GRAY}All Time: ${Formats.formatMoney(robot.moneyTotalEarnings.toDouble())}")

            val tokensLastHour = if (robot.tokenEarnings.isHourViewComplete()) {
                robot.tokenEarnings.getLastHourRealTime() + BigInteger(robot.tokenEarnings.getEarnings().toString())
            } else {
                BigInteger(robot.tokenEarnings.getEarnings().toString())
            }

            val tokensLastDay = if (robot.tokenEarnings.isDayViewComplete()) {
                robot.tokenEarnings.getLastDayRealTime() + BigInteger(robot.tokenEarnings.getEarnings().toString())
            } else {
                BigInteger(robot.tokenEarnings.getEarnings().toString())
            }

            val tokensLastWeek = if (robot.tokenEarnings.isWeekViewComplete()) {
                robot.tokenEarnings.getLastWeekRealTime() + BigInteger(robot.tokenEarnings.getEarnings().toString())
            } else {
                BigInteger(robot.tokenEarnings.getEarnings().toString())
            }

            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Token Earnings")
            description.add("${ChatColor.GRAY}Last Hour: ${Formats.formatTokens(tokensLastHour)}")
            description.add("${ChatColor.GRAY}Last 24 Hrs: ${Formats.formatTokens(tokensLastDay)}")
            description.add("${ChatColor.GRAY}Last 7 Days: ${Formats.formatTokens(tokensLastWeek)}")
            description.add("${ChatColor.GRAY}All Time: ${Formats.formatTokens(robot.tokensTotalEarnings)}")

            return description
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
            val description = arrayListOf<String>()
            description.add("${ChatColor.GRAY}Upgrades give your robot extra")
            description.add("${ChatColor.GRAY}abilities to earn you more money")
            description.add("${ChatColor.GRAY}faster.")
            description.add("")

            if (robot.getAppliedUpgrades().isEmpty()) {
                description.add("${ChatColor.GRAY}This robot has no applied upgrades.")
            } else {
                description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Applied Upgrades")

                for (upgrade in robot.getAppliedUpgrades()) {
                    val upgradeLevel = robot.getUpgradeLevel(upgrade)
                    var text = "${ChatColor.GRAY}${upgrade.getName()} (Lvl ${NumberFormat.getInstance().format(upgradeLevel)})"

                    if (upgradeLevel >= upgrade.getMaxLevel()) {
                        text = "$text ${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Maxed"
                    }

                    description.add(text)
                }
            }

            description.add("")
            description.add("${ChatColor.YELLOW}Click to manage the robot's upgrades.")

            return description
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
            val ownedCosmetics = CosmeticHandler.getRegisteredCosmetics().filter { CosmeticHandler.hasBeenGrantedCosmetic(player.uniqueId, it) }.size
            val availableCosmetics = CosmeticHandler.getRegisteredCosmetics().size

            val description = arrayListOf<String>()
            description.add("${ChatColor.GRAY}Style your robot the way you want.")
            description.add("")
            description.add("${ChatColor.GRAY}Cosmetics provide no game advantage")
            description.add("${ChatColor.GRAY}and can be purchased on our store")
            description.add("${ChatColor.GRAY}at ${ChatColor.RED}store.minejunkie.com${ChatColor.GRAY}.")
            description.add("")
            description.add("${ChatColor.GRAY}You own ${ChatColor.GREEN}$ownedCosmetics ${ChatColor.GRAY}cosmetics. ($availableCosmetics available)")
            description.add("")
            description.add("${ChatColor.YELLOW}Click to manage the robot's cosmetics.")
            return description
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
            return listOf(
                    "${ChatColor.GRAY}Pickup the robot to your inventory.",
                    "",
                    "${ChatColor.RED}${ChatColor.BOLD}Warning!",
                    "${ChatColor.GRAY}Robots lose all stats when they",
                    "${ChatColor.GRAY}are picked up.",
                    "",
                    "${ChatColor.YELLOW}Click to pickup the robot."
            )
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

            ConfirmMenu("Are you sure?") { confirmed ->
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
            return if (robot.moneyOwed > 0 || robot.tokensOwed > 0) {
                "${ChatColor.YELLOW}${ChatColor.BOLD}Collect Earnings"
            } else {
                "${ChatColor.GREEN}${ChatColor.BOLD}All caught up!"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (robot.moneyOwed > 0) {
                description.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${Formats.formatMoney(robot.moneyOwed)}")
            }

            if (robot.tokensOwed > 0) {
                description.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${Formats.formatTokens(robot.tokensOwed)}")
            }

            if (description.isEmpty()) {
                description.add("${ChatColor.GRAY}You don't have anything to collect.")
            } else {
                description.add("")
                description.add("${ChatColor.YELLOW}Click to collect earnings.")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return if (robot.moneyOwed > 0 || robot.tokensOwed > 0) {
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