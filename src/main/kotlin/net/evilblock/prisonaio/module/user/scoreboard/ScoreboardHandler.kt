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
import net.evilblock.prisonaio.module.user.scoreboard.slot.MinePartyAdvertisementSlot
import net.evilblock.prisonaio.module.user.scoreboard.slot.MinePartyPlayingSlot
import net.evilblock.prisonaio.module.user.scoreboard.slot.RankupSlot
import net.evilblock.prisonaio.module.user.scoreboard.slot.TeleportSlot
import org.bukkit.ChatColor
import java.util.concurrent.ConcurrentHashMap

object ScoreboardHandler : PluginHandler() {

    private var staticTitle: String = ""
    private var titleFrames: List<AnimationFrame> = arrayListOf()

    private val slots: MutableSet<ScoreboardSlot> = ConcurrentHashMap.newKeySet<ScoreboardSlot>().also {
        it.add(RankupSlot)
        it.add(TeleportSlot)
        it.add(MinePartyPlayingSlot)
        it.add(MinePartyAdvertisementSlot)
    }

    override fun getModule(): PluginModule {
        return UsersModule
    }

    override fun initialLoad() {
        super.initialLoad()

        loadConfig()

        loaded = true
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

    fun getSlots(): Set<ScoreboardSlot> {
        return slots
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