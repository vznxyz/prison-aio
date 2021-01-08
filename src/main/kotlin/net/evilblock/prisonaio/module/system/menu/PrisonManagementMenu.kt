/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.prisonaio.module.auction.AuctionHouseHandler
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.system.SystemModule
import net.evilblock.prisonaio.module.mine.menu.MineEditorMenu
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class PrisonManagementMenu : Menu() {

    companion object {
        private val BUTTON_SLOTS = arrayListOf<Int>().also {
            it.addAll(11..16)
            it.addAll(20..25)
            it.addAll(29..34)
            it.addAll(38..43)
        }
    }

    init {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Prison Management"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        if (!player.isOp && !player.hasPermission(Permissions.SYSTEM_ADMIN)) {
            return emptyMap()
        }

        return hashMapOf<Int, Button>().also { buttons ->
            buttons[9] = ServerStatusButton()
            buttons[18] = ManagePlayerButton()
            buttons[27] = ManageGangButton()

            var index = 0
            buttons[BUTTON_SLOTS[index++]] = ManageMinesButton()
            buttons[BUTTON_SLOTS[index++]] = ManageEnchantsButton()
            buttons[BUTTON_SLOTS[index++]] = ManageGangsButton()
            buttons[BUTTON_SLOTS[index++]] = ManageBattlePassButton()
            buttons[BUTTON_SLOTS[index++]] = ManageAuctionHouseButton()
            buttons[BUTTON_SLOTS[index++]] = ManageCoinFlipButton()
            buttons[BUTTON_SLOTS[index++]] = ManageRobotsButton()
            buttons[BUTTON_SLOTS[index++]] = ManageGeneratorsButton()
            buttons[BUTTON_SLOTS[index++]] = ManageAnalyticsButton()

            for (i in 0 until 54) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    private inner class ServerStatusButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Server Status"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val currentTPS = MinecraftReflection.getTPS()

                val formattedTps = TextUtil.formatTPS(currentTPS)
                val tpsColor = TextUtil.colorPercentage(NumberUtils.percentage(currentTPS, 20.0))

                val usedMemory = SystemModule.getUsedMemory()
                val totalMemory = SystemModule.getTotalMemory()
                val memoryColor = TextUtil.colorPercentage(NumberUtils.percentage(usedMemory.first, usedMemory.first))

                val formattedViewDist = NumberUtils.format(Bukkit.getViewDistance())

                desc.add("${ChatColor.GRAY}TPS: $tpsColor${ChatColor.BOLD}$formattedTps")
                desc.add("${ChatColor.GRAY}Memory: $memoryColor${ChatColor.BOLD}${usedMemory.first}${usedMemory.second}${ChatColor.GRAY}/${totalMemory.first}${totalMemory.second}")
                desc.add("${ChatColor.GRAY}Players: ${ChatColor.YELLOW}${ChatColor.BOLD}${Bukkit.getOnlinePlayers().size}${ChatColor.GRAY}/${Bukkit.getMaxPlayers()}")
                desc.add("${ChatColor.GRAY}Entities: ${ChatColor.YELLOW}${ChatColor.BOLD}${SystemModule.countEntities()}")
                desc.add("${ChatColor.GRAY}Worlds: ${ChatColor.YELLOW}${ChatColor.BOLD}${Bukkit.getWorlds().size}")
                desc.add("${ChatColor.GRAY}View Distance: ${ChatColor.YELLOW}${ChatColor.BOLD}${formattedViewDist}")
                desc.add("${ChatColor.DARK_GRAY}${ChatColor.BOLD}-")
                desc.add("${ChatColor.GRAY}User Cache: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(UserHandler.getUsers().size)}")
                desc.add("${ChatColor.GRAY}Gangs: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(GangHandler.getAllGangs().size)}")
                desc.add("${ChatColor.GRAY}Robots: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(RobotHandler.getRobots().size)}")
                desc.add("${ChatColor.GRAY}Generators: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(GeneratorHandler.getGenerators().size)}")
                desc.add("${ChatColor.GRAY}AH Listings: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(AuctionHouseHandler.getAllListings().size)}")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }
    }

    private inner class ManagePlayerButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Lookup/Manage Player"
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3
        }
    }

    private inner class ManageGangButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Lookup/Manage Gang"
        }

        override fun getMaterial(player: Player): Material {
            return Material.BEACON
        }
    }

    private inner class ManageMinesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage Mines"
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_ORE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                MineEditorMenu().openMenu(player)
            }
        }
    }

    private inner class ManageEnchantsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage Enchants"
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENCHANTMENT_TABLE
        }
    }

    private inner class ManageGangsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage Gangs"
        }

        override fun getMaterial(player: Player): Material {
            return Material.BEACON
        }
    }

    private inner class ManageBattlePassButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage BattlePass"
        }

        override fun getMaterial(player: Player): Material {
            return Material.SADDLE
        }
    }

    private inner class ManageAuctionHouseButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage Auction House"
        }

        override fun getMaterial(player: Player): Material {
            return Material.HAY_BLOCK
        }
    }

    private inner class ManageCoinFlipButton : TexturedHeadButton(Constants.TOKEN_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage CoinFlip"
        }
    }

    private inner class ManageRobotsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage Robots"
        }

        override fun getMaterial(player: Player): Material {
            return Material.ARMOR_STAND
        }
    }

    private inner class ManageGeneratorsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage Robots"
        }

        override fun getMaterial(player: Player): Material {
            return Material.HOPPER
        }
    }

    private inner class ManageAnalyticsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Manage Analytics"
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }
    }

}