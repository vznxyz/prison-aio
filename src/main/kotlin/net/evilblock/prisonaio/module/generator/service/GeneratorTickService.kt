/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.service

import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.service.Service

object GeneratorTickService : Service {

    override fun run() {
        for (generator in GeneratorHandler.getGenerators()) {
            try {
                if (System.currentTimeMillis() >= generator.lastTick + generator.getTickInterval()) {
                    generator.lastTick = System.currentTimeMillis()
                    generator.tick()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}