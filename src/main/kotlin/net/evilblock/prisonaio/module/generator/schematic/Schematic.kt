/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.schematic

import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools
import org.bukkit.Bukkit
import org.bukkit.util.Vector
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.experimental.and

class Schematic(
    val name: String,
    val blocks: ShortArray,
    val data: ByteArray,
    val width: Short,
    val length: Short,
    val height: Short
) {

    fun getBlockMap(): Array<Array<Array<SchematicBlock>>> {
        val blockMap: Array<Array<Array<SchematicBlock?>>> = Array(width.toInt()) { Array(height.toInt()) { arrayOfNulls<SchematicBlock>(length.toInt()) } }
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    val index = y * width * length + z * width + x
                    blockMap[x][y][z] = SchematicBlock(blocks[index].toInt(), data[index], Vector(x, y, z))
                }
            }
        }
        return blockMap as Array<Array<Array<SchematicBlock>>>
    }

    companion object {
        @JvmStatic
        fun loadSchematic(file: File): Schematic? {
            val name = file.name.replace(".schematic", "")
            if (!file.exists()) {
                Bukkit.getLogger().warning("$name schematic not found")
                return null
            }

            try {
                FileInputStream(file).use { stream ->
                    val nbtData = NBTCompressedStreamTools.a(stream)
                    val width = nbtData.getShort("Width")
                    val height = nbtData.getShort("Height")
                    val length = nbtData.getShort("Length")
                    val rawBlocks = nbtData.getByteArray("Blocks")
                    val rawData = nbtData.getByteArray("Data")
                    var addId = ByteArray(0)

                    if (nbtData.hasKey("AddBlocks")) {
                        addId = nbtData.getByteArray("AddBlocks")
                    }

                    val blocks = ShortArray(rawBlocks.size)
                    for (index in rawBlocks.indices) {
                        if (index shr 1 >= addId.size) {
                            blocks[index] = (rawBlocks[index].toInt() and 0xFF).toShort()
                        } else {
                            if (index and 1 == 0) {
                                blocks[index] = (((addId[index shr 1] and 0x0F.toByte()).toInt() shl 8) + (rawBlocks[index].toInt() and 0xFF)).toShort()
                            } else {
                                blocks[index] = (((addId[index shr 1] and 0xF0.toByte()).toInt() shl 4) + (rawBlocks[index].toInt() and 0xFF)).toShort()
                            }
                        }
                    }

                    return Schematic(name, blocks, rawData, width, length, height)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }
    }

}