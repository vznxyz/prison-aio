package net.evilblock.prisonaio.module.robot.impl.upgrade.impl

import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.impl.upgrade.Upgrade
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EfficiencyUpgrade : Upgrade {

    override fun getUniqueId(): String {
        return "efficiency"
    }

    override fun getName(): String {
        return "Efficiency"
    }

    override fun getDescription(): List<String> {
        return TextSplitter.split(text = "Makes your robot mine more efficiently, which means it generates revenue quicker.")
    }

    override fun getColor(): ChatColor {
        return ChatColor.GREEN
    }

    override fun getIcon(): ItemStack {
        return ItemBuilder(Material.DIAMOND_PICKAXE).build()
    }

    override fun getMaxLevel(): Int {
        return RobotsModule.config.getInt("upgrades.efficiency.max-level")
    }

    override fun getPrice(player: Player, tier: Int, level: Int): Double {
        val basePrice = RobotsModule.config.getInt("upgrades.efficiency.base-price")
        val basePriceScaling = RobotsModule.config.getInt("upgrades.efficiency.base-price-scaling.$tier", 0)
        val multiplier = RobotsModule.config.getDouble("upgrades.efficiency.price-multiplier")
        return basePriceScaling + ((basePrice * level) * multiplier)
    }

}