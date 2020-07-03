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
        names = ["deliveryman spawn"],
        description = "Spawn the Delivery Man NPC",
        permission = "prisonaio.deliveryman.admin"
    )
    @JvmStatic
    fun execute(player: Player) {
        val deliveryMan = DeliveryManNpcEntity(player.location)
        deliveryMan.initializeData()

        EntityManager.trackEntity(deliveryMan)

        player.sendMessage("${ChatColor.GREEN}Successfully spawned Delivery Man!")
    }

}