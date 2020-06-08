package net.evilblock.prisonaio.module.battlepass.tier.reward

import net.evilblock.prisonaio.module.battlepass.tier.Tier
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Reward(@Transient internal var tier: Tier) {

    internal var textLines: MutableList<String> = arrayListOf()
    internal var commands: MutableList<String> = arrayListOf()

    fun getTextLines(): List<String> {
        return textLines.toList()
    }

    fun getCommands(): List<String> {
        return commands.toList()
    }

    fun isFreeReward(): Boolean {
        return tier.freeReward == this
    }

    fun execute(player: Player) {
        for (command in commands) {
            val processedCommand = command
                .replace("{playerName}", player.name)
                .replace("{playerUuid}", player.uniqueId.toString())
                .replace("{tier}", tier.number.toString())

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand)
        }
    }

}