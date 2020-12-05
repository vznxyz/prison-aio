package net.evilblock.prisonaio.module.robot.listener

import com.plotsquared.bukkit.events.PlotDeleteEvent
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object RobotPlotListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlotDeleteEvent(event: PlotDeleteEvent) {
        if (!event.isCancelled) {
            val iterator = RobotHandler.getRobotsByPlot(event.plotId).iterator()
            while (iterator.hasNext()) {
                val robot = iterator.next()

                RobotHandler.forgetRobot(robot)
                (robot as MinerRobot).clearFakeBlock()
                robot.destroyForCurrentWatchers()
            }
        }
    }

}