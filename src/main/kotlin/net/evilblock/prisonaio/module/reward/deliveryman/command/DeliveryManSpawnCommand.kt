/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.reward.deliveryman.entity.DeliveryManNpcEntity
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object DeliveryManSpawnCommand {

    @Command(
        names = ["npc spawn delivery-man"],
        description = "Spawns a Delivery Man NPC",
        permission = "prisonaio.rewards.delivery-man.admin"
    )
    @JvmStatic
    fun execute(player: Player) {
        val deliveryMan = DeliveryManNpcEntity(player.location)
        deliveryMan.initializeData()
        deliveryMan.spawn(player)

        EntityManager.trackEntity(deliveryMan)

        player.sendMessage("${ChatColor.GREEN}Spawned a Delivery Man!")
    }

}