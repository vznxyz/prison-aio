/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.service

class ServiceTask(
    val service: Service,
    var delay: Long = 0,
    var interval: Long = 0
) {

    internal var delayComplete: Boolean = false
    internal var ticks: Long = 0

}