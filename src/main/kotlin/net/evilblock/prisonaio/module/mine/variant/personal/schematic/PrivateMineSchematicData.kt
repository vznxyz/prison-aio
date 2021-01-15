/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.schematic

import com.sk89q.worldedit.Vector
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineHandler
import org.bukkit.Location
import java.io.File

/**
 * Container for storing the data needed to paste a schematic at a given location.
 */
data class PrivateMineSchematicData(
    val blockCoords: Pair<Int, Int>,
    val pasteLocation: Location,
    val schematicSize: Vector,
    val cuboid: Cuboid
) {

    companion object {
        @JvmStatic
        fun of(gridIndex: Int): PrivateMineSchematicData {
            val blockCoords = PrivateMineHandler.gridCoordsToBlockCoords(PrivateMineHandler.indexToGrid(gridIndex))
            val pasteLocation = Location(PrivateMineHandler.getGridWorld(), blockCoords.first.toDouble(), 68.0, blockCoords.second.toDouble())
            val schematicSize = WorldEditUtils.readSchematicSize(PrivateMineHandler.schematicFile)

            return PrivateMineSchematicData(
                blockCoords = blockCoords,
                pasteLocation = pasteLocation,
                schematicSize = schematicSize,
                cuboid = Cuboid(pasteLocation, pasteLocation.clone().add(schematicSize.x, schematicSize.y, schematicSize.z))
            )
        }
    }

}