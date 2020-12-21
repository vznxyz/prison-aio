/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.enderpearl

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.combat.CombatModule
import org.bukkit.Bukkit
import java.util.*

object EnderpearlCooldownHandler : PluginHandler() {

    private val cooldowns: MutableMap<UUID, EnderpearlCooldown> = hashMapOf()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    override fun initialLoad() {
        super.initialLoad()

        Tasks.asyncTimer(10L, 10L) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (cooldowns.containsKey(player.uniqueId)) {
                    val cooldown = cooldowns[player.uniqueId]!!
                    if (cooldown.hasExpired() && !cooldown.hasBeenNotified()) {
                        cooldown.notify(player)
                    }
                }
            }
        }
    }

    fun getCooldown(uuid: UUID): EnderpearlCooldown? {
        return cooldowns[uuid]
    }

    fun trackCooldown(cooldown: EnderpearlCooldown) {
        cooldowns[cooldown.uuid] = cooldown
    }

    fun forgetCooldown(cooldown: EnderpearlCooldown) {
        cooldowns.remove(cooldown.uuid)
    }

}