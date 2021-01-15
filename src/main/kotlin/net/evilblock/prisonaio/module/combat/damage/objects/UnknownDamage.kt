package net.evilblock.prisonaio.module.combat.damage.objects

import java.util.*

class UnknownDamage(damaged: UUID, damage: Double) : Damage(damaged, damage) {

    override fun getDeathMessage(): String {
        return "${wrapName(damaged)} died."
    }

}