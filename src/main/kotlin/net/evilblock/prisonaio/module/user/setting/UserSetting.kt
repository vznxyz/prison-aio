package net.evilblock.prisonaio.module.user.setting

import net.evilblock.prisonaio.module.user.setting.option.ChatModeOption
import net.evilblock.prisonaio.module.user.setting.option.ChatModeOption.ChatMode
import net.evilblock.prisonaio.module.user.setting.option.PrivateMessagesOption
import net.evilblock.prisonaio.module.user.setting.option.CommentsRestrictionOption
import net.evilblock.prisonaio.module.user.setting.option.CommentsRestrictionOption.RestrictionOptionValue
import net.evilblock.prisonaio.module.user.setting.option.ScoreboardVisibilityOption
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
        defaultValue = { ScoreboardVisibilityOption(true) },
        options = {
            arrayListOf(
                ScoreboardVisibilityOption(true),
                ScoreboardVisibilityOption(false)
            )
        }
    ),
    CHAT_MODE(
        displayName = "Chat Mode",
        description = "This setting controls what chat mode you'll receive messages from.",
        icon = ItemStack(Material.SIGN),
        defaultValue = { ChatModeOption(ChatMode.GLOBAL_CHAT) },
        options = {
            arrayListOf(
                ChatModeOption(ChatMode.HIDDEN),
                ChatModeOption(ChatMode.SYSTEM_CHAT),
                ChatModeOption(ChatMode.GLOBAL_CHAT)
            )
        }
    ),
    PRIVATE_MESSAGES(
        displayName = "Receive Private Messages",
        description = "This setting controls if other players can you send private messages.",
        icon = ItemStack(Material.BOOK_AND_QUILL),
        defaultValue = { PrivateMessagesOption(PrivateMessagesOption.OptionValue.RECEIVE_ALL) },
        options = {
            arrayListOf(
                PrivateMessagesOption(PrivateMessagesOption.OptionValue.RECEIVE_ALL),
                PrivateMessagesOption(PrivateMessagesOption.OptionValue.RECEIVE_INITIATED),
                PrivateMessagesOption(PrivateMessagesOption.OptionValue.DISABLED)
            )
        }
    ),
    PROFILE_COMMENTS_RESTRICTION(
        displayName = "Allow Profile Comments",
        description = "This setting controls if other players are allowed to post comments on your profile.",
        icon = ItemStack(Material.EMPTY_MAP),
        defaultValue = { CommentsRestrictionOption(RestrictionOptionValue.ALLOWED) },
        options = {
            arrayListOf(
                CommentsRestrictionOption(RestrictionOptionValue.ALLOWED),
                CommentsRestrictionOption(RestrictionOptionValue.DISABLED)
            )
        }
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