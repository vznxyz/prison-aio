/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.service

import net.evilblock.prisonaio.PrisonAIO

/**
 * A dedicated thread for executing PrisonAIO services, rather
 * than using Bukkit's task scheduler.
 *
 * This thread executes its logic at the same rate a Minecraft server
 * does, which is 20 ticks per second.
 */
class ServicesThread : Thread("PrisonAIO - Services") {

    override fun run() {
        while (true) {
            try {
                tick()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            sleep(50L)
        }
    }

    private fun tick() {
        for (task in ServiceRegistry.registered) {
            try {
                task.ticks++

                if (task.delay > 0) {
                    if (!task.delayComplete) {
                        if (task.ticks >= task.delay) {
                            try {
                                task.service.run()
                            } catch (e: Exception) {
                                PrisonAIO.instance.logger.warning("Caught exception trying to execute service task")
                                e.printStackTrace()
                            }

                            task.delayComplete = true
                            task.ticks = 0
                        }

                        continue
                    }
                }

                if (task.ticks >= task.interval) {
                    try {
                        task.service.run()
                    } catch (e: Exception) {
                        PrisonAIO.instance.logger.warning("Caught exception trying to execute service task")
                        e.printStackTrace()
                    }

                    task.ticks = 0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}