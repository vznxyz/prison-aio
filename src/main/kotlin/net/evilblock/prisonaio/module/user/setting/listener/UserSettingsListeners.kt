/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.listener

import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.tool.enchant.type.AbilityEnchant
import net.evilblock.prisonaio.module.tool.pickaxe.menu.PickaxeMenu
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

    /**
     * Handles the Pickaxe Menu Quick Access setting functionality.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || (event.action == Action.RIGHT_CLICK_BLOCK && !Constants.CONTAINER_TYPES.contains(event.clickedBlock.type))) {
            val itemInHand = event.player.inventory.itemInMainHand

            if (MechanicsModule.isPickaxe(itemInHand)) {
                if (AbilityEnchant.isOnGlobalCooldown(event.player)) {
                    return
                }

                // prevent menu from opening when on global cooldown
                if (AbilityEnchant.isOnGlobalCooldown(event.player)) {
                    return
                }

                // prevent menu from opening if the player is probably trying to use an ability
                val checkLocation = if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    event.clickedBlock.location
                } else {
                    event.player.location
                }

                val pickaxeData = PickaxeHandler.getPickaxeData(itemInHand)

                val region = RegionHandler.findRegion(checkLocation)
                if (region.supportsAbilityEnchants()) {
                    if (pickaxeData?.enchants?.any { it.key is AbilityEnchant } == true) {
                        return
                    }
                }

                val user = UserHandler.getUser(event.player.uniqueId)
                if (user.settings.getSettingOption(UserSetting.PICKAXE_MENU_QUICK_ACCESS).getValue()) {
                    if (pickaxeData != null) {
                        Tasks.delayed(2L) {
                            if (Menu.currentlyOpenedMenus[event.player.uniqueId] == null) {
                                PickaxeMenu(itemInHand, pickaxeData).openMenu(event.player)
                            }
                        }
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
    fun onToggleSoundsEvent(event: ToggleSoundsEvent) {
        val user = UserHandler.getUser(event.uuid)

        if (event.playSounds) {
            user.settings.updateSettingOption(UserSetting.PRIVATE_MESSAGE_SOUNDS, PrivateMessageSoundsOption(true))
        } else {
            user.settings.updateSettingOption(UserSetting.PRIVATE_MESSAGE_SOUNDS, PrivateMessageSoundsOption(false))
        }

        user.requiresSave = true
    }

}