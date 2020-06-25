/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.achievement

import net.evilblock.prisonaio.module.rank.event.AsyncPlayerPrestigeEvent
import net.evilblock.prisonaio.module.rank.event.PlayerRankupEvent
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack

abstract class Achievement(
    /**
     * The unique ID of this achievement, which is used to determine if a player has completed an achievement
     */
    val id: String,
    /**
     * The description of this achievement
     */
    val description: String = "This achievement does not have a description",
    /**
     * The sort order of this achievement, which determines the order of where this achievement icon will be displayed in menus
     */
    val sortOrder: Int = 0
) {

    abstract fun getDisplayName(): String

    abstract fun getDisplayIcon(): ItemStack

    open fun onBlockBreak(event: BlockBreakEvent) {}

    open fun onBlockPlace(event: BlockPlaceEvent) {}

    open fun onPlayerRankup(event: PlayerRankupEvent) {}

    open fun onPlayerPrestige(event: AsyncPlayerPrestigeEvent) {}

    fun completedAchievement(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        user.markAchievementCompleted(this)

        player.sendMessage("${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Achievements${ChatColor.GRAY}] ${ChatColor.GRAY}Congratulations on achieving ${ChatColor.RED}${getDisplayName()}${ChatColor.GRAY}!")
    }

}