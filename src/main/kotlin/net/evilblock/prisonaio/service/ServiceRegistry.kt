/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.service

object ServiceRegistry {

    internal val registered = arrayListOf<ServiceTask>()

    @JvmStatic
    fun register(service: Service, interval: Long = 1L) {
        registered.add(ServiceTask(service, interval = interval))
    }

    @JvmStatic
    fun register(service: Service, delay: Long = 1L, interval: Long = 1L) {
        registered.add(ServiceTask(service, delay, interval))
    }

}