package net.evilblock.prisonaio.module.robot.event

import net.evilblock.prisonaio.module.robot.Robot
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class RobotEvent(val robot: Robot) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}