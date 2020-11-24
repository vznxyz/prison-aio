/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.listener

import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.option.GangQuickChatOption
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object GangChatListeners : Listener {

    @EventHandler
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        val quickChatEnabled = UserHandler.getUser(event.player.uniqueId).settings.getSettingOption(UserSetting.GANG_QUICK_CHAT).getValue() as Boolean
        val quickChatTrigger = event.message.startsWith("@")

        if (quickChatEnabled || quickChatTrigger) {
            val accessibleGangs = GangHandler.getAccessibleGangs(event.player.uniqueId)
            if (accessibleGangs.size == 1) {
                handleMessage(event, accessibleGangs.first(), quickChatTrigger)
                return
            }

            val visitingGang = GangHandler.getVisitingGang(event.player)
            if (visitingGang != null) {
                handleMessage(event, visitingGang, quickChatTrigger)
            } else {
                if (quickChatEnabled) {
                    UserHandler.getUser(event.player.uniqueId).settings.updateSettingOption(UserSetting.GANG_QUICK_CHAT, GangQuickChatOption(false))
                }
            }
        }
    }

    private fun handleMessage(event: AsyncPlayerChatEvent, gang: Gang, removePrefix: Boolean) {
        event.isCancelled = true

        val message = if (removePrefix) {
            event.message.drop(1)
        } else {
            event.message
        }

        val finalMessage = StringBuilder()
            .append("${ChatColor.GRAY}[${ChatColor.YELLOW}${gang.name}${ChatColor.GRAY}] ")
            .append("${ChatColor.GREEN}${event.player.name}${ChatColor.GRAY}: $message")
            .toString()

        gang.sendMessagesToAll(finalMessage)
    }

}