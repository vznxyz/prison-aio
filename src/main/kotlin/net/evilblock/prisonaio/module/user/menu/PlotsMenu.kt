/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.menu

import com.intellectualcrafters.plot.PS
import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotPlayer
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class PlotsMenu : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.DARK_GREEN}${ChatColor.BOLD}Plots"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = BackButton { MainMenu(UserHandler.getUser(player)).openMenu(player) }
            buttons[4] = InfoButton()

            val plots = PS.get().getPlots(PlotPlayer.wrap(player))
            if (plots.isEmpty()) {
                buttons[22] = CreatePlotButton()
            } else {
                for ((index, plot) in plots.withIndex()) {
                    buttons[19 + index] = PlotButton(plot)
                }
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                MainMenu(UserHandler.getUser(player)).openMenu(player)
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.DARK_GREEN}${ChatColor.BOLD}Plots"
        }

        override fun getMaterial(player: Player): Material {
            return Material.GRASS
        }
    }

    private inner class CreatePlotButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}Create Plot"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to create a plot"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()
                player.performCommand("plot auto")
            }
        }
    }

    private inner class PlotButton(private val plot: Plot) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Plot #${plot.id}"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("${ChatColor.YELLOW}Members${ChatColor.WHITE}: ${ChatColor.AQUA}${plot.members.size}")
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to teleport to plot"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENDER_PEARL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                plot.teleportPlayer(PlotPlayer.wrap(player))
            }
        }
    }

}