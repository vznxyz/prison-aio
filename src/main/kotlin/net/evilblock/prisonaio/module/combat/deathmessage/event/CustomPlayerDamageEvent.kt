package net.evilblock.prisonaio.module.combat.deathmessage.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.combat.deathmessage.objects.Damage
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class CustomPlayerDamageEvent(val cause: EntityDamageEvent, var trackerDamage: Damage) : PluginEvent() {

    fun getPlayer(): Player {
        return cause.entity as Player
    }

    fun getDamage(): Double {
        return cause.damage
    }

}