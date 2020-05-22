package net.evilblock.prisonaio.module.crate.roll

import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.crate.CratesModule
import org.bukkit.entity.Player
import java.util.*

object CrateRollHandler : PluginHandler {

    private val activeRolls = hashMapOf<UUID, CrateRoll>()

    override fun getModule(): PluginModule {
        return CratesModule
    }

    fun getActiveRolls(): List<CrateRoll> {
        return activeRolls.values.toList()
    }

    fun isRolling(player: Player): Boolean {
        return activeRolls.containsKey(player.uniqueId)
    }

    fun getActiveRoll(player: Player): CrateRoll {
        return activeRolls[player.uniqueId]!!
    }

    fun trackRoll(roll: CrateRoll) {
        activeRolls[roll.rolledBy] = roll
    }

    fun forgetRoll(roll: CrateRoll) {
        activeRolls.remove(roll.rolledBy)
    }

}