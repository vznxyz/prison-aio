/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.schematic

import org.bukkit.Location
import org.bukkit.block.Block

/**
 * Container for storing the result of a pasted schematic scan.
 */
data class PrivateMineSchematicScanResults(
    var spawnLocation: Location? = null,
    var npcLocation: Location? = null,
    var cuboidLower: Location? = null,
    var cuboidUpper: Location? = null,
    var blocks: HashSet<Block> = hashSetOf()
)