package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.PurchaseEnchantMenu
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.ItemMeta
import java.text.NumberFormat
import java.util.*

class PurchaseEnchantmentButton(private val parent: PurchaseEnchantMenu, private val enchant: AbstractEnchant) : Button() {

    override fun getName(player: Player): String {
        val enchants: Map<AbstractEnchant, Int> = EnchantsManager.getEnchants(parent.pickaxeInHand)
        val currentLevel = enchants[enchant] ?: 0
        return if (currentLevel >= enchant.maxLevel) {
            ChatColor.GREEN.toString() + ChatColor.BOLD + enchant.strippedEnchant
        } else {
            val nextLevel = currentLevel + 1
            ChatColor.GREEN.toString() + ChatColor.BOLD + enchant.strippedEnchant + ChatColor.GRAY + " (Lvl " + currentLevel + " -> " + nextLevel + ")"
        }
    }

    override fun getDescription(player: Player): List<String> {
        val user = UserHandler.getUser(player.uniqueId)
        val enchants: Map<AbstractEnchant, Int> = EnchantsManager.getEnchants(parent.pickaxeInHand)
        val currentLevel = enchants[enchant] ?: 0
        val nextLevel = currentLevel + 1

        val description: MutableList<String> = ArrayList()
        description.add("")

        description.addAll(TextSplitter.split(
            length = 40,
            text = enchant.readDescription(),
            linePrefix = "${ChatColor.GRAY}"
        ))

        description.add("")
        description.add(ChatColor.GRAY.toString() + "Price: " + ChatColor.GREEN + ChatColor.BOLD + NumberFormat.getInstance().format(enchant.getCost(nextLevel)) + " Tokens")
        description.add(ChatColor.GRAY.toString() + "Max Level: " + ChatColor.GOLD + ChatColor.BOLD + NumberFormat.getInstance().format(enchant.maxLevel.toLong()))

        val isMaxed = currentLevel >= enchant.maxLevel
        if (isMaxed) {
            description.add(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Maxed")
        }

        if (user.hasTokensBalance(enchant.getCost(nextLevel))) {
            description.add("")
            description.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Click to purchase")
            description.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Press " + ChatColor.AQUA + ChatColor.BOLD + "Q" + ChatColor.YELLOW + ChatColor.BOLD + " to Buy Max")
        } else {
            if (!isMaxed) {
                description.add("")
                description.add(ChatColor.RED.toString() + ChatColor.BOLD + "Not Enough Tokens")
            }
        }

        return description
    }

    override fun getMaterial(player: Player): Material {
        return Material.FIREWORK_CHARGE
    }

    override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
        val enchants: Map<AbstractEnchant, Int> = EnchantsManager.getEnchants(parent.pickaxeInHand)

        // add glow to item if the player has maxed out this enchantment
        val currentLevel = enchants[enchant] ?: 0
        if (currentLevel >= enchant.maxLevel) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true) // b = ignoreLevelRestrictions (same as addUnsafeEnchantment)
        }

        // hide useless info
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS)
        itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON)
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

        // set color of firework charge
        val fireworkEffectMeta = itemMeta as FireworkEffectMeta
        fireworkEffectMeta.effect = FireworkEffect.builder().withColor(enchant.iconColor).build()

        return fireworkEffectMeta
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (parent.pickaxeInHand == null) {
            return
        }


        val user = UserHandler.getUser(player.uniqueId)
        val enchants: Map<AbstractEnchant, Int> = EnchantsManager.getEnchants(parent.pickaxeInHand)
        val currentLevel = enchants[enchant] ?: 0
        val nextLevel = currentLevel + 1

        // left click and drop click are purchase actions, so we can check a few conditions collectively
        if (clickType == ClickType.LEFT || clickType == ClickType.DROP) {
            if (user.getTokensBalance() < enchant.getCost(nextLevel)) {
                player.sendMessage(EnchantsManager.CHAT_PREFIX + ChatColor.RED + "You don't have enough tokens to purchase the " + ChatColor.BOLD + enchant.strippedEnchant + ChatColor.RED + " enchantment.")
                return
            }

            if (currentLevel >= enchant.maxLevel) {
                player.sendMessage(EnchantsManager.CHAT_PREFIX + ChatColor.RED + "Your pickaxe already has the max level enchant for the " + ChatColor.BOLD + enchant.strippedEnchant + ChatColor.RED + " enchantment.")
                return
            }
        }

        if (clickType == ClickType.LEFT) { // purchase level
            player.sendMessage(EnchantsManager.CHAT_PREFIX.toString() + "Upgraded " + ChatColor.RED + enchant.strippedEnchant + ChatColor.GRAY + " by " + ChatColor.RED + "1" + ChatColor.GRAY + " level.")
            user.updateTokensBalance(user.getTokensBalance() - enchant.getCost(nextLevel))
            EnchantsManager.upgradeEnchant(parent.pickaxeInHand, enchant, 1, false)
        } else if (clickType == ClickType.DROP) { // purchase max levels
            var levelsPurchased = 0
            var levelsCost = 0.0

            for (level in currentLevel + 1 until enchant.maxLevel + 1) {
                val cost = enchant.getCost(level)
                if (user.getTokensBalance() < levelsCost + cost) {
                    break
                }

                levelsPurchased++
                levelsCost += cost
            }

            if (levelsPurchased == 0) {
                player.sendMessage(EnchantsManager.CHAT_PREFIX + ChatColor.RED + "You couldn't afford to purchase any levels.")
                return
            }

            player.sendMessage(EnchantsManager.CHAT_PREFIX + "Upgraded " + ChatColor.RED + enchant.strippedEnchant + ChatColor.GRAY + " by " + ChatColor.RED + levelsPurchased + ChatColor.GRAY + " level" + pluralize(levelsPurchased) + ".")
            user.updateTokensBalance(user.getTokensBalance() - levelsCost.toLong())
            EnchantsManager.upgradeEnchant(parent.pickaxeInHand, enchant, levelsPurchased, false)
        }
    }

    private fun pluralize(num: Int): String {
        return if (num == 1) {
            ""
        } else {
            "s"
        }
    }

}