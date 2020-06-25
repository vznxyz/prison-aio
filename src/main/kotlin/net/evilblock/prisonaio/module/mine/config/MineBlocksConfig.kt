/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.config

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.mine.block.BlockType

data class MineBlocksConfig(
    /**
     * The block types used to fill a newly generated mining region
     */
    val blockTypes: MutableSet<BlockType> = hashSetOf()
) {

    fun pickRandomBlockType(): BlockType {
        if (blockTypes.isEmpty()) {
            throw IllegalStateException("Cannot pick random block if block list is empty")
        }

        val filteredBlockTypes = blockTypes.filter { blockType -> blockType.percentage > 0.0 }
        if (filteredBlockTypes.isEmpty()) {
            throw IllegalStateException("Cannot pick random block if block list has no blocks with percentages more than 0%")
        }

        if (filteredBlockTypes.size == 1) {
            return filteredBlockTypes[0]
        }

        while (true) {
            val randomBlockType = blockTypes.random()
            if (Chance.percent(randomBlockType.percentage)) {
                return randomBlockType
            }
        }
    }

}