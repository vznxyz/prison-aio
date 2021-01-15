package net.evilblock.prisonaio.module.combat.damage.objects

import org.bukkit.entity.EntityType
import java.util.*

abstract class MobDamage(damaged: UUID, damage: Double, val mobType: EntityType) : Damage(damaged, damage)