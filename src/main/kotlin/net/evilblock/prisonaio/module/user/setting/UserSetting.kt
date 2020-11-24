/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.setting.option.*
import net.evilblock.prisonaio.module.user.setting.option.ChatModeOption.ChatMode
import net.evilblock.prisonaio.module.user.setting.option.CommentsRestrictionOption.RestrictionOptionValue
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class UserSetting(
    private val displayName: String,
    private val description: String,
    val icon: (UserSettingOption) -> ItemStack,
    val defaultOption: () -> UserSettingOption,
    private val options: () -> List<UserSettingOption>,
    val onUpdate: (User, UserSettingOption) -> Unit = { _, _ -> }
) {

    SCOREBOARD_VISIBILITY(
        displayName = "Scoreboard Visibility",
        description = "This setting controls if the scoreboard is visible.",
        icon = { ItemStack(Material.ITEM_FRAME) },
        defaultOption = { ScoreboardVisibilityOption(true) },
        options = {
            arrayListOf(
                ScoreboardVisibilityOption(true),
                ScoreboardVisibilityOption(false)
            )
        }
    ),
    SCOREBOARD_STYLE(
        displayName = "Scoreboard Style",
        description = "This setting controls the style of the scoreboard.",
        icon = { ItemStack(Material.PAINTING) },
        defaultOption = { ScoreboardStyleOption(ScoreboardStyleOption.ScoreboardStyle.FANCY) },
        options = {
            arrayListOf(
                ScoreboardStyleOption(ScoreboardStyleOption.ScoreboardStyle.SIMPLE),
                ScoreboardStyleOption(ScoreboardStyleOption.ScoreboardStyle.FANCY)
            )
        }
    ),
    RAINBOW_SCOREBOARD(
        displayName = "Rainbow Scoreboard",
        description = "Cycles the scoreboard's primary color through the rainbow colors.",
        icon = { ItemStack(Material.RECORD_4) },
        defaultOption = { RainbowScoreboardOption(false) },
        options = {
            arrayListOf(
                RainbowScoreboardOption(true),
                RainbowScoreboardOption(false)
            )
        }
    ),
    CHAT_MODE(
        displayName = "Chat Mode",
        description = "This setting controls what chat mode you'll receive messages from.",
        icon = { option ->
            if ((option.getValue() as ChatMode) == ChatMode.HIDDEN) {
                ItemUtils.makeTexturedSkull(Constants.IB_MUTED_TEXTURE)
            } else {
                ItemUtils.makeTexturedSkull(Constants.IB_TALKING_TEXTURE)
            }
        },
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
        displayName = "Private Messages",
        description = "This setting controls if other players can you send private messages.",
        icon = { option ->
            if ((option.getValue() as PrivateMessagesOption.OptionValue) == PrivateMessagesOption.OptionValue.DISABLED) {
                ItemUtils.makeTexturedSkull(Constants.IB_CHAT_FORBIDDEN_TEXTURE)
            } else {
                ItemUtils.makeTexturedSkull(Constants.IB_CHAT_TEXTURE)
            }
        },
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
        displayName = "Private Message Sounds",
        description = "This setting controls if sounds will play when you receive private messages.",
        icon = { option ->
            if (option.getValue()) {
                ItemUtils.makeTexturedSkull(Constants.IB_ALARM_ON_TEXTURE)
            } else {
                ItemUtils.makeTexturedSkull(Constants.IB_ALARM_OFF_TEXTURE)
            }
        },
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
        icon = { option ->
            if ((option.getValue() as RestrictionOptionValue) == RestrictionOptionValue.ALLOWED) {
                ItemUtils.makeTexturedSkull(Constants.IB_UNLOCKED_TEXTURE)
            } else {
                ItemUtils.makeTexturedSkull(Constants.IB_LOCKED_TEXTURE)
            }
        },
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
        icon = { ItemStack(Material.ENDER_PEARL) },
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
        icon = { ItemStack(Material.EXP_BOTTLE) },
        defaultOption = { AutoRankupOption(false) },
        options = {
            arrayListOf(
                AutoRankupOption(true),
                AutoRankupOption(false)
            )
        }
    ),
    QUICK_ACCESS_ENCHANTS(
        displayName = "Enchant Quick Access",
        description = "This setting controls if the enchant menu should open when right-clicking with a pickaxe in hand.",
        icon = { ItemStack(Material.LEVER) },
        defaultOption = { QuickAccessEnchantsOption(true) },
        options = {
            arrayListOf(
                QuickAccessEnchantsOption(true),
                QuickAccessEnchantsOption(false)
            )
        }
    ),
    ENCHANT_MESSAGES(
        displayName = "Enchantment Messages",
        description = "This setting controls if you will receive messages in chat related to enchantment abilities.",
        icon = { ItemStack(Material.ENCHANTMENT_TABLE) },
        defaultOption = { EnchantmentMessagesOption(true) },
        options = {
            arrayListOf(
                EnchantmentMessagesOption(true),
                EnchantmentMessagesOption(false)
            )
        }
    ),
    REWARD_MESSAGES(
        displayName = "Reward Messages",
        description = "This setting controls if you will receive messages in chat related to rewards, such as: MineCrates, Gang Trophies, etc;",
        icon = { option ->
            if (option.getValue()) {
                ItemUtils.makeTexturedSkull(Constants.IB_CHEST_OPEN_TEXTURE)
            } else {
                ItemUtils.makeTexturedSkull(Constants.IB_CHEST_LOOTED_TEXTURE)
            }
        },
        defaultOption = { RewardMessagesOption(true) },
        options = {
            arrayListOf(
                RewardMessagesOption(true),
                RewardMessagesOption(false)
            )
        }
    ),
    ROBOT_HOLOGRAMS(
        displayName = "Robot Holograms",
        description = "This setting controls if the hologram that appears above robots will be displayed to you.",
        icon = { ItemStack(Material.ARMOR_STAND) },
        defaultOption = { RobotHologramsOption(true) },
        options = {
            arrayListOf(
                RobotHologramsOption(true),
                RobotHologramsOption(false)
            )
        }
    ),
    TRADE_REQUESTS(
        displayName = "Trade Requests",
        description = "This setting controls if you can receive trade requests from other players.",
        icon = { ItemStack(Material.DIAMOND_BARDING) },
        defaultOption = { TradeRequestsOption(true) },
        options = {
            arrayListOf(
                TradeRequestsOption(true),
                TradeRequestsOption(false)
            )
        }
    ),
    SHOP_RECEIPTS(
        displayName = "Shop Receipts",
        description = "This setting controls if you will be displayed a shop receipt whenever you sell items to a shop.",
        icon = { option ->
            if (option.getValue()) {
                ItemUtils.makeTexturedSkull(Constants.IB_MUSIC_NOTE_TEXTURE)
            } else {
                ItemUtils.makeTexturedSkull(Constants.IB_MUSIC_NOTE_GRAY_TEXTURE)
            }
        },
        defaultOption = { ShopReceiptsOption(true) },
        options = {
            arrayListOf(
                ShopReceiptsOption(true),
                ShopReceiptsOption(false)
            )
        }
    ),
    TOKEN_SHOP_NOTIFICATIONS(
        displayName = "Token Shop Notifications",
        description = "This setting controls if you will be notified when somebody buys/sells to your token shop.",
        icon = { ItemUtils.makeTexturedSkull(Constants.IB_FORWARD_TEXTURE) },
        defaultOption = { TokenShopNotificationsOption(true) },
        options = {
            arrayListOf(
                TokenShopNotificationsOption(true),
                TokenShopNotificationsOption(false)
            )
        }
    ),
    GANG_QUICK_CHAT(
        displayName = "Gang Quick Chat",
        description = "This setting controls if your regular chat messages are processed as gang chat messages, without having to put \"@\" at the beginning.",
        icon = { option ->
            if (option.getValue()) {
                ItemUtils.makeTexturedSkull(Constants.IB_TEAM_TEXTURE)
            } else {
                ItemUtils.makeTexturedSkull(Constants.IB_TEAM_GRAY_TEXTURE)
            }
        },
        defaultOption = { GangQuickChatOption(false) },
        options = {
            arrayListOf(
                GangQuickChatOption(true),
                GangQuickChatOption(false)
            )
        }
    ),
    GRAND_EXCHANGE_NOTIFICATIONS(
        displayName = "Grand Exchange Notifications",
        description = "This setting controls if you will receive notifications about Grand Exchange listings you're involved in. If this setting is disabled, the other Grand Exchange settings will have no effect.",
        icon = { option ->
            if (option.getValue()) {
                ItemUtils.makeTexturedSkull(Constants.IB_UNLOCKED_TEXTURE)
            } else {
                ItemUtils.makeTexturedSkull(Constants.IB_LOCKED_TEXTURE)
            }
        },
        defaultOption = { GENotificationsOption(true) },
        options = {
            arrayListOf(
                GENotificationsOption(true),
                GENotificationsOption(false)
            )
        }
    ),
    GRAND_EXCHANGE_OUTBID_NOTIFICATIONS(
        displayName = "Grand Exchange Outbid Notifications",
        description = "This setting controls if you will receive a notification when you've been outbid in a Grand Exchange auction. Has no effect if \"Grand Exchange Notifications\" is disabled.",
        icon = { ItemUtils.makeTexturedSkull(Constants.IB_WARNING_TEXTURE) },
        defaultOption = { GEOutbidNotificationOption(true) },
        options = {
            arrayListOf(
                GEOutbidNotificationOption(true),
                GEOutbidNotificationOption(false)
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