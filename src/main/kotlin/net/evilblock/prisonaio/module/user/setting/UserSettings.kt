/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.serialize.EnchantsSetReferenceSerializer
import net.evilblock.prisonaio.module.user.User
import java.util.*

class UserSettings(@Transient internal var user: User) {

    private val settings: MutableMap<UserSetting, UserSettingOption> = EnumMap(UserSetting::class.java)

    @JsonAdapter(EnchantsSetReferenceSerializer::class)
    private val disabledEnchantMessages: MutableSet<Enchant> = hashSetOf()

    /**
     * Updates the user's setting option for the given [setting].
     */
    fun updateSettingOption(setting: UserSetting, value: UserSettingOption) {
        settings[setting] = value
        user.requiresSave = true
    }

    /**
     * Gets the user's setting option for the given [setting].
     */
    fun getSettingOption(setting: UserSetting): UserSettingOption {
        if (!settings.containsKey(setting)) {
            settings[setting] = setting.newDefaultOption()
        }
        return settings[setting]!!
    }

    /**
     * If the user has the given [enchant]'s messages disabled.
     */
    fun isEnchantMessagesDisabled(enchant: Enchant): Boolean {
        return disabledEnchantMessages.contains(enchant)
    }

    /**
     * Toggles if the user has the given [enchant]'s messages disabled.
     */
    fun toggleEnchantMessages(enchant: Enchant) {
        if (disabledEnchantMessages.contains(enchant)) {
            disabledEnchantMessages.remove(enchant)
        } else {
            disabledEnchantMessages.add(enchant)
        }

        user.requiresSave = true
    }

}