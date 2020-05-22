package net.evilblock.prisonaio.module.shop.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.prompt.PricePrompt
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditShopItemPriceMenu(private val previous: EditShopMenu, val item: ShopItem) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Price - ${previous.shop.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0..8) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
        }

        buttons[2] = EditPriceButton(PriceType.BUYING)
        buttons[6] = EditPriceButton(PriceType.SELLING)

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                previous.openMenu(player)
            }, 1L)
        }
    }

    enum class PriceType {
        BUYING,
        SELLING
    }

    private inner class EditPriceButton(private val type: PriceType) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}${Formats.capitalizeFully(type.name)} Price"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (type == PriceType.BUYING) {
                description.add("")

                if (item.buying) {
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}Shop is buying this item")
                } else {
                    description.add("${ChatColor.RED}${ChatColor.BOLD}Shop is not buying this item")
                }

                description.add("")
                description.add("${ChatColor.GRAY}The price it costs for the shop")
                description.add("${ChatColor.GRAY}to buy a unit of this item from")
                description.add("${ChatColor.GRAY}a player.")
                description.add("")
                description.add("${ChatColor.AQUA}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.AQUA}to edit ${type.name.toLowerCase()} price")

                if (item.buying) {
                    description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to stop buying this item")
                } else {
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.GREEN}to start buying this item")
                }
            } else {
                description.add("")

                if (item.selling) {
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}Shop is selling this item")
                } else {
                    description.add("${ChatColor.RED}${ChatColor.BOLD}Shop is not selling this item")
                }

                description.add("")
                description.add("${ChatColor.GRAY}The price it costs for the player")
                description.add("${ChatColor.GRAY}to buy a unit of this item from")
                description.add("${ChatColor.GRAY}the shop.")
                description.add("")
                description.add("${ChatColor.AQUA}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.AQUA}to edit ${type.name.toLowerCase()} price")

                if (item.selling) {
                    description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to stop selling this item")
                } else {
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.GREEN}to start selling this item")
                }
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return if (type == PriceType.BUYING) {
                Material.SKULL_ITEM
            } else {
                Material.ITEM_FRAME
            }
        }

        override fun getDamageValue(player: Player): Byte {
            return if (type == PriceType.BUYING) {
                3.toByte()
            } else {
                0.toByte()
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()

                ConversationUtil.startConversation(player, PricePrompt("shop") { price ->
                    if (type == PriceType.BUYING) {
                        item.buyPricePerUnit = price
                    } else {
                        item.sellPricePerUnit = price
                    }

                    ShopHandler.saveData()

                    player.sendMessage("${ChatColor.GREEN}Successfully updated ${type.name.toLowerCase()} price of item.")

                    openMenu(player)
                })
            } else if (clickType.isRightClick) {
                if (type == PriceType.BUYING) {
                    item.buying = !item.buying
                } else {
                    item.selling = !item.selling
                }
            }
        }
    }

}