/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.apple.listener

import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.combat.apple.GodAppleCooldown
import net.evilblock.prisonaio.module.combat.apple.GodAppleCooldownHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerQuitEvent

object GodAppleListeners : Listener {

    @EventHandler(ignoreCancelled = false)
    fun onPlayerItemConsume(event: PlayerItemConsumeEvent) {
        if (event.item == null || event.item.type != Material.GOLDEN_APPLE || event.item.durability.toInt() != 1) {
            return
        }

        var cooldown = GodAppleCooldownHandler.getCooldown(event.player.uniqueId)
        if (cooldown == null || cooldown.hasExpired()) {
            if (cooldown == null) {
                cooldown = GodAppleCooldown(event.player.uniqueId)
                GodAppleCooldownHandler.trackCooldown(cooldown)
            } else {
                cooldown.reset()
            }

            event.player.sendMessage("${ChatColor.DARK_GREEN}███${ChatColor.BLACK}██${ChatColor.DARK_GREEN}███")
            event.player.sendMessage("${ChatColor.DARK_GREEN}███${ChatColor.BLACK}█${ChatColor.DARK_GREEN}████")
            event.player.sendMessage("${ChatColor.DARK_GREEN}██${ChatColor.GOLD}████${ChatColor.DARK_GREEN}██${ChatColor.GOLD} Super Golden Apple:")
            event.player.sendMessage("${ChatColor.DARK_GREEN}█${ChatColor.GOLD}██${ChatColor.WHITE}█${ChatColor.GOLD}███${ChatColor.DARK_GREEN}█${ChatColor.DARK_GREEN}   Consumed")
            event.player.sendMessage("${ChatColor.DARK_GREEN}█${ChatColor.GOLD}█${ChatColor.WHITE}█${ChatColor.GOLD}████${ChatColor.DARK_GREEN}█${ChatColor.YELLOW} Cooldown Remaining:")
            event.player.sendMessage("${ChatColor.DARK_GREEN}█${ChatColor.GOLD}██████${ChatColor.DARK_GREEN}█${ChatColor.BLUE}   ${TimeUtil.formatIntoDetailedString(cooldown.getRemainingSeconds().toInt())}")
            event.player.sendMessage("${ChatColor.DARK_GREEN}█${ChatColor.GOLD}██████${ChatColor.DARK_GREEN}█")
            event.player.sendMessage("${ChatColor.DARK_GREEN}██${ChatColor.GOLD}████${ChatColor.DARK_GREEN}██")
        } else {
            event.isCancelled = true
            event.player.updateInventory()

            val formattedRemainingTime = TimeUtil.formatIntoDetailedString(cooldown.getRemainingSeconds().toInt())
            event.player.sendMessage("${ChatColor.RED}You cannot use this for another ${ChatColor.BOLD}$formattedRemainingTime${ChatColor.RED}.")
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val cooldown = GodAppleCooldownHandler.getCooldown(event.player.uniqueId)
        if (cooldown != null) {
            GodAppleCooldownHandler.forgetCooldown(cooldown)
        }
    }

}