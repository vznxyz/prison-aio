package net.evilblock.prisonaio.module.robot.thread

interface Tickable {

    /**
     * The tick function.
     */
    fun tick()

    /**
     * The interval (in milliseconds) between ticks.
     */
    fun getTickInterval(): Long

    /**
     * Gets the time (in epoch milliseconds) that this [Tickable] was last ticked.
     */
    fun getLastTick(): Long

    /**
     * Updates the last tick time.
     */
    fun updateLastTick()

}