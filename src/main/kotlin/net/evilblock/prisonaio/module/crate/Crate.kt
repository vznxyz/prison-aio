package net.evilblock.prisonaio.module.crate

import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.bukkit.HiddenLore
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Crate(val id: String) {

    var name: String = id
    var keyItemStack: ItemStack = ItemStack(Material.TRIPWIRE_HOOK)
    val rewards: MutableList<CrateReward> = arrayListOf()
    var rewardsRange: IntRange = 1..1
    var reroll: Boolean = false
    var blockType: Material = Material.CHEST
    internal var hologramLines: List<String> = CratesModule.getDefaultHologramLines()

    fun getRawName(): String {
        return ChatColor.stripColor(name)
    }

    fun getHologramLines(): List<String> {
        return hologramLines.map {
            if (TextUtil.isBlank(it)) {
                return@map ""
            }

            it.replace("{crateId}", id)
                .replace("{crateName}", name)
                .replace("{rawCrateName}", getRawName())
                .replace("{minRewards}", rewardsRange.first.toString())
                .replace("{maxRewards}", rewardsRange.last.toString())
        }
    }

    fun isSetup(): Boolean {
        return rewards.isNotEmpty() && rewards.any { it.chance > 0.0 }
    }

    @Throws(IllegalArgumentException::class)
    fun updateMinRewards(min: Int) {
        if (min < 1) {
            throw IllegalStateException("Min cannot be less than 1")
        }

        if (min > rewardsRange.last) {
            throw IllegalStateException("Min cannot be more than max")
        }

        rewardsRange = min..rewardsRange.last
    }

    @Throws(IllegalArgumentException::class)
    fun updateMaxRewards(max: Int) {
        if (max < 1) {
            throw IllegalStateException("Max cannot be less than 1")
        }

        if (max < rewardsRange.first) {
            throw IllegalStateException("Max cannot be less than min")
        }

        rewardsRange = rewardsRange.first..max
    }

    fun toItemStack(): ItemStack {
        return ItemBuilder.of(blockType)
            .name("$name Crate")
            .setLore(listOf(
                HiddenLore.encodeString(id),
                "${ChatColor.GRAY}Place this chest anywhere to",
                "${ChatColor.GRAY}setup a $name ${ChatColor.GRAY}crate."
            ))
            .build()
    }

}