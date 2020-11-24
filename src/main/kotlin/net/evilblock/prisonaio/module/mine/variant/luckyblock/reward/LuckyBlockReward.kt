/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.reward

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.mine.MinesModule
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class LuckyBlockReward {

    var name: String = "Default name"
    var itemStack: ItemStack = ItemStack(Material.DIAMOND)
    var giveItem: Boolean = false
    var commands: MutableList<String> = arrayListOf()
    var chance: Double = 0.0

    open fun execute(player: Player) {
        player.sendMessage("${MinesModule.getLuckyBlockChatPrefix()}You have won the $name${ChatColor.GRAY} reward!")

        for (command in commands) {
            val processed = translate(command)
                .replace("{playerName}", player.name)
                .replace("{playerDisplayName}", player.displayName)
                .replace("{playerUuid}", player.uniqueId.toString())

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processed)
        }
    }

    open fun translate(text: String): String {
        return text
            .replace("{rewardName}", name)
            .replace("{chance}", NumberUtils.format(chance))
    }

}