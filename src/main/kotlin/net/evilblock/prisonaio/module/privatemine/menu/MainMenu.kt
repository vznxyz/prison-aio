package net.evilblock.prisonaio.module.privatemine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.privatemine.PrivateMinesModule
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.util.function.Function

class MainMenu : Menu(PrivateMinesModule.getMenuTitle("main-menu")) {

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[11] = MyMinesMenuButton()
        buttons[15] = PublicMinesMenuButton()

        for (i in 0..26) {
            if (!buttons.containsKey(i)) {
                if (BORDER_SLOTS.contains(i)) {
                    buttons[i] = Button.Companion.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
                } else {
                    buttons[i] = Button.Companion.placeholder(Material.STAINED_GLASS_PANE, 10, " ")
                }
            }
        }

        return buttons
    }

    companion object {
        private val BORDER_SLOTS = listOf(0, 8, 9, 17, 18, 26)
    }

    class MyMinesMenuButton : Button() {
        override fun getName(player: Player): String {
            return PrivateMinesModule.getButtonTitle("main-menu", "my-mines")
        }

        override fun getDescription(player: Player): List<String> {
            return PrivateMinesModule.getButtonLore("main-menu", "my-mines")
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_PICKAXE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val meta = MineSelectionMenu.Meta()

            meta.minesFunc = { PrivateMineHandler.getAccessibleMines(player.uniqueId) }

            meta.clickLambda = { mine, clickType ->
                player.closeInventory()

                if (clickType.isLeftClick) {
                    PrivateMineHandler.attemptJoinMine(mine, player)
                }

                if (clickType.isRightClick) {
                    if (mine.owner == player.uniqueId) {
                        SettingsMenu(mine).openMenu(player)
                    }
                }
            }

            meta.titleFunc = Function { mine ->
                val ownerContext = if (mine.owner == player.uniqueId) {
                    "Your"
                } else {
                    "${mine.getOwnerName()}'s"
                }

                return@Function "&a&l$ownerContext Tier ${mine.tier.number} Mine"
            }

            meta.loreFunc = Function { mine ->
                val slotsTaken = mine.whitelistedPlayers.size
                val reservedSlots = mine.tier.playerLimit
                val resetInterval = TimeUtil.formatIntoDetailedString((mine.tier.resetInterval / 1000).toInt())
                val salesTax = mine.salesTax

                val description = arrayListOf<String>()

                description.add("${ChatColor.GRAY}Players Active: ${ChatColor.GREEN}${mine.getActivePlayers().size}")

                if (mine.owner == player.uniqueId) {
                    description.add("${ChatColor.GRAY}Reserved Slots: ${ChatColor.GREEN}$slotsTaken/$reservedSlots")
                }

                description.add("${ChatColor.GRAY}Reset Interval: ${ChatColor.GREEN}$resetInterval")
                description.add("${ChatColor.GRAY}Sales Tax: ${ChatColor.GREEN}$salesTax%")
                description.add("")
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to teleport")

                if (mine.owner == player.uniqueId) {
                    description.add("${ChatColor.AQUA}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.AQUA}to open settings")
                }

                return@Function description
            }

            MineSelectionMenu("&a&lMy Mines", meta).openMenu(player)
        }
    }

    class PublicMinesMenuButton : Button() {
        override fun getName(player: Player): String {
            return PrivateMinesModule.getButtonTitle("main-menu", "public-mines")
        }

        override fun getDescription(player: Player): List<String> {
            return PrivateMinesModule.getButtonLore("main-menu", "public-mines")
        }

        override fun getMaterial(player: Player): Material {
            return Material.COMPASS
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val meta = MineSelectionMenu.Meta()

            meta.minesFunc = { PrivateMineHandler.getPublicMines() }
            meta.clickLambda = { mine, clickType ->
                if (clickType.isLeftClick) {
                    PrivateMineHandler.attemptJoinMine(mine, player)
                }
            }

            meta.titleFunc = Function { mine ->
                val owner = if (mine.owner == player.uniqueId) {
                    "Your"
                } else {
                    "${mine.getOwnerName()}'s"
                }

                return@Function "&a&l$owner Tier ${mine.tier.number} Mine"
            }

            meta.loreFunc = Function { mine ->
                val slotsTaken = mine.whitelistedPlayers.size
                val reservedSlots = mine.tier.playerLimit
                val resetInterval = TimeUtil.formatIntoDetailedString((mine.tier.resetInterval / 1000).toInt())
                val salesTax = mine.salesTax

                val description = arrayListOf<String>()

                description.add("${ChatColor.GRAY}Players Active: ${ChatColor.GREEN}${mine.getActivePlayers().size}")

                if (mine.owner == player.uniqueId) {
                    description.add("${ChatColor.GRAY}Reserved Slots: ${ChatColor.GREEN}$slotsTaken/$reservedSlots")
                }

                description.add("${ChatColor.GRAY}Reset Interval: ${ChatColor.GREEN}$resetInterval")
                description.add("${ChatColor.GRAY}Sales Tax: ${ChatColor.GREEN}$salesTax%")
                description.add("")
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to teleport")

                if (mine.owner == player.uniqueId) {
                    description.add("${ChatColor.AQUA}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.AQUA}to open settings")
                }

                return@Function description
            }

            MineSelectionMenu("&a&lPublic Mines", meta).openMenu(player)
        }
    }

}