/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting

import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.setting.option.ChatModeOption
import net.evilblock.prisonaio.module.user.setting.option.ChatModeOption.ChatMode
import net.evilblock.prisonaio.module.user.setting.option.CommentsRestrictionOption
import net.evilblock.prisonaio.module.user.setting.option.CommentsRestrictionOption.RestrictionOptionValue
import net.evilblock.prisonaio.module.user.setting.option.PrivateMessagesOption
import net.evilblock.prisonaio.module.user.setting.option.PrivateMessageSoundsOption
import net.evilblock.prisonaio.module.user.setting.option.ScoreboardVisibilityOption
import net.evilblock.prisonaio.module.user.setting.option.SneakToTeleportOption
import net.evilblock.prisonaio.module.user.setting.option.AutoRankupOption
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class UserSetting(
    private val displayName: String,
    private val description: String,
    private val icon: ItemStack,
    val defaultOption: () -> UserSettingOption,
    private val options: () -> List<UserSettingOption>,
    val onUpdate: (User, UserSettingOption) -> Unit = { _, _ -> }
) {

    SCOREBOARD_VISIBILITY(
        displayName = "Scoreboard Visibility",
        description = "This setting controls if the scoreboard is visible.",
        icon = ItemStack(Material.ITEM_FRAME),
        defaultOption = { ScoreboardVisibilityOption(true) },
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
        defaultOption = { ChatModeOption(ChatMode.GLOBAL_CHAT) },
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
        defaultOption = { PrivateMessagesOption(PrivateMessagesOption.OptionValue.RECEIVE_ALL) },
        options = {
            arrayListOf(
                PrivateMessagesOption(PrivateMessagesOption.OptionValue.RECEIVE_ALL),
                PrivateMessagesOption(PrivateMessagesOption.OptionValue.RECEIVE_INITIATED),
                PrivateMessagesOption(PrivateMessagesOption.OptionValue.DISABLED)
            )
        },
        onUpdate = { user, option ->
            when (option.getValue<PrivateMessagesOption.OptionValue>()) {
                PrivateMessagesOption.OptionValue.RECEIVE_ALL -> {
                    MessagingManager.toggleMessages(user.uuid, true)
                }
                PrivateMessagesOption.OptionValue.RECEIVE_INITIATED -> {
                    MessagingManager.toggleMessages(user.uuid, true)
                }
                PrivateMessagesOption.OptionValue.DISABLED -> {
                    MessagingManager.toggleMessages(user.uuid, false)
                }
            }
        }
    ),
    PRIVATE_MESSAGE_SOUNDS(
        displayName = "Play Private Message Sounds",
        description = "This setting controls if sounds will play when you receive private messages.",
        icon = ItemStack(Material.NOTE_BLOCK),
        defaultOption = { PrivateMessageSoundsOption(true) },
        options = {
            arrayListOf(
                PrivateMessageSoundsOption(true),
                PrivateMessageSoundsOption(false)
            )
        },
        onUpdate = { user, option ->
            if (option.getValue()) {
                MessagingManager.toggleSounds(user.uuid, true)
            } else {
                MessagingManager.toggleSounds(user.uuid, false)
            }
        }
    ),
    PROFILE_COMMENTS_RESTRICTION(
        displayName = "Allow Profile Comments",
        description = "This setting controls if other players are allowed to post comments on your profile.",
        icon = ItemStack(Material.EMPTY_MAP),
        defaultOption = { CommentsRestrictionOption(RestrictionOptionValue.ALLOWED) },
        options = {
            arrayListOf(
                CommentsRestrictionOption(RestrictionOptionValue.ALLOWED),
                CommentsRestrictionOption(RestrictionOptionValue.DISABLED)
            )
        }
    ),
    SNEAK_TO_TELEPORT(
        displayName = "Sneak to Teleport",
        description = "This setting controls if pressing your sneak button will teleport you to the mine's spawn when you have a full inventory.",
        icon = ItemStack(Material.ENDER_PEARL),
        defaultOption = { SneakToTeleportOption(true) },
        options = {
            arrayListOf(
                SneakToTeleportOption(true),
                SneakToTeleportOption(false)
            )
        }
    ),
    AUTO_RANKUP(
        displayName = "Auto Rankup",
        description = "This setting controls if rankups will be automatically purchased when you have enough funds to afford it.",
        icon = ItemStack(Material.EXP_BOTTLE),
        defaultOption = { AutoRankupOption(true) },
        options = {
            arrayListOf(
                AutoRankupOption(true),
                AutoRankupOption(false)
            )
        }
    );

    private var cached: UserSettingOption = this.defaultOption()

    fun getDisplayName(): String {
        return displayName
    }

    fun getDescription(): String {
        return description
    }

    fun getIcon(): ItemStack {
        return icon
    }

    fun getDefaultOption(): UserSettingOption {
        return cached
    }

    fun <T : UserSettingOption> newDefaultOption(): T {
        return this.defaultOption() as T
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