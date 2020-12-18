/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.build

import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.util.particle.ParticleMeta
import net.evilblock.prisonaio.util.particle.ParticleUtil
import net.minecraft.server.v1_12_R1.EnumParticle
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material

class GeneratorBuild {

    @Transient
    lateinit var generator: Generator

    var total: Int = 0
    var progress: Int = 0
    var lastPlaced: Long = System.currentTimeMillis()
    var finished: Boolean = false

    var speed: Double = 1.0

    fun getTickInterval(): Long {
        return (((generator.getLevel().buildTime / total.toDouble()) * 1000.0) / speed).toLong()
    }

    fun getRemainingTime(): Long {
        if (finished) {
            return 0L
        }

        val level = generator.getLevel()
        val schematic = level.getSchematic(generator.rotation)

        val blocksLeft = schematic.blocks.size - progress
        val timeLeft = (blocksLeft * getTickInterval())

        if (lastPlaced == 0L) {
            lastPlaced = System.currentTimeMillis() - (1000.0.toLong() + 1)
        }

        val remainingTime = timeLeft - ((System.currentTimeMillis() - lastPlaced) / speed).toLong()
        return if (remainingTime > 0) { remainingTime } else { 0 }
    }

    fun renderProgressBar(): String {
        return ProgressBarBuilder.DEFAULT.build(ProgressBarBuilder.percentage(progress, total))
    }

    fun renderRemainingTime(): String {
        return TimeUtil.formatIntoAbbreviatedString((getRemainingTime() / 1000.0).toInt())
    }

    fun tick() {
        val level = generator.getLevel()
        val schematic = level.getSchematic(generator.rotation)

        total = schematic.blocks.size

        val bounds = generator.bounds
        val timePerBlock = getTickInterval()

        if (lastPlaced == 0L) {
            lastPlaced = System.currentTimeMillis() - (timePerBlock + 1)
        }

        if (System.currentTimeMillis() > lastPlaced + timePerBlock) {
            lastPlaced = System.currentTimeMillis()

            if (progress < schematic.blocks.size) {
                val schematicBlock = schematic.blocks[progress++]

                val worldBlock = bounds.lowerNE.add(schematicBlock.vector).block
                worldBlock.setTypeIdAndData(schematicBlock.type, schematicBlock.data, false)

                ParticleUtil.sendsParticleToAll(
                    ParticleMeta(
                        location = worldBlock.location.clone().add(0.5, 0.75, 0.5),
                        particle = EnumParticle.CLOUD,
                        deltaX = 0.0F,
                        deltaY = 0.1F,
                        deltaZ = 0.0F,
                        speed = 0.05F,
                        amount = 5
                    )
                )
            }

            if (progress >= schematic.blocks.size) {
                finished = true

                Bukkit.getPlayer(generator.owner)?.also { player ->
                    player.sendMessage("${GeneratorHandler.CHAT_PREFIX}Your ${generator.getGeneratorType().getColoredName()} ${ChatColor.GRAY}has finished building!")
                }
            }
        }
    }

    fun clearBlocks() {
        for (block in generator.bounds) {
            block.type = Material.AIR
            block.state.update()
        }
    }

}