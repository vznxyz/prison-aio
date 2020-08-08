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
import net.evilblock.cubed.menu.template.button.CloneableMenuTemplateButton
import net.evilblock.cubed.menu.template.menu.EditTemplateLayoutMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.template.ShopMenuTemplate
import net.evilblock.prisonaio.util.economy.menu.SelectCurrencyMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class EditShopMenu(val shop: Shop) : Menu() {

    init {
        updateAfterClick = true
        shop.syncItemsOrder()
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
        buttons[8] = EditCurrencyButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        shop.items.sortedBy { it.order }.forEachIndexed { index, shopItem ->
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
            shop.items.add(ShopItem(shopItemStack, shopItemStack.amount))

            Tasks.async {
                shop.syncItemsOrder()
                ShopHandler.saveData()
            }

            player.updateInventory()
            return true
        }

        return false
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                ShopEditorMenu().openMenu(player)
            }
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
                "${ChatColor.GRAY}Selling items works by collecting",
                "${ChatColor.GRAY}all of the shops a player has",
                "${ChatColor.GRAY}access to. The order they are sold",
                "${ChatColor.GRAY}to is determined by the priority.",
                "",
                "${ChatColor.GRAY}A shop with higher priority than",
                "${ChatColor.GRAY}another shop will be sold to first.",
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
            val mod = if (clickType.isShiftClick) 10 else 1
            if (clickType.isLeftClick) {
                shop.priority += mod
            } else if (clickType.isRightClick) {
                shop.priority = (shop.priority - mod).coerceAtLeast(0)
            }

            Tasks.async {
                ShopHandler.saveData()
            }
        }
    }

    private inner class GuideButton : HelpButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Shop Editor Help"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "${ChatColor.GRAY}To add a new item to the shop, simply pickup",
                "${ChatColor.GRAY}and drop the item on an empty inventory slot.",
                "",
                "${ChatColor.GRAY}To ${ChatColor.GREEN}${ChatColor.BOLD}edit ${ChatColor.GRAY}the price of an item, ${ChatColor.GREEN}${ChatColor.BOLD}left/right-click",
                "${ChatColor.GRAY}the item and follow the procedure in chat.",
                "",
                "${ChatColor.GRAY}To ${ChatColor.RED}${ChatColor.BOLD}delete ${ChatColor.GRAY}an item, ${ChatColor.RED}${ChatColor.BOLD}shift right-click",
                "${ChatColor.GRAY}the item and complete the confirmation prompt.",
                "",
                "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Granting Access",
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
                description.add("${ChatColor.AQUA}${ChatColor.BOLD}SHIFT-CLICK ${ChatColor.AQUA}to copy template")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.ITEM_FRAME
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && clickType.isShiftClick) {
                SelectShopMenu("Copy Shop Template") { selected ->
                    if (selected.menuTemplate != null) {
                        shop.menuTemplate = ShopMenuTemplate(shop.id, shop)
                        shop.menuTemplate!!.boundButtons.putAll(selected.menuTemplate!!.boundButtons
                            .filter { it.value is CloneableMenuTemplateButton }
                            .map { it.key to (it.value as CloneableMenuTemplateButton).clone() }
                            .toMap())

                        Tasks.async {
                            ShopHandler.saveData()
                        }
                    }
                }.openMenu(player)
            } else if (clickType.isLeftClick) {
                if (shop.menuTemplate == null) {
                    shop.menuTemplate = ShopMenuTemplate(shop.id, shop)

                    Tasks.async {
                        ShopHandler.saveData()
                    }
                }

                EditTemplateLayoutMenu(shop.menuTemplate!!).openMenu(player)
            } else if (clickType.isRightClick) {
                shop.menuTemplate = null

                Tasks.async {
                    ShopHandler.saveData()
                }
            }
        }
    }

    private inner class EditCurrencyButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Currency"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "Change the currency that players use to purchase items FROM the store. This will not affect selling blocks to shops at all.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            description.add("")
            description.add("${ChatColor.GRAY}Currently using ${shop.currency.displayName}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit currency")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return shop.currency.icon
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectCurrencyMenu { currency ->
                    shop.currency = currency

                    Tasks.async {
                        ShopHandler.saveData()
                    }

                    this@EditShopMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class ShopItemButton(private val item: ShopItem) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}Buying Price: ${shop.currency.format(item.buyPricePerUnit)}")
            description.add("${ChatColor.GRAY}(Price that the shop buys for)")
            description.add("")
            description.add("${ChatColor.GRAY}Selling Price: ${shop.currency.format(item.sellPricePerUnit)}")
            description.add("${ChatColor.GRAY}(Price that the shop sells for)")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to set buy price")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.YELLOW}to set sell price")
            description.add("")

            val leftArrowColor = if (item.order > 0) {
                ChatColor.BLUE
            } else {
                ChatColor.GRAY
            }

            val rightArrowColor = if (item.order < shop.items.size) {
                ChatColor.BLUE
            } else {
                ChatColor.GRAY
            }

            description.add("$leftArrowColor${ChatColor.BOLD}⬅ ${ChatColor.YELLOW}${ChatColor.BOLD}SHIFT LEFT/RIGHT CLICK $rightArrowColor${ChatColor.BOLD}➡")
            description.add("")
            description.add("${ChatColor.RED}${ChatColor.BOLD}MIDDLE-CLICK ${ChatColor.RED}to delete item")

            return ItemBuilder(item.itemStack.clone())
                .addToLore(*description.toTypedArray())
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType == ClickType.MIDDLE) {
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

                return
            }

            if (clickType.isShiftClick) {
                val canShiftLeft = item.order > 0
                val canShiftRight = item.order < shop.items.size - 1

                val orderMod = if (canShiftLeft && clickType.isLeftClick) {
                    -1
                } else if (canShiftRight && clickType.isRightClick) {
                    1
                } else {
                    0
                }

                if (orderMod != 0) {
                    item.order += orderMod

                    Tasks.async {
                        ShopHandler.saveData()
                    }
                }

                return
            }

            if (clickType.isLeftClick) {
                NumberPrompt("${ChatColor.GREEN}Please input a buying price.") { price ->
                    assert(price.toInt() >= 0) { "Price must be equal to or greater than 0.0" }

                    item.buyPricePerUnit = price.toDouble()

                    Tasks.async {
                        ShopHandler.saveData()
                    }

                    this@EditShopMenu.openMenu(player)
                }.start(player)
            } else if (clickType.isRightClick) {
                NumberPrompt("${ChatColor.GREEN}Please input a selling price.") { price ->
                    assert(price.toInt() >= 0) { "Price must be equal to or greater than 0.0" }

                    item.sellPricePerUnit = price.toDouble()

                    Tasks.async {
                        ShopHandler.saveData()
                    }

                    this@EditShopMenu.openMenu(player)
                }.start(player)
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

                shop.items.add(ShopItem(shopItemStack, shopItemStack.amount))

                Tasks.async {
                    shop.syncItemsOrder()
                    ShopHandler.saveData()
                }

                view.cursor = null
                player.updateInventory()
            }
        }
    }

}