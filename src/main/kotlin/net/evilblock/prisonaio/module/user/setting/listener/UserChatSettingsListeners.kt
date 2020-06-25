/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.listener

import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.option.ChatModeOption
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object UserChatSettingsListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        val user = UserHandler.getUser(event.player.uniqueId)
        if (user.getSettingOption(UserSetting.CHAT_MODE).getValue<ChatModeOption.ChatMode>() != ChatModeOption.ChatMode.GLOBAL_CHAT) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You can't talk in public chat while you have it disabled. View /settings to configure your chat settings.")
            return
        }

        val recipientsIterator = event.recipients.iterator()
        while (recipientsIterator.hasNext()) {
            val recipient = recipientsIterator.next()
            if (UserHandler.getUser(recipient.uniqueId).getSettingOption(UserSetting.CHAT_MODE).getValue<ChatModeOption.ChatMode>() != ChatModeOption.ChatMode.GLOBAL_CHAT) {
                recipientsIterator.remove()
            }
        }
    }

}