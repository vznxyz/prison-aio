/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.entity

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramEntity
import net.evilblock.cubed.entity.villager.VillagerEntity
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorType
import net.evilblock.prisonaio.module.generator.impl.core.CoreGenerator
import net.evilblock.prisonaio.module.generator.menu.GeneratorMenu
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player

class GeneratorVillagerEntity(location: Location) : VillagerEntity(lines = listOf(), location = location) {

    @Transient lateinit var generator: Generator

    override fun initializeData() {
        super.initializeData()

        hologram.updateLines((hologram as UpdatingHologramEntity).getNewLines())
    }

    override fun createHologram() {
        hologram = UpdatingGeneratorHologram(calculateHologramLocation())
    }

    override fun updateLocation(location: Location) {
        super.updateLocation(location)

        hologram.updateLocation(calculateHologramLocation())
    }

    override fun onRightClick(player: Player) {
        GeneratorMenu(generator as CoreGenerator).openMenu(player)
//        generator.build.speed = generator.build.speed * 2
//        player.sendMessage("speed: ${generator.build.speed}")
    }

    override fun isMultiPartEntity(): Boolean {
        return true
    }

    override fun getChildEntities(): Set<Entity> {
        return setOf(hologram)
    }

    private inner class UpdatingGeneratorHologram(location: Location) : UpdatingHologramEntity("", location) {
        override fun getNewLines(): List<String> {
            return arrayListOf<String>().also {
                val genType = generator.getGeneratorType()
                if (genType == GeneratorType.CORE) {
                    it.add(genType.getColoredName())
                } else {
                    it.add(genType.getColoredName() + " Generator")
                }

                it.add("${ChatColor.GRAY}Level ${generator.level}")

                if (!generator.build.finished) {
                    val progressBar = PROGRESS_BAR.build(ProgressBarBuilder.percentage(generator.build.progress, generator.build.total))
                    val countdown = TimeUtil.formatIntoAbbreviatedString((generator.build.getRemainingTime() / 1000.0).toInt())

                    it.add("${ChatColor.GRAY}Progress: ${ChatColor.RED}$countdown")
                    it.add(progressBar)
                }
            }
        }

        override fun getTickInterval(): Long {
            return 1000L
        }
    }

    companion object {
        private val PROGRESS_BAR = ProgressBarBuilder()
    }

}