package net.evilblock.prisonaio.module.minigame.event.game.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.minigame.event.game.EventGame
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class MapVoteMenu(private val game: EventGame) : Menu() {

    override fun getTitle(player: Player): String {
        return "Map Votes"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()
        for ((key, value) in game.arenaOptions) {
            buttons[buttons.size] = MapButton(key, value)
        }
        return buttons
    }

    private inner class MapButton(private val arena: EventGameArena, private val votes: AtomicInteger) : Button() {

        override fun getName(player: Player): String {
            return ChatColor.YELLOW.toString() + ChatColor.BOLD + arena.name
        }

        override fun getDescription(player: Player): List<String> {
            val description: MutableList<String> = ArrayList()
            description.add("${ChatColor.GRAY}This map has ${ChatColor.GREEN}${votes.get()} ${ChatColor.GRAY}votes.")

            if (game.playerVotes.containsKey(player.uniqueId) && game.playerVotes[player.uniqueId] == arena) {
                description.add("${ChatColor.GRAY}You voted for this map!")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.EMPTY_MAP
        }
    }

}