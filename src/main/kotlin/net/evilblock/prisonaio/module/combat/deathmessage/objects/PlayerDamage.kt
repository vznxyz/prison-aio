package net.evilblock.prisonaio.module.combat.deathmessage.objects

import java.util.*

abstract class PlayerDamage(damaged: UUID, damage: Double, val damager: UUID) : Damage(damaged, damage)