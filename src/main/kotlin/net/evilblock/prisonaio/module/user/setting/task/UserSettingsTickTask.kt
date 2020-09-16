/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.task

import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting

object UserSettingsTickTask : Runnable {

    override fun run() {
        for (user in UserHandler.getUsers()) {
            if (!user.settings.getSettingOption(UserSetting.AUTO_RANKUP).getValue<Boolean>()) {
                continue
            }

            val player = user.getPlayer() ?: continue
            user.purchaseMaxRankups(player, manual = false)
        }
    }

}