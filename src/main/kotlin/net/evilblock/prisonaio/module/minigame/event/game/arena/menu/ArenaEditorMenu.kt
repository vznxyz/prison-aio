/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.arena.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArenaHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ArenaEditorMenu : Menu() {

    override fun getTitle(player: Player): String {
        return "Event Arenas"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = AddArenaButton()

        for (i in 9..17) {
            buttons[i] = GlassButton(0)
        }

        for ((index, arena) in EventGameArenaHandler.getArenas().withIndex()) {
            buttons[18 + index] = ArenaButton(arena)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class AddArenaButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Arena"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(linePrefix = ChatColor.GRAY.toString(), text = "Create a new arena by following the setup procedure."))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create a new arena")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText("${ChatColor.GREEN}Please input a name for the arena.")
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .charLimit(32)
                    .acceptInput { _, input ->
                        if (EventGameArenaHandler.getArenaByName(input) != null) {
                            player.sendMessage("${ChatColor.RED}That name is taken!")
                            return@acceptInput
                        }

                        val arena = EventGameArena(input)

                        Tasks.async {
                            EventGameArenaHandler.trackArena(arena)
                            EventGameArenaHandler.saveData()
                        }

                        EditArenaMenu(arena).openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class ArenaButton(private val arena: EventGameArena) : Button() {
        override fun getName(player: Player): String {
            return if (arena.isSetup()) {
                "${ChatColor.GREEN}${ChatColor.BOLD}${arena.name}"
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}${arena.name}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.add("${ChatColor.GRAY}Setup: ${ if (arena.isSetup()) "${ChatColor.GREEN}${ChatColor.BOLD}yes" else "${ChatColor.RED}${ChatColor.BOLD}no" }")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit arena")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.EMPTY_MAP
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditArenaMenu(arena).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        EventGameArenaHandler.forgetArena(arena)
                    }

                    this@ArenaEditorMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}