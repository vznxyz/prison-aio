package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager.addEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager.upgradeEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

object Scavenger : AbstractEnchant("scavenger", "Scavenger", 1) {

    override val iconColor: Color
        get() = Color.YELLOW

    override val textColor: ChatColor
        get() = ChatColor.BLUE

    override val menuDisplay: Material
        get() = Material.GOLD_NUGGET

    override fun getCost(level: Int): Long {
        return readCost()
    }

    fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int) {
        if (Chance.percent(readChance())) {
            val enchant: AbstractEnchant = if (Chance.random()) {
                Efficiency
            } else {
                Fortune
            }

            val result = if (enchant.canEnchant(enchantedItem!!)) {
                upgradeEnchant(enchantedItem, enchant, 1, false)
            } else {
                addEnchant(enchantedItem, enchant, 1, false)
            }

            if (result) {
                sendMessage(event.player, "You found " + aOrAn(enchant.enchant) + " level while mining! It has been applied to your pickaxe.")
            }
        }
    }

    private val vowels = listOf('a', 'o', 'u', 'i', 'e')

    private fun aOrAn(text: String?): String {
        if (text == null || text.isEmpty()) {
            return "a $text"
        }

        return if (vowels.contains(text.toLowerCase()[0])) {
            "an $text"
        } else {
            "a $text"
        }
    }

    private fun readCost(): Long {
        return EnchantsModule.config.getLong("scavenger.cost")
    }

    private fun readChance(): Double {
        return EnchantsModule.config.getDouble("scavenger.chance")
    }

}