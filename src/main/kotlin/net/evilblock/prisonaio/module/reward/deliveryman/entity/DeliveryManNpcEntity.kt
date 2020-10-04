/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.menu.DeliveryManMenu
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Location
import org.bukkit.entity.Player

class DeliveryManNpcEntity(location: Location) : NpcEntity(lines = listOf(""), location = location) {

    override fun initializeData() {
        super.initializeData()

        updateTexture(DeliveryManHandler.getTextureValue(), DeliveryManHandler.getTextureSignature())
    }

    override fun onRightClick(player: Player) {
        DeliveryManMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
    }

}