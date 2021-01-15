package net.evilblock.prisonaio.module.combat.damage

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.combat.CombatModule
import net.evilblock.prisonaio.module.combat.damage.listeners.DamageListener
import net.evilblock.prisonaio.module.combat.damage.objects.Damage
import net.evilblock.prisonaio.module.combat.damage.task.DamageTrackerCleanupTask
import net.evilblock.prisonaio.module.combat.damage.trackers.*
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object DamageTracker : PluginHandler() {

    private val damage: MutableMap<UUID, MutableList<Damage>> = ConcurrentHashMap()

    val lastKilled: MutableMap<UUID, Pair<UUID, Long>> = ConcurrentHashMap()
    val boosting: MutableMap<UUID, Pair<Int, Long>> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    override fun initialLoad() {
        super.initialLoad()

        Tasks.asyncTimer(DamageTrackerCleanupTask, 20L, 20L)

        arrayListOf(
            DamageListener,
            GeneralTracker(),
            PVPTracker(),
            EntityTracker(),
            FallTracker(),
            ArrowTracker(),
            VoidTracker(),
            BurnTracker()
        ).forEach { tracker ->
            getModule().getPluginFramework().server.pluginManager.registerEvents(tracker, getModule().getPluginFramework())
        }
    }

    @JvmStatic
    fun clearDamageCache() {
        damage.clear()
    }

    @JvmStatic
    fun getDamageList(player: Player): MutableList<Damage> {
        return damage[player.uniqueId] ?: arrayListOf()
    }

    @JvmStatic
    fun addDamage(player: Player, addedDamage: Damage) {
        if (!damage.containsKey(player.uniqueId)) {
            damage[player.uniqueId] = arrayListOf()
        }

        val damageList = getDamageList(player)
        while (damageList.size > 30) {
            damageList.removeAt(0)
        }

        damageList.add(addedDamage)
    }

    @JvmStatic
    fun clearDamage(player: Player) {
        damage.remove(player.uniqueId)
    }

}