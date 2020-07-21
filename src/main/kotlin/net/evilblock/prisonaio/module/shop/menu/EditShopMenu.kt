/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.HelpButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.template.menu.EditTemplateLayoutMenu
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.template.ShopMenuTemplate
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat

class EditShopMenu(val shop: Shop) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Shop - ${shop.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = EditNameButton()
        buttons[2] = EditPriorityButton()
        buttons[4] = GuideButton()
        buttons[6] = EditTemplateButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        shop.items.forEachIndexed { index, shopItem ->
            buttons[18 + index] = ShopItemButton(shopItem)
        }

        val startFrom = 18 + shop.items.size
        for (index in startFrom until 54) {
            buttons[index] = EmptySlotButton()
        }

        return buttons
    }

    override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        if (itemStack.type != Material.AIR) {
            val shopItemStack = itemStack.clone()
            if (!shop.items.add(ShopItem(shopItemStack, shopItemStack.amount))) {
                player.sendMessage("${ChatColor.RED}The shop already has that item listed.")
                return false
            }

            ShopHandler.saveData()
            player.updateInventory()
            return true
        }

        return false
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                ShopEditorMenu().openMenu(player)
            }, 1L)
        }
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The name is how you want the shop",
                "${ChatColor.GRAY}to appear in chat and menu text.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit the name"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            player.closeInventory()

            ConversationUtil.startConversation(player, object : StringPrompt() {
                override fun getPromptText(context: ConversationContext): String {
                    return "${ChatColor.GREEN}Please input a new name. ${ChatColor.GRAY}(Colors supported)"
                }

                override fun acceptInput(context: ConversationContext, input: String): Prompt? {
                    if (input.length >= 32) {
                        context.forWhom.sendRawMessage("${ChatColor.RED}Shop name is too long! (${input.length} > 32 characters)")
                        return Prompt.END_OF_CONVERSATION
                    }

                    shop.name = ChatColor.translateAlternateColorCodes('&', input)
                    ShopHandler.saveData()

                    context.forWhom.sendRawMessage("${ChatColor.GREEN}Successfully updated name of shop.")
                    openMenu(context.forWhom as Player)

                    return Prompt.END_OF_CONVERSATION
                }
            })
        }
    }

    private inner class EditPriorityButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Priority ${ChatColor.GRAY}(${shop.priority})"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Priority is how a shop to sell to is chosen",
                "${ChatColor.GRAY}for a player.",
                "",
                "${ChatColor.GRAY}All the shops a player can sell to are",
                "${ChatColor.GRAY}collected and the shop with the lowest",
                "${ChatColor.GRAY}priority is selected.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to increase priority by +1",
                "${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to decrease priority by -1",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to increase priority by +10",
                "${ChatColor.RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.RED}to decrease priority by -10"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.HOPPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (clickType.isShiftClick) {
                    shop.priority += 10
                } else {
                    shop.priority += 1
                }
            } else if (clickType.isRightClick) {
                if (clickType.isShiftClick) {
                    shop.priority = 0.coerceAtLeast(shop.priority - 10)
                } else {
                    shop.priority = 0.coerceAtLeast(shop.priority - 1)
                }
            }

            ShopHandler.saveData()
        }
    }

    private inner class GuideButton : HelpButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}The Shop Editor"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}To add a new item to the shop, simply pickup",
                "${ChatColor.GRAY}and drop the item on an empty inventory slot.",
                "",
                "${ChatColor.GRAY}To ${ChatColor.GREEN}${ChatColor.BOLD}edit ${ChatColor.GRAY}the price of an item, ${ChatColor.GREEN}${ChatColor.BOLD}left-click",
                "${ChatColor.GRAY}the item and follow the procedure in chat.",
                "",
                "${ChatColor.GRAY}To ${ChatColor.RED}${ChatColor.BOLD}delete ${ChatColor.GRAY}an item, ${ChatColor.RED}${ChatColor.BOLD}right-click",
                "${ChatColor.GRAY}the item and complete the confirmation prompt.",
                "",
                "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Shop Permission",
                "",
                "${ChatColor.GRAY}To access this shop, the player needs the",
                "${ChatColor.YELLOW}prisonaio.shops.${shop.id.toLowerCase()} ${ChatColor.GRAY}permission."
            )
        }
    }

    private inner class EditTemplateButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Template"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf(
                "",
                "${ChatColor.GRAY}Edit this shop's menu template."
            )

            description.add("")

            if (shop.menuTemplate == null) {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create template")
            } else {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit template")
                description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to reset template")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.ITEM_FRAME
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (shop.menuTemplate == null) {
                    shop.menuTemplate = ShopMenuTemplate(shop.id, shop)
                }

                EditTemplateLayoutMenu(shop.menuTemplate!!).openMenu(player)
            } else if (clickType.isRightClick) {
                shop.menuTemplate = null
            }
        }
    }

    private inner class ShopItemButton(private val item: ShopItem) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            val description = arrayListOf<String>()
            description.add("")

            if (!item.buying) {
                description.add("${ChatColor.GRAY}Buy Price: ${ChatColor.RED}Shop does not buy")
            } else {
                description.add("${ChatColor.GRAY}Buy Price: ${ChatColor.AQUA}\$${ChatColor.GREEN}${NumberFormat.getInstance().format(item.buyPricePerUnit)}")
            }

            description.add("${ChatColor.GRAY}(Price that shop buys item for)")
            description.add("")

            if (!item.selling) {
                description.add("${ChatColor.GRAY}Sell Price: ${ChatColor.RED}Shop does not sell")
            } else {
                description.add("${ChatColor.GRAY}Sell Price: ${ChatColor.AQUA}$${ChatColor.GREEN}${NumberFormat.getInstance().format(item.sellPricePerUnit)}")
            }

            description.add("${ChatColor.GRAY}(Price that shop sells item for)")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit price")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete item")

            return ItemBuilder(item.itemStack.clone())
                .addToLore(*description.toTypedArray())
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditShopItemPriceMenu(this@EditShopMenu, item).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        shop.items.remove(item)
                        ShopHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully deleted item from shop.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made to shop.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class EmptySlotButton : Button() {
        override fun getName(player: Player): String {
            return " "
        }

        override fun getMaterial(player: Player): Material {
            return Material.AIR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (view.cursor != null && view.cursor.type != Material.AIR) {
                val shopItemStack = view.cursor.clone()

                if (shop.items.add(ShopItem(shopItemStack, shopItemStack.amount))) {
                    ShopHandler.saveData()
                    view.cursor = null
                } else {
                    view.cursor = shopItemStack
                    player.sendMessage("${ChatColor.RED}The shop already has that item listed!")
                }

                player.updateInventory()
            }
        }
    }

}