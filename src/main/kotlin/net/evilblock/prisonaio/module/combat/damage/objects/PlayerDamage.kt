package net.evilblock.prisonaio.module.combat.damage.objects

import java.util.*

abstract class PlayerDamage(damaged: UUID, damage: Double, val damager: UUID) : Damage(damaged, damage)