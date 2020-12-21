/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.kits

import net.evilblock.cubed.util.Duration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Kit(val id: String) {

    var name: String = id
    var icon: ItemStack = ItemStack(Material.CHEST)

    var items: MutableList<ItemStack> = arrayListOf()

    var requiresPermission: Boolean = true
    var public: Boolean = false

    val cooldowns: MutableMap<UUID, Long> = ConcurrentHashMap()
    var cooldownDuration: Duration? = null

    fun getPermission(): String {
        return "kits.redeem.${id.toLowerCase()}"
    }

    fun hasPermission(player: Player): Boolean {
        return player.hasPermission(getPermission())
    }

    fun giveItems(player: Player) {
        for (item in items) {
            val notInserted = player.inventory.addItem(item)
            if (notInserted.isNotEmpty()) {
                for (tryAgain in notInserted.values) {
                    val notInsertedAgain = player.enderChest.addItem(tryAgain)
                    for (drop in notInsertedAgain.values) {
                        player.world.dropItemNaturally(player.location, drop)
                    }
                }
            }
        }

        player.updateInventory()
    }

    fun isCooldownSet(): Boolean {
        return cooldownDuration != null
    }

    fun isOnCooldown(player: Player): Boolean {
        return cooldownDuration != null && cooldowns.containsKey(player.uniqueId) && (System.currentTimeMillis() < cooldowns[player.uniqueId]!! + cooldownDuration!!.get())
    }

    fun getRemainingCooldown(player: Player): Long {
        return (cooldowns[player.uniqueId]!! + cooldownDuration!!.get()) - System.currentTimeMillis()
    }

    fun applyCooldown(player: Player) {
        cooldowns[player.uniqueId] = System.currentTimeMillis()
    }

}