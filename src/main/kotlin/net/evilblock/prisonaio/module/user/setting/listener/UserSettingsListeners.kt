/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.listener

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.enchant.menu.PurchaseEnchantmentsMenu
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.tool.enchant.type.AbilityEnchant
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.option.PrivateMessageSoundsOption
import net.evilblock.prisonaio.module.user.setting.option.PrivateMessagesOption
import net.evilblock.source.messaging.event.ToggleMessagesEvent
import net.evilblock.source.messaging.event.ToggleSoundsEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object UserSettingsListeners : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || (event.action == Action.RIGHT_CLICK_BLOCK && !Constants.CONTAINER_TYPES.contains(event.clickedBlock.type))) {
            val itemInHand = event.player.inventory.itemInMainHand
            if (MechanicsModule.isPickaxe(itemInHand)) {
                if (AbilityEnchant.isOnGlobalCooldown(event.player)) {
                    return
                }

                val user = UserHandler.getUser(event.player.uniqueId)
                if (user.settings.getSettingOption(UserSetting.QUICK_ACCESS_ENCHANTS).getValue()) {
                    EnchantsManager.handleItemSwitch(event.player, itemInHand, event)

                    val pickaxeData = PickaxeHandler.getPickaxeData(itemInHand)
                    if (pickaxeData != null) {
                        PurchaseEnchantmentsMenu(itemInHand, pickaxeData).openMenu(event.player)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onToggleMessagesEvent(event: ToggleMessagesEvent) {
        val user = UserHandler.getUser(event.uuid)

        if (event.receiving) {
            user.settings.updateSettingOption(UserSetting.PRIVATE_MESSAGES, PrivateMessagesOption(PrivateMessagesOption.OptionValue.RECEIVE_ALL))
        } else {
            user.settings.updateSettingOption(UserSetting.PRIVATE_MESSAGES, PrivateMessagesOption(PrivateMessagesOption.OptionValue.DISABLED))
        }

        user.requiresSave = true
    }

    @EventHandler
    fun onToggleMessagesEvent(event: ToggleSoundsEvent) {
        val user = UserHandler.getUser(event.uuid)

        if (event.playSounds) {
            user.settings.updateSettingOption(UserSetting.PRIVATE_MESSAGE_SOUNDS, PrivateMessageSoundsOption(true))
        } else {
            user.settings.updateSettingOption(UserSetting.PRIVATE_MESSAGE_SOUNDS, PrivateMessageSoundsOption(false))
        }

        user.requiresSave = true
    }

}