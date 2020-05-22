package net.evilblock.prisonaio.module.user.setting

import net.evilblock.prisonaio.module.user.setting.chat.ChatMode
import net.evilblock.prisonaio.module.user.setting.option.ChatModeSettingOption
import net.evilblock.prisonaio.module.user.setting.option.ScoreboardVisibilitySettingOption
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class UserSetting(
    private val displayName: String,
    private val description: String,
    private val icon: ItemStack,
    val defaultValue: () -> UserSettingOption,
    private val options: () -> List<UserSettingOption>
) {

    SCOREBOARD_VISIBILITY(
        displayName = "Scoreboard Visibility",
        description = "This setting controls if the scoreboard is visible.",
        icon = ItemStack(Material.ITEM_FRAME),
        defaultValue = { ScoreboardVisibilitySettingOption(true) },
        options = { arrayListOf(ScoreboardVisibilitySettingOption(true), ScoreboardVisibilitySettingOption(false)) }
    ),
    CHAT_MODE(
        displayName = "Chat Mode",
        description = "This setting controls what chat mode you'll receive messages from.",
        icon = ItemStack(Material.SIGN),
        defaultValue = { ChatModeSettingOption(ChatMode.GLOBAL_CHAT) },
        options = { arrayListOf(ChatModeSettingOption(ChatMode.HIDDEN), ChatModeSettingOption(ChatMode.SYSTEM_CHAT), ChatModeSettingOption(ChatMode.GLOBAL_CHAT)) }
    );

    fun getDisplayName(): String {
        return displayName
    }

    fun getDescription(): String {
        return description
    }

    fun getIcon(): ItemStack {
        return icon
    }

    fun <T : UserSettingOption> getOptions(): List<T> {
        return options.invoke() as List<T>
    }

    fun <T : UserSettingOption> getPreviousValue(current: T): T {
        val newOptions = options.invoke()
        if (newOptions.first() == current) {
            return newOptions.last() as T
        }

        val indexOf = newOptions.indexOf(current)
        if (indexOf == -1) {
            return newOptions.first() as T
        }

        if (indexOf == 0) {
            return newOptions.last() as T
        }

        return newOptions[indexOf - 1] as T
    }

    fun <T : UserSettingOption> getNextValue(current: T): T {
        val newOptions = options.invoke()
        if (newOptions.last() == current) {
            return newOptions.first() as T
        }

        val indexOf = newOptions.indexOf(current)
        if (indexOf == -1 || indexOf >= newOptions.size) {
            return newOptions.first() as T
        }

        return newOptions[indexOf + 1] as T
    }

}