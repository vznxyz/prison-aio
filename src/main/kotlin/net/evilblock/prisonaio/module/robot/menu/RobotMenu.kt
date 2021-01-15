package net.evilblock.prisonaio.module.robot.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.MenuButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.InventoryUtils
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.render.GraphicalTable
import net.evilblock.prisonaio.module.generator.menu.GeneratorMenu
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import net.evilblock.prisonaio.module.robot.impl.modifier.RobotModifierUtils
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

class RobotMenu(private val robot: MinerRobot) : Menu() {

    companion object {
        val MODIFIER_SLOTS = listOf(40)
    }

    init {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return ChatColor.stripColor(robot.getTierName())
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[4] = YourRobotButton()
            buttons[19] = UpgradesButton()
            buttons[21] = CosmeticsButton()
            buttons[23] = PickupRobotButton()
            buttons[25] = CollectButton()

            if (robot.modifierStorage.isNotEmpty()) {
                for ((index, itemStack) in robot.modifierStorage.withIndex()) {
                    if (itemStack != null && index < GeneratorMenu.MODIFIER_SLOTS.size) {
                        buttons[MODIFIER_SLOTS[index]] = ModifierItemButton(itemStack)
                    }
                }
            }

            buttons[41] = MenuButton()
                .icon(Material.BOOK)
                .name("${ChatColor.GOLD}${ChatColor.BOLD}Modifiers")
                .lore(arrayListOf<String>().also { desc ->
                    desc.add("")
                    desc.addAll(TextSplitter.split(text = "Place any modifier items you have in the empty slot to the left."))

                    val modifiers = robot.modifiers.values.let {
                        if (it.isEmpty()) {
                            it
                        } else {
                            robot.modifiers.values.filter { it.isPermanent() || !it.isExpired() }
                        }
                    }

                    if (modifiers.isNotEmpty()) {
                        desc.add("")

                        for (modifier in modifiers) {
                            desc.add("${modifier.type.getColoredName()} ${ChatColor.GRAY}(${TimeUtil.formatIntoAbbreviatedString((modifier.getRemainingTime() / 1000.0).toInt())})")
                        }
                    }
                })

            for (i in 0 until 54) {
                if (!buttons.containsKey(i) && !MODIFIER_SLOTS.contains(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    override fun acceptsInsertedItem(player: Player, itemStack: ItemStack, slot: Int): Boolean {
        if (!MODIFIER_SLOTS.contains(slot)) {
            return false
        }

        val modifier = RobotModifierUtils.extractModifierFromItemStack(itemStack) ?: return false

        val notInserted = InventoryUtils.addAmountToInventory(robot.modifierStorage, itemStack, itemStack.amount)
        if (notInserted != null) {
            player.inventory.addItem(notInserted)
            player.updateInventory()
        }

        return true
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
                desc.add("${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}Revenue History")
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

                    .borders(true)
                    .render()

                desc.addAll(table)

                robot.renderModifiersInfo(desc)
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
                RobotUpgradesMenu(robot).openMenu(player)
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
                desc.add("${ChatColor.GRAY}You own ${ChatColor.GREEN}${ChatColor.BOLD}$ownedCosmetics${ChatColor.GRAY}/$availableCosmetics ${ChatColor.GRAY}cosmetics.")
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
            RobotCosmeticsMenu(robot).openMenu(player)
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

    private inner class ModifierItemButton(private val itemStack: ItemStack) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return itemStack.clone()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (player.inventory.firstEmpty() == -1) {
                player.sendMessage("${ChatColor.RED}You need a free inventory space to do that!")
                return
            }

            when (clickType) {
                ClickType.LEFT -> {
                    val removed = robot.removeModifierItem(itemStack, itemStack.amount)
                    if (removed > 0) {
                        player.inventory.addItem(ItemBuilder.copyOf(itemStack).amount(removed).build())
                        player.updateInventory()
                    }
                }
                ClickType.RIGHT -> {
                    var removed = 0
                    if (itemStack.amount > 1) {
                        removed = robot.removeModifierItem(itemStack, itemStack.amount / 2)
                    } else if (itemStack.amount == 1) {
                        removed = robot.removeModifierItem(itemStack, 1)
                    }

                    if (removed > 0) {
                        player.inventory.addItem(ItemBuilder.copyOf(itemStack).amount(removed).build())
                        player.updateInventory()
                    }
                }
                else -> {

                }
            }
        }
    }

}