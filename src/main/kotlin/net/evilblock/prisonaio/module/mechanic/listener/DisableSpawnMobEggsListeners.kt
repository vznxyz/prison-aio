package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.prisonaio.module.mechanic.region.bypass.RegionBypass
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerInteractEvent

object DisableSpawnMobEggsListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.player.inventory.itemInMainHand != null && event.player.inventory.itemInMainHand.type == Material.MONSTER_EGG) {
            if (!event.player.isOp || !RegionBypass.hasBypass(event.player)) {
                event.isCancelled = true
            } else {
                if (!RegionBypass.hasReceivedNotification(event.player)) {
                    RegionBypass.sendNotification(event.player)
                }
            }
        }
    }

    @EventHandler
    fun onCreatureSpawnEvent(event: CreatureSpawnEvent) {
        if (event.spawnReason == CreatureSpawnEvent.SpawnReason.DISPENSE_EGG ||
            event.spawnReason == CreatureSpawnEvent.SpawnReason.EGG ) {
            event.isCancelled = true
        }
    }

}