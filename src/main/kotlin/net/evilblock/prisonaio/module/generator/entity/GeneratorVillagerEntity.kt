/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.entity

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramEntity
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.villager.VillagerEntity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.generator.build.mode.BuildModeHandler
import net.evilblock.prisonaio.module.generator.menu.GeneratorMenu
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class GeneratorVillagerEntity(location: Location) : VillagerEntity(lines = listOf(), location = location) {

    @Transient lateinit var generator: Generator

    override fun initializeData() {
        super.initializeData()

        hologram.updateLines((hologram as UpdatingHologramEntity).getNewLines())
    }

    override fun getEditorButtons(player: Player, menu: Menu): MutableList<Button> {
        return super.getEditorButtons(player, menu).also { list ->
            list.add(0, SetLevelButton())
        }
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

        if (!GeneratorHandler.canAccess(player)) {
            player.sendMessage("${ChatColor.RED}That's not yours!")
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

    private inner class SetLevelButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Set Level"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Set this generator's build level."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to set level"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.EXP_BOTTLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt()
                    .withText("${ChatColor.GREEN}Please input a new level. ${ChatColor.GRAY}(1-${generator.getGeneratorType().getMaxLevel()})")
                    .acceptInput { number ->
                        if (number.toInt() !in 1..(generator.getGeneratorType().getMaxLevel())) {
                            player.sendMessage("${ChatColor.RED}Invalid level!")
                            return@acceptInput
                        }

                        generator.level = number.toInt()

                        player.sendMessage("${ChatColor.GREEN}Updated level to ${generator.level}!")

                        EditEntityMenu(this@GeneratorVillagerEntity).openMenu(player)
                    }
                    .start(player)
            }
        }
    }

}