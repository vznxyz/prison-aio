/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.listener

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangModule
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.region.event.RegionBlockBreakEvent
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import kotlin.random.Random

object GangTrophiesListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onRegionBlockBreakEvent(event: RegionBlockBreakEvent) {
        if (event.region.supportsRewards()) {
            val assumedGang = GangHandler.getAssumedGang(event.player.uniqueId) ?: return

            var chance = GangModule.readTrophyBlockBreakChance()
            if (assumedGang.hasBooster(GangBooster.BoosterType.INCREASED_TROPHIES)) {
                chance += GangModule.readIncreasedTrophiesChanceMod()
            }

            if (Chance.percent(chance)) {
                val amount = Random.nextInt(GangModule.readTrophyBlockBreakMinAmount(), GangModule.readTrophyBlockBreakMaxAmount())
                assumedGang.giveTrophies(amount)

                val user = UserHandler.getUser(event.player.uniqueId)
                if (user.getSettingOption(UserSetting.REWARD_MESSAGES).getValue()) {
                    event.player.sendMessage("${RewardsModule.getChatPrefix()}You found ${ChatColor.GOLD}${ChatColor.BOLD}$amount ${ChatColor.GOLD}Trophies ${ChatColor.GRAY}for your gang while mining!")
                }
            }
        }
    }

}