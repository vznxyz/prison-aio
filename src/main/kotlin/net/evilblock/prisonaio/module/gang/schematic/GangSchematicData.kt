/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.schematic

import com.sk89q.worldedit.Vector
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.gang.GangHandler
import org.bukkit.Location
import java.io.File

/**
 * Container for storing the data needed to paste a schematic at a given location.
 */
data class GangSchematicData(
    val blockCoords: Pair<Int, Int>,
    val pasteLocation: Location,
    val schematicSize: Vector,
    val cuboid: Cuboid
) {

    companion object {
        @JvmStatic
        fun of(gridIndex: Int, schematicFile: File): GangSchematicData {
            val blockCoords = GangHandler.gridCoordsToBlockCoords(GangHandler.indexToGrid(gridIndex))
            val pasteLocation = Location(GangHandler.getGridWorld(), blockCoords.first.toDouble(), 68.0, blockCoords.second.toDouble())
            val schematicSize = WorldEditUtils.readSchematicSize(schematicFile)

            return GangSchematicData(
                blockCoords = blockCoords,
                pasteLocation = pasteLocation,
                schematicSize = schematicSize,
                cuboid = Cuboid(pasteLocation, pasteLocation.clone().add(schematicSize.x, schematicSize.y, schematicSize.z))
            )
        }
    }

}