package net.evilblock.prisonaio.module.reward.minecrate.reward

import net.evilblock.cubed.util.Chance
import org.bukkit.Bukkit
import org.bukkit.entity.Player

data class MineCrateRewardSet(
    val id: String,
    val chance: Double,
    val maxRewards: Int
) {

    val items: MutableList<RewardItem> = arrayListOf()

    fun addReward(name: String, chance: Double, commands: List<String>) {
        items.add(RewardItem(name, chance, commands))
    }

    fun pickRewards(): List<RewardItem> {
        val pickedItems = arrayListOf<RewardItem>()

        if (items.isEmpty()) {
            return pickedItems
        }

        while (pickedItems.size < maxRewards) {
            val randomItem = items.random()
            if (Chance.percent(randomItem.chance)) {
                pickedItems.add(randomItem)
            }
        }

        return pickedItems
    }

    inner class RewardItem(
        val name: String,
        val chance: Double,
        val commands: List<String>
    ) {

        fun execute(player: Player) {
            for (rawCommand in commands) {
                val command = rawCommand
                    .replace("{playerName}", player.name)
                    .replace("{playerUuid}", player.uniqueId.toString())

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
            }
        }

    }

}