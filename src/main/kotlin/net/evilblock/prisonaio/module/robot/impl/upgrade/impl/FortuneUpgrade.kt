package net.evilblock.prisonaio.module.robot.impl.upgrade.impl

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.impl.upgrade.Upgrade
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object FortuneUpgrade : Upgrade {

    override fun getUniqueId(): String {
        return "fortune"
    }

    override fun getName(): String {
        return "Fortune"
    }

    override fun getDescription(): List<String> {
        return listOf(
                "${ChatColor.GRAY}Makes your robot better at identifying",
                "${ChatColor.GRAY}precious ores and gems, which earns you",
                "${ChatColor.GRAY}more money."
        )
    }

    override fun getColor(): ChatColor {
        return ChatColor.AQUA
    }

    override fun getIcon(): ItemStack {
        return ItemBuilder(Material.GOLD_INGOT).build()
    }

    override fun getMaxLevel(): Int {
        return RobotsModule.config.getInt("upgrades.fortune.max-level")
    }

    override fun getPrice(player: Player, tier: Int, level: Int): Double {
        val basePrice = RobotsModule.config.getInt("upgrades.fortune.base-price")
        val basePriceScaling = RobotsModule.config.getInt("upgrades.fortune.base-price-scaling.$tier", 0)
        val multiplier = RobotsModule.config.getDouble("upgrades.fortune.price-multiplier")
        return basePriceScaling + ((basePrice * level) * multiplier)
    }

}