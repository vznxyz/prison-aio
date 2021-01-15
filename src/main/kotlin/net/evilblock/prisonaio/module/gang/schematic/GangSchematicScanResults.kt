/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.schematic

import org.bukkit.Location
import org.bukkit.block.Block

/**
 * Container for storing the result of a pasted schematic scan.
 */
data class GangSchematicScanResults(
    var spawnLocation: Location? = null,
    var guideLocation: Location? = null,
    var blocks: HashSet<Block> = hashSetOf()
)