package net.evilblock.prisonaio.module.robot.event

import net.evilblock.prisonaio.module.robot.Robot
import org.bukkit.event.Cancellable

class RobotClickedEvent(robot: Robot, clickType: ClickType) : RobotEvent(robot), Cancellable {

    private var cancelled = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    enum class ClickType {
        LEFT_CLICK,
        RIGHT_CLICK,
        SHIFT_LEFT_CLICK,
        SHIFT_RIGHT_CLICK
    }

}