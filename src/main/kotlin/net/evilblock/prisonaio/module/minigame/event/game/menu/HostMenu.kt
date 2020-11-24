/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.minigame.event.game.EventGameType
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.util.*

class HostMenu : Menu() {

    override fun getTitle(player: Player): String {
        return "Host an Event"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (gameType in EventGameType.values()) {
            buttons[buttons.size] = GameTypeButton(gameType)
        }

        return buttons
    }

    private inner class GameTypeButton(private val gameType: EventGameType) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}${gameType.displayName}"
        }

        override fun getDescription(player: Player): List<String> {
            val description: MutableList<String> = ArrayList()
            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}HOW TO PLAY")
            description.addAll(TextSplitter.split(text = gameType.description))
            description.add("")

            if (gameType.canHost(player)) {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}UNLOCKED")
                description.add("${ChatColor.GREEN}Click to host this event!")
            } else {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LOCKED")
                description.add("${ChatColor.RED}Purchase a rank to unlock access")
                description.add("${ChatColor.RED}to this event!")
                description.add("")
                description.add("${ChatColor.LIGHT_PURPLE}store.minejunkie.com")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return gameType.icon.type
        }

        override fun getDamageValue(player: Player): Byte {
            return gameType.icon.durability.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (!gameType.canHost(player)) {
                    player.sendMessage("${ChatColor.RED}You don't have permission to host ${gameType.displayName} events.")
                    return
                }

                if (!EventGameHandler.canStartGame(player, gameType)) {
                    return
                }

                try {
                    player.closeInventory()

                    EventGameHandler.createGame(player, gameType) { game ->
                        game.addPlayer(player)
                    }
                } catch (e: IllegalStateException) {
                    player.sendMessage("${ChatColor.RED}${e.message}")
                }
            }
        }
    }
}