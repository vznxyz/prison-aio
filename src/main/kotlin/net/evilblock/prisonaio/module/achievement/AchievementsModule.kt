/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.achievement

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.achievement.listener.AchievementProgressListeners
import net.evilblock.prisonaio.module.achievement.type.PrestigeAchievement
import org.bukkit.event.Listener

object AchievementsModule : PluginModule() {

    private val achievements: MutableSet<Achievement> = hashSetOf()

    override fun getName(): String {
        return "Achievements"
    }

    override fun getConfigFileName(): String {
        return "achievements"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        achievements.add(PrestigeAchievement(1))
        achievements.add(PrestigeAchievement(5))
        achievements.add(PrestigeAchievement(10))
        achievements.add(PrestigeAchievement(25))
        achievements.add(PrestigeAchievement(50))
    }

    override fun getListeners(): List<Listener> {
        return listOf(AchievementProgressListeners)
    }

    fun getAchievements(): List<Achievement> {
        return ArrayList(achievements)
    }

}