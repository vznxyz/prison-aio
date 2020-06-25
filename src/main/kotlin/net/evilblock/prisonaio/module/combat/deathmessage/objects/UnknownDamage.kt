package net.evilblock.prisonaio.module.combat.deathmessage.objects

import net.evilblock.prisonaio.module.combat.deathmessage.objects.Damage
import java.util.*

class UnknownDamage(damaged: UUID, damage: Double) : Damage(damaged, damage) {

    override fun getDeathMessage(): String {
        return "${wrapName(damaged)} died."
    }

}