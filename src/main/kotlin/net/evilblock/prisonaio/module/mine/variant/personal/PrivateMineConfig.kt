/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal

import net.evilblock.prisonaio.module.mine.MinesModule
import net.evilblock.prisonaio.module.mine.variant.personal.data.BlockType
import org.apache.commons.lang.math.DoubleRange

object PrivateMineConfig {

    var resetInterval: Long = 100000L
    var salesTaxRange: DoubleRange = DoubleRange(10)
    var playerLimit: Int = 15
    var blocks: List<BlockType> = arrayListOf()

    fun load() {
        MinesModule.config.getConfigurationSection("personal-mines.mine").also { section ->
            resetInterval = section.getLong("reset-interval")
            salesTaxRange = DoubleRange(section.getDouble("sales-tax"))
            playerLimit = section.getInt("player-limit")

            blocks = (section["blocks"] as List<Map<String, Any>>).map { BlockType.fromMap(it) }
        }
    }

}