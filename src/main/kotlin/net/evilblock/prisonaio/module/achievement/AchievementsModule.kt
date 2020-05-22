package net.evilblock.prisonaio.module.achievement

import net.evilblock.prisonaio.module.PluginModule
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