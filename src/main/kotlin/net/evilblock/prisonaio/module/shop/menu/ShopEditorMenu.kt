package net.evilblock.prisonaio.module.shop.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.text.NumberFormat

class ShopEditorMenu : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Shop Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()
        buttons[2] = AddShopButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        ShopHandler.getShops().sortedBy { it.priority }.forEachIndexed { index, shop ->
            buttons[index] = ShopButton(shop)
        }

        return buttons
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class AddShopButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Shop"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Create a new shop by completing",
                "${ChatColor.GRAY}the setup procedure.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new shop"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText(EzPrompt.IDENTIFIER_PROMPT)
                    .charLimit(16)
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .acceptInput { player, input ->
                        if (ShopHandler.getShopById(input).isPresent) {
                            player.sendMessage("${ChatColor.RED}A shop's ID must be unique, and a shop with the ID `$input` already exists.")
                            return@acceptInput
                        }

                        val shop = Shop(input)

                        ShopHandler.trackShop(shop)
                        ShopHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully created a new shop.")

                        openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class ShopButton(private val shop: Shop) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}${shop.name}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")

            val formattedItemsListed = NumberFormat.getInstance().format(shop.items.size.toLong())
            description.add("${ChatColor.GRAY}Items Listed: ${ChatColor.GREEN}${formattedItemsListed}")

            description.add("${ChatColor.GRAY}Priority: ${ChatColor.GREEN}${shop.priority}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit shop")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete shop")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.CHEST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditShopMenu(shop).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        ShopHandler.forgetShop(shop)
                        ShopHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully deleted shop ${ChatColor.RESET}${shop.name}${ChatColor.GREEN}.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made to shop.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

}