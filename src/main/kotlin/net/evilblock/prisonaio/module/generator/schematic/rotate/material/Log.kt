package net.evilblock.prisonaio.module.generator.schematic.rotate.material

import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.material.Directional
import org.bukkit.material.MaterialData
import kotlin.experimental.and
import kotlin.experimental.or

class Log : MaterialData(Material.LOG), Directional {

    override fun setFacingDirection(blockFace: BlockFace) {
        var data = data
        data = data and 0xC.inv()

        data = when (blockFace) {
            BlockFace.NORTH, BlockFace.SOUTH -> data or 0x4
            BlockFace.WEST, BlockFace.EAST -> data or 0x8
            else -> data
        }

        setData(data)
    }

    override fun getFacing(): BlockFace {
        var data = data
        data = data and 0xC

        return when (data.toInt() shr 2) {
            0 -> return BlockFace.UP
            1 -> return BlockFace.NORTH
            2 -> return BlockFace.EAST
            3 -> return BlockFace.SELF
            else -> BlockFace.SELF
        }
    }

}