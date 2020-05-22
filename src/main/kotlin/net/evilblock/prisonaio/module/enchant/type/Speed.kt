package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Speed : AbstractEnchant("speed", "Speed", 10) {

    override val iconColor: Color
        get() = Color.LIME

    override val textColor: ChatColor
        get() = ChatColor.GREEN

    override val menuDisplay: Material
        get() = Material.DIAMOND_BOOTS

    override fun getCost(level: Int): Long {
        return (5000 + (level - 1) * 500).toLong()
    }

    override fun onHold(player: Player, item: ItemStack?, level: Int) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, level - 1))
        player.isSprinting = true
    }

    override fun onUnhold(player: Player) {
        player.removePotionEffect(PotionEffectType.SPEED)
        player.isSprinting = true
    }

}