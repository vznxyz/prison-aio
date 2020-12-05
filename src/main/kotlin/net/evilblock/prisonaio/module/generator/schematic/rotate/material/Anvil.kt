package net.evilblock.prisonaio.module.generator.schematic.rotate.material

import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.material.Directional
import org.bukkit.material.MaterialData
import kotlin.experimental.and
import kotlin.experimental.or

class Anvil : MaterialData(Material.ANVIL), Directional {

    override fun setFacingDirection(blockFace: BlockFace) {
        var data = data
        data = data and 0x3.inv()

        data = when (blockFace) {
            BlockFace.EAST -> data or 0x1
            BlockFace.SOUTH -> data or 0x2
            BlockFace.WEST -> data or 0x3
            else -> data
        }

        setData(data)
    }

    override fun getFacing(): BlockFace {
        return when ((data and 0x3).toInt()) {
            0 -> BlockFace.NORTH
            1 -> BlockFace.EAST
            2 -> BlockFace.SOUTH
            3 -> BlockFace.WEST
            else -> BlockFace.SELF
        }
    }

}