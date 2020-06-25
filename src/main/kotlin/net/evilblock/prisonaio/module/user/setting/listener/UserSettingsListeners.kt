/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.listener

import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.option.PrivateMessageSoundsOption
import net.evilblock.prisonaio.module.user.setting.option.PrivateMessagesOption
import net.evilblock.source.messaging.event.ToggleMessagesEvent
import net.evilblock.source.messaging.event.ToggleSoundsEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object UserSettingsListeners : Listener {

    @EventHandler
    fun onToggleMessagesEvent(event: ToggleMessagesEvent) {
        val user = UserHandler.getUser(event.uuid)

        if (event.receiving) {
            user.updateSettingOption(UserSetting.PRIVATE_MESSAGES, PrivateMessagesOption(PrivateMessagesOption.OptionValue.RECEIVE_ALL))
        } else {
            user.updateSettingOption(UserSetting.PRIVATE_MESSAGES, PrivateMessagesOption(PrivateMessagesOption.OptionValue.DISABLED))
        }

        user.requiresSave = true
    }

    @EventHandler
    fun onToggleMessagesEvent(event: ToggleSoundsEvent) {
        val user = UserHandler.getUser(event.uuid)

        if (event.playSounds) {
            user.updateSettingOption(UserSetting.PRIVATE_MESSAGE_SOUNDS, PrivateMessageSoundsOption(true))
        } else {
            user.updateSettingOption(UserSetting.PRIVATE_MESSAGE_SOUNDS, PrivateMessageSoundsOption(false))
        }

        user.requiresSave = true
    }

}