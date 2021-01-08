/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.scoreboard.animation.AnimationFrame
import org.bukkit.ChatColor

object ScoreboardConfigHandler : PluginHandler() {

    private var staticTitle: String = ""
    private var titleFrames: List<AnimationFrame> = arrayListOf()

    override fun getModule(): PluginModule {
        return UsersModule
    }

    override fun initialLoad() {
        super.initialLoad()

        loadConfig()
    }

    fun isTitleAnimated(): Boolean {
        return getModule().config.getBoolean("scoreboard.title.animated", false)
    }

    fun getTitleAnimationFrames(): List<AnimationFrame> {
        return titleFrames
    }

    fun getStaticTitle(): String {
        return staticTitle
    }

    fun loadConfig() {
        staticTitle = ChatColor.translateAlternateColorCodes('&', getModule().config.getString("scoreboard.title.static"))

        val titleAnimation = getModule().config.getList("scoreboard.title.animation") as List<Map<String, Any>>
        titleFrames = titleAnimation.map {
            AnimationFrame(
                text = ChatColor.translateAlternateColorCodes('&', it["text"] as String),
                delay = (it["delay"] as Int).toLong()
            )
        }
    }

}