package net.evilblock.prisonaio.module.battlepass.challenge

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface ChallengeType {

    fun getName(): String

    fun getDescription(): String

    fun getIcon(): ItemStack

    fun startSetupPrompt(player: Player, id: String, lambda: (Challenge) -> Unit)

}