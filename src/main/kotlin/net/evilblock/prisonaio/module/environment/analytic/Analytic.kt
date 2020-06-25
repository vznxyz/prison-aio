/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.environment.analytic

enum class Analytic(
    val displayName: String,
    val defaultValue: Any?
) {

    UNIQUE_JOINS("Unique Joins", 0),
    BLOCKS_MINED("Blocks Mined", 0),
    TIME_PLAYED("Time Played", 0L);

    fun <T> getValue(): T {
        return AnalyticHandler.getValue(this)
    }

    fun <T> updateValue(value: T) {
        AnalyticHandler.updateValue(this, value)
    }

}