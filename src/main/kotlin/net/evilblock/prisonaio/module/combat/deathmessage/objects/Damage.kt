package net.evilblock.prisonaio.module.combat.deathmessage.objects

import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import java.util.*

abstract class Damage(
    val damaged: UUID,
    val damage: Double
) {

    val time: Long = System.currentTimeMillis()

    abstract fun getDeathMessage(): String

    fun wrapName(player: UUID): String {
        val name = Cubed.instance.uuidCache.name(player)
        val kills = UserHandler.getUser(player).statistics.getKills()
        return "${ChatColor.RED}$name${ChatColor.DARK_RED}[$kills]${ChatColor.YELLOW}"
    }

    fun getTimeDifference(): Long {
        return System.currentTimeMillis() - time
    }

}