/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.listener

import net.evilblock.prisonaio.module.rank.event.AsyncPlayerPrestigeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object UserPrestigeListeners : Listener {

    /**
     * Rewards the user a prestige token for reaching the next prestige.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onAsyncPlayerPrestigeEvent(event: AsyncPlayerPrestigeEvent) {
        event.user.addPrestigeTokens(event.to - event.from)
    }

}