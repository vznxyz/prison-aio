/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.arena.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.minigame.event.game.EventGameType
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArenaHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta

class EditArenaMenu(private val arena: EventGameArena) : Menu() {

    init {
        updateAfterClick = true
        placeholder = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Arena ${arena.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[1] = EditNameButton()
        buttons[3] = EditCompatibleGamesButton()

        return buttons
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "The name is how you want the arena to appear in chat and menu text."))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit name")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .acceptInput { input ->
                        if (EventGameArenaHandler.getArenaByName(input) != null) {
                            player.sendMessage("${ChatColor.RED}That name is taken!")
                            return@acceptInput
                        }

                        arena.name = input

                        Tasks.async {
                            EventGameArenaHandler.saveData()
                        }

                        this@EditArenaMenu.openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class EditCompatibleGamesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Event Compatibility"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "Manage which type of events can use this arena."))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit event compatibility")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.HOPPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditArenaEventCompatibilityMenu().openMenu(player)
            }
        }
    }

    private inner class EditArenaEventCompatibilityMenu : Menu() {
        init {
            updateAfterClick = true
        }

        override fun getTitle(player: Player): String {
            return "Edit Arena Compatibility - ${arena.name}"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            for (gameType in EventGameType.values()) {
                buttons[buttons.size] = EventGameTypeButton(gameType)
            }

            return buttons
        }

        override fun onClose(player: Player, manualClose: Boolean) {
            if (manualClose) {
                Tasks.delayed(1L) {
                    this@EditArenaMenu.openMenu(player)
                }
            }
        }

        private inner class EventGameTypeButton(private val gameType: EventGameType) : Button() {
            override fun getName(player: Player): String {
                return if (arena.isCompatible(gameType)) {
                    "${ChatColor.GREEN}${ChatColor.BOLD}${gameType.displayName}"
                } else {
                    "${ChatColor.RED}${ChatColor.BOLD}${gameType.displayName}"
                }
            }

            override fun getDescription(player: Player): List<String> {
                return if (arena.isCompatible(gameType)) {
                    listOf("${ChatColor.GRAY}Click to make this event incompatible")
                } else {
                    listOf("${ChatColor.GRAY}Click to make this event compatible")
                }
            }

            override fun getMaterial(player: Player): Material {
                return if (arena.isCompatible(gameType)) {
                    Material.GREEN_RECORD
                } else {
                    Material.RECORD_4
                }
            }

            override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                return itemMeta
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    if (arena.isCompatible(gameType)) {
                        arena.makeIncompatible(gameType)
                    } else {
                        arena.makeCompatible(gameType)
                    }

                    Tasks.async {
                        EventGameArenaHandler.saveData()
                    }
                }
            }
        }
    }

}