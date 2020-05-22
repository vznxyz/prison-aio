package net.evilblock.prisonaio.module.enchant.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.button.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

class SalvagePickaxeMenu(player: Player) : Menu() {

    companion object {
        fun tryOpeningMenu(player: Player) {
            try {
                SalvagePickaxeMenu(player).openMenu(player)
            } catch (e: IllegalStateException) {
                player.sendMessage(EnchantsManager.CHAT_PREFIX + ChatColor.RED + e.message)
            }
        }
    }

    private val heldItemSlot: Int
    val pickaxeInHand: ItemStack
    private var pendingConfirmation = false

    init {
        this.placeholder = true
        this.keepBottomMechanics = false

        // get item in hand
        // make sure item in hand isn't null
        val itemInHand = player.inventory.getItem(player.inventory.heldItemSlot) ?: throw IllegalStateException("You have no item in your hand.")

        // check if the item is a pickaxe
        check(itemInHand.type.toString().endsWith("_PICKAXE")) { "You must be holding the pickaxe you would like to salvage." }

        val heldItemSlot = player.inventory.heldItemSlot

        // remove item from player's inventory
        player.inventory.setItem(heldItemSlot, null)
        player.updateInventory()

        this.heldItemSlot = heldItemSlot
        this.pickaxeInHand = itemInHand
    }

    fun setPendingConfirmation(pendingConfirmation: Boolean) {
        this.pendingConfirmation = pendingConfirmation
    }

    override fun getTitle(player: Player): String {
        return ChatColor.GRAY.toString() + "[" + ChatColor.RED + "*" + ChatColor.GRAY + "] " + ChatColor.RED + ChatColor.BOLD + "Salvage Menu " + ChatColor.GRAY + "[" + ChatColor.RED + "*" + ChatColor.GRAY + "]"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()

        // header buttons
        buttons[0] = TokenShopButton()
        buttons[2] = TokenBalanceButton()
        buttons[4] = PickaxeInHandButton(pickaxeInHand.clone()) // make sure to clone the item
        buttons[6] = PurchaseEnchantsMenuButton()
        buttons[8] = ExitButton()

        // footer buttons
        buttons[49] = RedirectRefundEnchantsMenuButton()

        // middle button
        buttons[22] = ConfirmSalvageButton(this)

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) { // don't return pickaxe if pending confirmation (because confirm menu handles the pickaxe from now on)
        if (pendingConfirmation) {
            return
        }

        // return pickaxe to the player's hand if the pickaxe isn't null
        player.inventory.heldItemSlot = heldItemSlot
        player.inventory.setItem(heldItemSlot, pickaxeInHand)
        player.updateInventory()
    }

    private inner class PurchaseEnchantsMenuButton : Button() {

        override fun getName(player: Player): String {
            return ChatColor.GRAY.toString() + "» " + ChatColor.RED + ChatColor.BOLD + "Purchase Enchants" + ChatColor.GRAY + " «"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(ChatColor.GRAY.toString() + "Click here to purchase enchants")
        }

        override fun getMaterial(player: Player): Material {
            return Material.MAGMA_CREAM
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true) // b = ignoreLevelRestrictions (same as addUnsafeEnchantment)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType == ClickType.LEFT) {
                player.closeInventory()
                PurchaseEnchantMenu.tryOpeningMenu(player)
            }
        }

    }

}