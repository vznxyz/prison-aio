package net.evilblock.prisonaio.module.achievement.listener

import net.evilblock.prisonaio.module.achievement.AchievementsModule
import net.evilblock.prisonaio.module.rank.event.PlayerPrestigeEvent
import net.evilblock.prisonaio.module.rank.event.PlayerRankupEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

object AchievementProgressListeners : Listener {

    private fun getAchievementsModule(): AchievementsModule {
        return AchievementsModule
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        getAchievementsModule().getAchievements().forEach { achievement -> achievement.onBlockBreak(event) }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        getAchievementsModule().getAchievements().forEach { achievement -> achievement.onBlockPlace(event) }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerRankupEvent(event: PlayerRankupEvent) {
        getAchievementsModule().getAchievements().forEach { achievement -> achievement.onPlayerRankup(event) }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerPrestigeEvent(event: PlayerPrestigeEvent) {
        getAchievementsModule().getAchievements().forEach { achievement -> achievement.onPlayerPrestige(event) }
    }

}