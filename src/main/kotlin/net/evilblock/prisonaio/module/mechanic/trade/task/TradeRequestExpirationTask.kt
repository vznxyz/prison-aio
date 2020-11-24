/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.trade.task

import net.evilblock.prisonaio.module.mechanic.trade.TradeHandler
import net.evilblock.prisonaio.module.mechanic.trade.TradeRequest

object TradeRequestExpirationTask : Runnable {

    override fun run() {
        val expired = arrayListOf<TradeRequest>()
        for (request in TradeHandler.getPendingRequests()) {
            if (System.currentTimeMillis() >= request.createdAt + 20_000L) {
                expired.add(request)
            }
        }

        for (request in expired) {
            request.expired()
            TradeHandler.forgetPendingRequest(request.sender, request.target)
        }
    }

}