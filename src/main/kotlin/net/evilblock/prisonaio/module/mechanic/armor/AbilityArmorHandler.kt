/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mechanic.armor.impl.InmateArmorSet
import net.evilblock.prisonaio.module.mechanic.armor.impl.MinerArmorSet
import net.evilblock.prisonaio.module.mechanic.armor.impl.WardenArmorSet
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AbilityArmorHandler {

    val registeredSets: List<AbilityArmorSet> = listOf(InmateArmorSet, MinerArmorSet, WardenArmorSet)

    internal val equippedSet: MutableMap<UUID, AbilityArmorSet> = ConcurrentHashMap()
    internal val pendingCheck: MutableSet<Player> = ConcurrentHashMap.newKeySet()

    fun initialLoad() {
        for (set in registeredSets) {
            if (set is Listener) {
                PrisonAIO.instance.server.pluginManager.registerEvents(set, PrisonAIO.instance)
            }
        }

        Tasks.asyncTimer(10L, 10L) {
            val iterator = pendingCheck.iterator()
            while (iterator.hasNext()) {
                val player = iterator.next()
                iterator.remove()

                if (equippedSet.containsKey(player.uniqueId)) {
                    val armorSet = equippedSet[player.uniqueId]!!
                    if (armorSet.hasSetEquipped(player)) {
                        continue
                    } else {
                        equippedSet.remove(player.uniqueId)
                        armorSet.onUnequipped(player)
                    }
                }

                for (armorSet in registeredSets) {
                    if (armorSet.hasSetEquipped(player)) {
                        equippedSet[player.uniqueId] = armorSet
                        armorSet.onEquipped(player)
                        break
                    }
                }
            }
        }
    }

    fun getSetById(id: String): AbilityArmorSet? {
        for (set in registeredSets) {
            if (set.setId.equals(id, ignoreCase = true)) {
                return set
            }
        }
        return null
    }

    fun getEquippedSet(player: Player): AbilityArmorSet? {
        return equippedSet[player.uniqueId]
    }

    fun isArmorPiece(itemStack: ItemStack): Boolean {
        for (set in registeredSets) {
            if (set.isArmorPiece(itemStack, false)) {
                return true
            }
        }
        return false
    }

}