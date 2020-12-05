/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.build.mode

import com.intellectualcrafters.plot.`object`.Plot
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.cubed.util.nms.block.BlockChangeBuilder
import net.evilblock.cubed.util.snapshot.PlayerSnapshot
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.generator.GeneratorType
import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel
import net.evilblock.prisonaio.module.generator.schematic.rotate.RotateUtil
import net.evilblock.prisonaio.module.generator.schematic.rotate.RotatedSchematic
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.DyeColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class BuildMode(
    val player: Player,
    val plot: Plot,
    val type: GeneratorType,
    val level: GeneratorBuildLevel,
    var location: Location
) {

    private val minX: Int = plot.bottomAbs.x
    private val minZ: Int = plot.bottomAbs.z
    private val maxX: Int = plot.topAbs.x
    private val maxZ: Int = plot.topAbs.z

    lateinit var snapshot: PlayerSnapshot
    lateinit var rotation: Rotation
    lateinit var schematic: RotatedSchematic

    var previewing: Boolean = false
    var previewLocation: Location? = null

    private var blockChanges: BlockChangeBuilder = BlockChangeBuilder(player.world)

    fun start() {
        snapshot = PlayerSnapshot(player)
        rotation = RotateUtil.getFacing(player)
        schematic = level.getSchematic(rotation) ?: throw IllegalStateException("Couldn't find schematic for ${type.name} level ${level.number}")

        player.inventory.clear()
        player.closeInventory()
        player.teleport(player.location.add(0.0, schematic.area.blockY.toDouble(), 0.0))
        player.allowFlight = true
        player.isFlying = true

        player.inventory.setItem(0, BuildModeItems.CONFIRM)
        player.inventory.setItem(4, BuildModeItems.PREVIEW)
        player.inventory.setItem(8, BuildModeItems.EXIT)

        updateLocation(player.location)
    }

    fun stop() {
        snapshot.restore(player)
        clearPacket()
    }

    fun sendPacket() {
        val schematic = level.getSchematic(rotation) ?: throw IllegalStateException("Couldn't find schematic for ${type.name} level ${level.number}")
        val area: Vector = schematic.area
        val min = location.clone().subtract(area.blockX / 2.toDouble(), 0.0, area.blockZ / 2.toDouble())
        val max = min.clone().add(area)

        blockChanges = BlockChangeBuilder(player.world)

        if (!previewing) {
            val id = Material.STAINED_GLASS.id

            val plot = PlotUtil.getPlot(location)
            if (plot != null && plot.connectedPlots.size > 1) {
                for (i in minX..maxX) {
                    blockChanges
                        .addChange(i, min.blockY, minZ, id, DyeColor.GRAY.woolData.toInt())
                        .addChange(i, min.blockY, maxZ, id, DyeColor.GRAY.woolData.toInt())
                }

                for (i in minZ..maxZ) {
                    blockChanges
                        .addChange(minX, min.blockY, i, id, DyeColor.GRAY.woolData.toInt())
                        .addChange(maxX, min.blockY, i, id, DyeColor.GRAY.woolData.toInt())
                }
            }

            val data = if (canBuild(player, Cuboid(min, max))) {
                DyeColor.LIME.woolData.toInt()
            } else {
                DyeColor.RED.woolData.toInt()
            }

            for (i in min.blockX..max.blockX) {
                blockChanges
                    .addChange(i, min.blockY, min.blockZ, id, data)
                    .addChange(i, min.blockY, max.blockZ, id, data)
            }

            for (i in min.blockZ..max.blockZ) {
                blockChanges
                    .addChange(min.blockX, min.blockY, i, id, data)
                    .addChange(max.blockX, min.blockY, i, id, data)
            }
        } else {
            for (block in schematic.blocks) {
                blockChanges.addChange(
                    min.blockX + block.vector.blockX,
                    min.blockY + block.vector.blockY,
                    min.blockZ + block.vector.blockZ,
                    block.type,
                    block.data.toInt()
                )
            }
        }

        blockChanges.sendUpdate(player, false)
    }

    fun clearPacket() {
        blockChanges.sendUpdate(player, true)
    }

    fun updateLocation(location: Location) {
        if (!previewing) {
            clearPacket()

            val newLocation = location.clone()
            newLocation.y = location.y - level.getSchematic(rotation)!!.area.blockY

            this.location = newLocation
            sendPacket()
        }
    }

    fun togglePreview() {
        clearPacket()

        if (previewing) {
            player.teleport(previewLocation)
        } else {
            previewLocation = player.location
        }

        previewing = !previewing

        sendPacket()
    }

    fun getBounds(): Cuboid {
        val area: Vector = schematic.area
        val min = location.clone().subtract(area.blockX / 2.toDouble(), 0.0, area.blockZ / 2.toDouble())
        val max = min.clone().add(area)
        return Cuboid(min, max)
    }

    fun canBuild(player: Player, area: Cuboid): Boolean {
        if (area.upperY >= 256 || area.lowerY <= 0) {
            return false
        }

        val min = area.lowerNE
        val max = area.upperSW

        val altMin = Location(min.world, min.x, min.y, max.z)
        val altMax = Location(max.world, max.x, max.y, min.z)

        val plotMin = PlotUtil.getPlot(min)
        val plotMax = PlotUtil.getPlot(max)

        val plotAltMin = PlotUtil.getPlot(altMin)
        val plotAltMax = PlotUtil.getPlot(altMax)

        if (plotMin == null
            || plotMax == null
            || plotAltMin == null
            || plotAltMax == null
            || plotMin != plotMax
            || plotMin != plotAltMin
            || plotMin != plotAltMax) {
            return false
        }

        if (GeneratorHandler.isOccupiedArea(area, plotMin)) {
            return false
        }

        if (RegionBypass.hasBypass(player)) {
            return true
        }

        return plotMin.isOwner(player.uniqueId)
    }

}