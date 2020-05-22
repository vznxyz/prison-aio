package net.evilblock.prisonaio.module.enchant.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.button.*
import net.evilblock.prisonaio.module.enchant.type.*
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

class PurchaseEnchantMenu(player: Player) : Menu() {

    private val heldItemSlot: Int
    val pickaxeInHand: ItemStack?

    init {
        this.placeholder = true

        // get item in hand
        val itemInHand = player.inventory.getItem(player.inventory.heldItemSlot) ?: throw IllegalStateException("You have no item in your hand.")

        // make sure item in hand isn't null
        // check if the item is a pickaxe
        check(itemInHand.type.toString().endsWith("_PICKAXE")) { "You must be holding the pickaxe you would like to enchant." }

        val heldItemSlot = player.inventory.heldItemSlot
        // remove item from player's inventory
        player.inventory.setItem(heldItemSlot, null)
        player.updateInventory()

        this.heldItemSlot = heldItemSlot
        this.pickaxeInHand = itemInHand
    }

    override fun getTitle(player: Player): String {
        return ChatColor.GRAY.toString() + "[" + ChatColor.RED + "*" + ChatColor.GRAY + "] " + ChatColor.RED + ChatColor.BOLD + "Enchants Menu " + ChatColor.GRAY + "[" + ChatColor.RED + "*" + ChatColor.GRAY + "]"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()

        // header buttons
        buttons[0] = TokenShopButton()
        buttons[2] = TokenBalanceButton()
        buttons[4] = PickaxeInHandButton(pickaxeInHand!!.clone()) // make sure to clone the item
        buttons[6] = SalvagePickaxeButton()
        buttons[8] = ExitButton()

        // footer buttons
        buttons[49] = RedirectRefundEnchantsMenuButton()

        // left column
        buttons[10] = PurchaseEnchantmentButton(this, MineBomb)
        buttons[11] = PurchaseEnchantmentButton(this, Explosive)
        buttons[19] = PurchaseEnchantmentButton(this, Efficiency)
        buttons[20] = PurchaseEnchantmentButton(this, Unbreaking)
        buttons[28] = PurchaseEnchantmentButton(this, Speed)
        buttons[29] = PurchaseEnchantmentButton(this, Luck)
        buttons[37] = PurchaseEnchantmentButton(this, Jump)
        buttons[38] = PurchaseEnchantmentButton(this, Haste)

        // middle column
        buttons[13] = PurchaseEnchantmentButton(this, JackHammer)
        buttons[22] = PurchaseEnchantmentButton(this, Exporter)
        buttons[31] = PurchaseEnchantmentButton(this, Fortune)
        buttons[40] = PurchaseEnchantmentButton(this, Nuke)

        // right column
        buttons[15] = PurchaseEnchantmentButton(this, Tokenator)
        buttons[16] = PurchaseEnchantmentButton(this, Locksmith)
        buttons[24] = PurchaseEnchantmentButton(this, TokenPouch)
        buttons[25] = PurchaseEnchantmentButton(this, LuckyMoney)
        buttons[33] = PurchaseEnchantmentButton(this, Greed)
        buttons[34] = PurchaseEnchantmentButton(this, Scavenger)
        buttons[42] = PurchaseEnchantmentButton(this, Laser)

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        // add the pickaxe back to the player's hand ONLY IF the pickaxe isn't null
        if (pickaxeInHand != null) {
            player.inventory.heldItemSlot = heldItemSlot
            player.inventory.setItem(heldItemSlot, pickaxeInHand)
            player.updateInventory()
        }
    }

    class SalvagePickaxeButton : Button() {

        override fun getName(player: Player): String {
            return ChatColor.GRAY.toString() + "» " + ChatColor.RED + ChatColor.BOLD + "Salvage Pickaxe" + ChatColor.GRAY + " «"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(ChatColor.GRAY.toString() + "Click here to view your pickaxe's salvage options")
        }

        override fun getMaterial(player: Player): Material {
            return Material.ANVIL
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true) // b = ignoreLevelRestrictions (same as addUnsafeEnchantment)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType == ClickType.LEFT) {
                player.closeInventory()
                SalvagePickaxeMenu.tryOpeningMenu(player)
            }
        }

    }

    companion object {
        @JvmStatic
        fun tryOpeningMenu(player: Player) {
            try {
                PurchaseEnchantMenu(player).openMenu(player)
            } catch (e: IllegalStateException) {
                player.sendMessage(EnchantsManager.CHAT_PREFIX + ChatColor.RED + e.message)
            }
        }
    }

}