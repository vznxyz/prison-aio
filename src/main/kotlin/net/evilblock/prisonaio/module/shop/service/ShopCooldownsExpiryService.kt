/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.service

import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.service.Service
import java.util.*

object ShopCooldownsExpiryService : Service {

    override fun run() {
        for (shop in ShopHandler.getShops()) {
            for (item in shop.items) {
                if (item.purchaseCooldown == null) {
                    if (item.purchaseTimestamps.isNotEmpty()) {
                        item.purchaseTimestamps.clear()
                    }
                } else {
                    val current = System.currentTimeMillis()
                    val cooldown = item.purchaseCooldown!!.get()

                    val toRemove = hashSetOf<UUID>()

                    for ((uuid, timestamp) in item.purchaseTimestamps.entries) {
                        if (current >= timestamp + cooldown) {
                            toRemove.add(uuid)
                        }
                    }

                    for (uuid in toRemove) {
                        item.purchaseTimestamps.remove(uuid)
                    }
                }
            }
        }
    }

}