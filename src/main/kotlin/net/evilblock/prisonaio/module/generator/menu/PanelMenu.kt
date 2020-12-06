/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.menu

import com.intellectualcrafters.plot.`object`.Plot
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.generator.GeneratorType
import net.evilblock.prisonaio.module.generator.build.mode.BuildMode
import net.evilblock.prisonaio.module.generator.build.mode.BuildModeHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class PanelMenu(private val plot: Plot) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.RED}Generator Panel"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for ((index, generator) in GeneratorHandler.getGeneratorsByPlot(plot).withIndex()) {
                buttons[GENERATOR_SLOTS[index]] = GeneratorButton(generator)
            }

            buttons[16] = PurchaseGeneratorButton(GeneratorType.CORE)
            buttons[25] = PurchaseGeneratorButton(GeneratorType.MONEY)
            buttons[34] = PurchaseGeneratorButton(GeneratorType.TOKEN)
            buttons[43] = PurchaseGeneratorButton(GeneratorType.KEY)

            for (i in BORDER_SLOTS) {
                buttons[i] = GlassButton(7)
            }
        }
    }

    private inner class GeneratorButton(private val generator: Generator) : Button() {
        override fun getName(player: Player): String {
            return "${generator.getGeneratorType().getColoredName()} ${ChatColor.GRAY}(Level ${NumberUtils.format(generator.level)})"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.add("${ChatColor.YELLOW}${ChatColor.BOLD}Click ")
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(generator.getGeneratorType().icon)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }
    }

    private inner class PurchaseGeneratorButton(private val type: GeneratorType) : Button() {
        override fun getName(player: Player): String {
            val hasGeneratorType = GeneratorHandler.getGeneratorsByPlot(plot).any { it.getGeneratorType() == type }

            return if (hasGeneratorType) {
                "${type.getColoredName()} Generator ${ChatColor.GRAY}(Reached Limit)"
            } else {
                "${type.getColoredName()} Generator ${ChatColor.GRAY}(Level 1)"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val hasGeneratorType = GeneratorHandler.getGeneratorsByPlot(plot).any { it.getGeneratorType() == type }
                if (hasGeneratorType) {
                    desc.addAll(TextSplitter.split(text = "You have reached the limit of ${type.getProperName(true)} you can have on a plot!"))
                } else {
                    val firstLevel = type.getLevels().first()

                    desc.add("${ChatColor.GRAY}Price: ${Formats.formatTokens(firstLevel.cost)}")
                    desc.add("${ChatColor.GRAY}Time: ${ChatColor.RED}${ChatColor.BOLD}${TimeUtil.formatIntoAbbreviatedString(firstLevel.buildTime)}")
                    desc.add("")
                    desc.add("${ChatColor.YELLOW}Click to purchase a ${type.getProperName()}")
                }
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(type.icon)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                val hasGeneratorType = GeneratorHandler.getGeneratorsByPlot(plot).any { it.getGeneratorType() == type }
                if (hasGeneratorType) {
                    player.sendMessage("${ChatColor.RED}You have reached the limit of ${type.getProperName(true)} you can have on a plot!")
                    return
                }

                val buildMode = BuildMode(player, plot, type, type.getLevels().first(), player.location)
                buildMode.start()

                BuildModeHandler.startTracking(player, buildMode)
            }
        }
    }

    companion object {
        private val BORDER_SLOTS = arrayListOf(9, 15, 17, 18, 24, 26, 27, 33, 35, 36, 42, 44).also {
            it.addAll(0..8)
            it.addAll(45..53)
        }

        private val GENERATOR_SLOTS = arrayListOf<Int>().also {
            it.addAll(10..14)
            it.addAll(19..23)
            it.addAll(28..32)
            it.addAll(37..41)
        }
    }

}