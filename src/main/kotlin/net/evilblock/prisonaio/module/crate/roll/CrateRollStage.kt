/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.roll

data class CrateRollStage(var started: Boolean = false,
                          var startedAt: Long = -1,
                          var stageLength: Long,
                          var onFinish: () -> Unit) {

    fun start() {
        started = true
        startedAt = System.currentTimeMillis()
    }

    fun finish() {
        onFinish.invoke()
    }

}