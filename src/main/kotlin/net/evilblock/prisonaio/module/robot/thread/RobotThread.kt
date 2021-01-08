package net.evilblock.prisonaio.module.robot.thread

import net.evilblock.prisonaio.module.robot.RobotHandler

/**
 * The thread that handles ticking tracked robots that are [Tickable].
 */
object RobotThread : Thread("Robots-Ticker") {

    @JvmStatic
    var suppressErrors: Boolean = false

    override fun run() {
        while (true) {
            try {
                tick()
            } catch (e: Exception) {
                if (!suppressErrors) {
                    e.printStackTrace()
                }
            }

            sleep(50L)
        }
    }

    private fun tick() {
        for (robot in RobotHandler.getRobots()) {
            try {
                if (robot is Tickable) {
                    val tickable = robot as Tickable

                    if (System.currentTimeMillis() - tickable.getLastTick() >= tickable.getTickInterval()) {
                        tickable.tick()
                        tickable.updateLastTick()
                    }
                }
            } catch (e: Exception) {
                if (!suppressErrors) {
                    e.printStackTrace()
                }
            }
        }
    }

}