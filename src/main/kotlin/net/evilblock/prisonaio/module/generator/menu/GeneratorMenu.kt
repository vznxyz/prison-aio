/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class GeneratorMenu(private val generator: Generator) : Menu() {

    override fun getTitle(player: Player): String {
        return "Manage ${generator.getGeneratorType().getProperName()} (Level ${NumberUtils.format(generator.level)})"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in BORDER_SLOTS) {
                buttons[i] = GlassButton(7)
            }

            buttons[16] = UpgradeButton()
            buttons[43] = DestroyButton()
        }
    }

    private inner class UpgradeButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Upgrade ${generator.getGeneratorType().getProperName()} ${ChatColor.GRAY}(Lvl ${generator.level} -> ${generator.getNextLevel().number})"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val nextLevel = generator.getNextLevel()

                desc.add("${ChatColor.GRAY}Price: ${Formats.formatTokens(nextLevel.cost)}")
                desc.add("${ChatColor.GRAY}Time: ${ChatColor.RED}${ChatColor.BOLD}${TimeUtil.formatIntoAbbreviatedString(nextLevel.buildTime)}")
                desc.add("")
                desc.add("${ChatColor.YELLOW}Click to purchase upgrade")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 5
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ConfirmMenu { confirmed ->
                    if (!generator.build.finished) {
                        player.sendMessage("${ChatColor.RED}You can't upgrade your ${generator.getGeneratorType().getProperName()} until it's finished building!")
                        return@ConfirmMenu
                    }

                    if (confirmed) {
                        generator.level++
                        generator.startBuild()
                    }
                }.openMenu(player)
            }
        }
    }

    private inner class DestroyButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Destroy ${generator.getGeneratorType().getColoredName()}"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Remove this ${generator.getGeneratorType().getProperName()} from your plot. It and all of its contents will be lost forever."))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 14
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        generator.destroy()
                        GeneratorHandler.forgetGenerator(generator)
                    }
                }.openMenu(player)
            }
        }
    }

    companion object {
        private val BORDER_SLOTS = arrayListOf<Int>().also {
            it.addAll(0..8)
            it.add(9)
            it.add(15)
            it.add(17)
            it.add(18)
            it.add(24)
            it.add(25)
            it.add(26)
            it.add(27)
            it.add(33)
            it.add(34)
            it.add(35)
            it.add(36)
            it.add(42)
            it.add(44)
            it.addAll(45..53)
        }
    }

}