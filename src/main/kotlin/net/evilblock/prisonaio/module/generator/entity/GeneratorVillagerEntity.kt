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
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.build.mode.BuildModeHandler
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
        if (BuildModeHandler.isInMode(player)) {
            player.sendMessage("${ChatColor.RED}You can't access Generator NPCs while in Build Mode!")
            return
        }

        GeneratorMenu(generator).openMenu(player)
    }

    override fun isMultiPartEntity(): Boolean {
        return true
    }

    override fun getChildEntities(): Set<Entity> {
        return setOf(hologram)
    }

    private inner class UpdatingGeneratorHologram(location: Location) : UpdatingHologramEntity("", location) {
        override fun getNewLines(): List<String> {
            return arrayListOf<String>().also { lines ->
                val genType = generator.getGeneratorType()

                lines.add(genType.getColoredName())
                lines.add("${ChatColor.GRAY}Level ${generator.level}")

                if (!generator.build.finished) {
                    lines.add("${ChatColor.GRAY}Progress: ${ChatColor.RED}${generator.build.renderRemainingTime()}")
                    lines.add("${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}${generator.build.renderProgressBar()}${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}")
                }
            }
        }

        override fun getTickInterval(): Long {
            return 1000L
        }
    }

}