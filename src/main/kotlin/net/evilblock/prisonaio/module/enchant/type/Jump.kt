package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Jump : AbstractEnchant("jump", "Jump", 10) {

    override val iconColor: Color
        get() = Color.LIME

    override val textColor: ChatColor
        get() = ChatColor.GREEN

    override fun onHold(player: Player, item: ItemStack?, level: Int) {
        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.JUMP,
                Int.MAX_VALUE,
                level - 1
            )
        )
    }

    override fun onUnhold(player: Player) {
        player.removePotionEffect(PotionEffectType.JUMP)
    }

    override fun getCost(level: Int): Long {
        return (5200 + (level - 1) * 200).toLong()
    }

    override val menuDisplay: Material
        get() = Material.FEATHER
}