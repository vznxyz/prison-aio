/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.apple

import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.combat.CombatModule
import java.util.*

object GodAppleCooldownHandler : PluginHandler {

    private val cooldowns: MutableMap<UUID, GodAppleCooldown> = hashMapOf()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    override fun initialLoad() {
        super.initialLoad()
    }

    fun getCooldown(uuid: UUID): GodAppleCooldown? {
        return cooldowns[uuid]
    }

    fun trackCooldown(cooldown: GodAppleCooldown) {
        cooldowns[cooldown.uuid] = cooldown
    }

    fun forgetCooldown(cooldown: GodAppleCooldown) {
        cooldowns.remove(cooldown.uuid)
    }

}