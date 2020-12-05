package net.evilblock.prisonaio.module.robot.cosmetic

import net.evilblock.prisonaio.module.robot.impl.MinerRobot

interface Cosmetic {

    fun getUniqueId(): String

    fun getName(): String

    fun getDescription(): List<String>

    fun isCompatible(other: Cosmetic): Boolean

    fun onEnable(robot: MinerRobot) {}

    fun onDisable(robot: MinerRobot) {}

}