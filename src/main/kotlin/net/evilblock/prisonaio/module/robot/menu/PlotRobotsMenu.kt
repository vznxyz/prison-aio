/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot.menu

import com.intellectualcrafters.plot.`object`.Plot
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.menus.ExitButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.floor

class PlotRobotsMenu(private val plot: Plot) : Menu() {

    companion object {
        var disabled = false

        private val BORDER_SLOTS = listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 17,
            18, 26,
            36, 44,
            45, 53
        )
    }

    init {
        updateAfterClick = true
    }

    override fun getAutoUpdateTicks(): Long {
        return 1000L
    }

    override fun getTitle(player: Player): String {
        return "Robot Mechanic"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[3] = ViewRobotsButton()
        buttons[5] = MergeRobotsButton()

        for (i in 0 until 9) {
            if (!buttons.containsKey(i)) {
                buttons[i] = GlassButton(7)
            }
        }

        return buttons
    }

    private inner class ViewRobotsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}View Robots"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(text = "Click here to view all of the robots placed inside of your plot.", linePrefix = ChatColor.GRAY.toString())
        }

        override fun getMaterial(player: Player): Material {
            return Material.BEACON
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!RobotHandler.isPrivileged(player, player.location)) {
                return
            }

            if (clickType.isLeftClick) {
                ViewRobotsMenu(plot).openMenu(player)
            }
        }
    }

    private inner class MergeRobotsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Merge Robots"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(text = "Click here to merge all of the robots in your inventory.", linePrefix = ChatColor.GRAY.toString())
        }

        override fun getMaterial(player: Player): Material {
            return Material.ANVIL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (disabled) {
                player.sendMessage("${ChatColor.RED}This feature is currently disabled!")
                return
            }

            if (clickType.isLeftClick) {
                ConfirmMenu("Combine all robots in inventory?") { confirmed ->
                    if (confirmed) {
                        val owed = hashMapOf<Int, Int>()

                        for (index in player.inventory.storageContents.indices) {
                            val item = player.inventory.storageContents[index] ?: continue
                            if (item.amount <= 0) {
                                continue
                            }

                            if (!RobotUtils.isRobotItem(item) || RobotUtils.getRobotItemTier(item) > 6) {
                                continue
                            }

                            val itemTier = RobotUtils.getRobotItemTier(item)
                            owed[itemTier] = owed.getOrDefault(itemTier, 0) + item.amount

                            player.inventory.setItem(index, null)
                        }

                        var changed = true
                        while (changed) {
                            changed = false

                            for ((tier, amount) in owed.entries.sortedBy { it.key }) {
                                if (tier + 1 > 7) {
                                    continue
                                }

                                if (amount <= 1) {
                                    continue
                                }

                                val result = floor(amount / 2.0).toInt()
                                owed[tier + 1] = owed.getOrDefault(tier + 1, 0) + result

                                if (amount % 2 == 0) {
                                    owed.remove(tier)
                                } else {
                                    owed[tier] = 1
                                }

                                changed = true
                            }
                        }

                        if (owed.isNotEmpty()) {
                            for ((tier, amount) in owed) {
                                if (amount > 0) {
                                    player.inventory.addItem(RobotUtils.makeRobotItem(amount, tier))
                                }
                            }

                            player.updateInventory()

                            player.sendMessage("")
                            player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}Robot Mechanic")
                            player.sendMessage(" ${ChatColor.GRAY}I've combined the robots in your inventory!")
                            player.sendMessage("")

                            for ((tier, amount) in owed) {
                                if (amount > 0) {
                                    player.sendMessage(" ${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${ChatColor.GRAY}x$amount ${ChatColor.RED}${RobotsModule.getTierName(tier)}")
                                }
                            }

                            player.sendMessage("")
                        } else {
                            player.sendMessage("${ChatColor.RED}You didn't have any robots to combine in your inventory!")
                        }
                    }

                    this@PlotRobotsMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class ViewRobotsMenu(private val plot: Plot) : PaginatedMenu() {
        init {
            updateAfterClick = true
            autoUpdate = true
        }

        override fun getPrePaginatedTitle(player: Player): String {
            return "Plot Robots"
        }

        override fun getGlobalButtons(player: Player): Map<Int, Button>? {
            val buttons = hashMapOf<Int, Button>()

            buttons[2] = CollectAllButton()
            buttons[4] = InfoButton()
            buttons[6] = PickupAllButton()
            buttons[8] = ExitButton()

            for (i in BORDER_SLOTS) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }

            return buttons
        }

        override fun getAllPagesButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            val robots = getAllRobots(plot)
            for (robot in robots.sortedBy { it.tier }) {
                buttons[buttons.size] = RobotButton(robot)
            }

            return buttons
        }

        override fun getAllPagesButtonSlots(): List<Int> {
            return arrayListOf<Int>().also {
                it.addAll(10..16)
                it.addAll(19..25)
                it.addAll(28..34)
                it.addAll(37..43)
            }
        }

        override fun getMaxItemsPerPage(player: Player): Int {
            return 35
        }

        override fun getPageButtonSlots(): Pair<Int, Int> {
            return Pair(27, 35)
        }

        override fun onClose(player: Player, manualClose: Boolean) {
            if (manualClose) {
                Tasks.delayed(1L) {
                    this@PlotRobotsMenu.openMenu(player)
                }
            }
        }

        private inner class InfoButton : Button() {
            override fun getName(player: Player): String {
                return "${ChatColor.RED}${ChatColor.BOLD}Plot Robots"
            }

            override fun getDescription(player: Player): List<String> {
                val description = arrayListOf<String>()

                val possessiveContext = if (plot.owners.contains(player.uniqueId)) {
                    "your"
                } else {
                    "this"
                }

                val robots = RobotHandler.getRobotsByPlot(plot.id)
                if (robots.isEmpty()) {
                    description.add("${ChatColor.GRAY}There aren't any robots on $possessiveContext plot.")
                } else {
                    var uncollectedMoney = BigDecimal(0.0)
                    var uncollectedTokens = BigInteger("0")

                    for (robot in robots.map { it as MinerRobot }) {
                        uncollectedMoney += robot.moneyOwed
                        uncollectedTokens += robot.tokensOwed.toBigInteger()
                    }

                    description.add("${ChatColor.GRAY}There are ${ChatColor.RED}${ChatColor.BOLD}${robots.size} ${ChatColor.GRAY}robots on $possessiveContext plot.")
                    description.add("")
                    description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Collectable Earnings")
                    description.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${Formats.formatMoney(uncollectedMoney)}")
                    description.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${Formats.formatTokens(uncollectedTokens)}")
                }

                return description
            }

            override fun getMaterial(player: Player): Material {
                return Material.BEACON
            }
        }

        private inner class CollectAllButton : Button() {
            override fun getName(player: Player): String {
                return "${ChatColor.GREEN}${ChatColor.BOLD}Collect All"
            }

            override fun getDescription(player: Player): List<String> {
                val possessiveContext = if (plot.owners.contains(player.uniqueId)) {
                    "your"
                } else {
                    "this"
                }

                return TextSplitter.split(text = "Click here to collect the earnings from all robots on $possessiveContext plot.", linePrefix = ChatColor.GRAY.toString())
            }

            override fun getMaterial(player: Player): Material {
                return Material.HOPPER
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    var moneyCollected = BigDecimal(0.0)
                    var tokensCollected = BigInteger("0")

                    val robots = getAllRobots(plot)
                    for (robot in robots) {
                        val moneyOwed = robot.moneyOwed
                        val tokensOwed = robot.tokensOwed

                        if (robot.collectEarnings(player, sendMessages = false)) {
                            moneyCollected += moneyOwed
                            tokensCollected += tokensOwed.toBigInteger()
                        }
                    }

                    val zeroBigDecimal = BigDecimal(0.0)
                    val zeroBigInt = BigInteger("0")

                    if (moneyCollected > zeroBigDecimal && tokensCollected > zeroBigInt) {
                        player.sendMessage("${RobotsModule.CHAT_PREFIX}You collected ${Formats.formatMoney(moneyCollected)} ${ChatColor.GRAY}and ${Formats.formatTokens(tokensCollected)}${ChatColor.GRAY}.")
                    } else if (moneyCollected > zeroBigDecimal) {
                        player.sendMessage("${RobotsModule.CHAT_PREFIX}You collected ${Formats.formatMoney(moneyCollected)}${ChatColor.GRAY}.")
                    } else if (tokensCollected > zeroBigInt) {
                        player.sendMessage("${RobotsModule.CHAT_PREFIX}You collected ${Formats.formatTokens(tokensCollected)}${ChatColor.GRAY}.")
                    }
                }
            }
        }

        private inner class PickupAllButton : Button() {
            override fun getName(player: Player): String {
                return "${ChatColor.RED}${ChatColor.BOLD}Pickup All"
            }

            override fun getDescription(player: Player): List<String> {
                return arrayListOf<String>().also {
                    val possessiveContext = if (plot.owners.contains(player.uniqueId)) {
                        "your"
                    } else {
                        "this"
                    }

                    it.add("")
                    it.add("${ChatColor.RED}${ChatColor.BOLD}Warning!")
                    it.add("${ChatColor.GRAY}Robots lose all stats when they")
                    it.add("${ChatColor.GRAY}are picked up.")
                    it.add("")
                    it.addAll(TextSplitter.split(text = "Click here to pickup all of the robots on $possessiveContext plot.", linePrefix = ChatColor.GRAY.toString()))
                }
            }

            override fun getMaterial(player: Player): Material {
                return Material.EXPLOSIVE_MINECART
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    ConfirmMenu { confirmed ->
                        if (confirmed) {
                            val pickupItems = arrayListOf<ItemStack>()

                            val robots = getOwnedRobots(player, plot)
                            for (robot in robots) {
                                if (!RobotHandler.getRobots().contains(robot)) {
                                    continue
                                }

                                RobotsModule.getPluginFramework().logger.info("${player.name} is picking up a Tier ${robot.tier} Robot from ${robot.location.world.name}, ${robot.location.x}, ${robot.location.y}, ${robot.location.z}")
                                RobotHandler.forgetRobot(robot)

                                robot.clearFakeBlock()
                                robot.destroyForCurrentWatchers()

                                pickupItems.add(RobotUtils.makeRobotItem(1, robot.tier))
                            }

                            if (pickupItems.isEmpty()) {
                                player.sendMessage("${ChatColor.RED}There are no robots on your plot to pickup.")
                                return@ConfirmMenu
                            }

                            for (robotItem in pickupItems) {
                                val notInserted = player.inventory.addItem(robotItem)
                                for (item in notInserted.values) {
                                    val lostItems = player.enderChest.addItem(item)
                                    if (lostItems.isNotEmpty()) {
                                        if (lostItems.values.first().amount != item.amount) {
                                            player.sendMessage("${ChatColor.GREEN}You didn't have enough room in your inventory space, so we put items in your enderchest!")
                                        }

                                        for (lostItem in lostItems.values) {
                                            val tier = RobotUtils.getRobotItemTier(lostItem).coerceAtLeast(0)
                                            RobotsModule.getPluginFramework().logger.warning("Failed to give ${player.name} ${lostItem.amount}x Tier $tier Robot(s)")
                                        }
                                    } else {
                                        player.sendMessage("${ChatColor.GREEN}You didn't have enough room in your inventory space, so we put items in your enderchest!")
                                    }
                                }
                            }
                        }

                        this@PlotRobotsMenu.openMenu(player)
                    }.openMenu(player)
                }
            }
        }

        private inner class RobotButton(private val robot: MinerRobot) : Button() {
            override fun getName(player: Player): String {
                val tierName = robot.getTierName()
                val tierColor = ChatColor.getLastColors(tierName)

                return if (robot.owner == player.uniqueId) {
                    "${tierColor}Your $tierName"
                } else {
                    "${tierColor}${robot.getOwnerName()}'s $tierName"
                }
            }

            override fun getDescription(player: Player): List<String> {
                val description = arrayListOf<String>()

                description.add("${ChatColor.GRAY}(Located at ${ChatColor.AQUA}${robot.location.x.toInt()}${ChatColor.GRAY}, ${ChatColor.AQUA}${robot.location.y.toInt()}${ChatColor.GRAY}, ${ChatColor.AQUA}${robot.location.z.toInt()}${ChatColor.GRAY})")
                description.add("")
                description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Upgrades")

                if (robot.getAppliedUpgrades().isEmpty()) {
                    description.add("${ChatColor.GRAY}No upgrades applied")
                } else {
                    for (upgrade in robot.getAppliedUpgrades()) {
                        description.add("${upgrade.getColor()}${ChatColor.BOLD}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}${upgrade.getName()} ${robot.getUpgradeLevel(upgrade)}")
                    }
                }

                description.add("")
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}for more options")
                description.add("${ChatColor.YELLOW}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.YELLOW}to collect earnings")

                return description
            }

            override fun getMaterial(player: Player): Material {
                return Material.ARMOR_STAND
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    ManageRobotMenu(robot).openMenu(player)
                } else if (clickType.isRightClick) {
                    robot.collectEarnings(player)
                }
            }
        }

        private fun getAllRobots(plot: Plot): List<MinerRobot> {
            return RobotHandler.getRobotsByPlot(plot.id).map { robot -> robot as MinerRobot }
        }

        private fun getOwnedRobots(player: Player, plot: Plot): List<MinerRobot> {
            return getAllRobots(plot).let { it.filter { robot -> robot.owner == player.uniqueId } }
        }
    }

}