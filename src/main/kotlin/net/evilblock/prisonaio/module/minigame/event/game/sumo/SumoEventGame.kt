package net.evilblock.prisonaio.module.minigame.event.game.sumo

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.util.TextUtil
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.minigame.event.game.EventGame
import net.evilblock.prisonaio.module.minigame.event.game.EventGameState
import net.evilblock.prisonaio.module.minigame.event.game.EventGameType
import net.evilblock.prisonaio.module.minigame.event.EventUtils.resetInventoryNow
import net.evilblock.prisonaio.module.minigame.event.EventUtils.resetPlayer
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.roundToInt

class SumoEventGame(host: UUID, arenaOptions: List<EventGameArena>) : EventGame(host, EventGameType.SUMO, arenaOptions) {

    private var playerA: Player? = null
    private var playerB: Player? = null
    private val roundsPlayed: MutableMap<UUID, Int> = HashMap()
    private var currentRound = 0

    override fun startGame() {
        super.startGame()

        for (player in getPlayersAndSpectators()) {
            player.teleport(votedArena!!.spectatorLocation)
        }

        object : BukkitRunnable() {
            override fun run() {
                if (state === EventGameState.ENDED) {
                    cancel()
                    return
                }

                if (state === EventGameState.RUNNING) {
                    determineNextPlayers()
                    startRound()
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(PrisonAIO.instance, 10L, 10L)
    }

    fun startRound() {
        check(!(playerA == null || playerB == null)) { "Cannot start round without both players" }

        currentRound++
        startedAt = System.currentTimeMillis()

        resetInventoryNow(playerA!!)
        playerA!!.teleport(votedArena!!.pointA)

        resetInventoryNow(playerB!!)
        playerB!!.teleport(votedArena!!.pointB)

        object : BukkitRunnable() {
            private var i = 4

            override fun run() {
                if (state === EventGameState.ENDED) {
                    cancel()
                    return
                }

                i--

                if (i == 0) {
                    sendMessages("${ChatColor.GRAY}The round has started!")
                    sendSound(Sound.BLOCK_NOTE_PLING, 1f, 2f)
                } else {
                    sendMessages("${ChatColor.GRAY}The round is starting in ${ChatColor.DARK_RED}${i} ${ChatColor.GRAY}${TextUtil.pluralize(i, "second", "seconds")}...")
                    sendSound(Sound.BLOCK_NOTE_PLING, 1f, 1f)
                }

                if (i <= 0) {
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(PrisonAIO.instance, 20L, 20L)
    }

    fun endRound() {
        if (players.size == 1) {
            endGame()
        } else {
            if (playerA != null) {
                resetPlayer(playerA!!)
                playerA!!.teleport(votedArena!!.spectatorLocation)
                playerA = null
            }

            if (playerB != null) {
                resetPlayer(playerB!!)
                playerB!!.teleport(votedArena!!.spectatorLocation)
                playerB = null
            }

            Bukkit.getServer().scheduler.runTaskLater(PrisonAIO.instance, {
                determineNextPlayers()
                startRound()
            }, 50L)
        }
    }

    override fun eliminatePlayer(player: Player, killer: Player?) {
        super.eliminatePlayer(player, killer)

        if (killer != null) {
            sendMessages("${ChatColor.DARK_RED}${player.name} ${ChatColor.GRAY}has been eliminated by ${ChatColor.DARK_RED}${killer.name}${ChatColor.GRAY} (${ChatColor.RED}${getPlayers().size}/$startedWith${ChatColor.GRAY})")
        }

        if (isCurrentlyFighting(player)) {
            if (playerA!!.uniqueId === player.uniqueId) {
                playerA = null
            } else if (playerB!!.uniqueId === player.uniqueId) {
                playerB = null
            }

            endRound()
        }
    }

    fun isCurrentlyFighting(player: Player?): Boolean {
        return playerA != null && playerA!!.uniqueId === player!!.uniqueId || playerB != null && playerB!!.uniqueId === player!!.uniqueId
    }

    fun getOpponent(player: Player): Player? {
        if (playerA != null && playerA!!.uniqueId === player.uniqueId) {
            return playerB
        }

        return if (playerB != null && playerB!!.uniqueId === player.uniqueId) {
            playerA
        } else {
            null
        }
    }

    fun determineNextPlayers() {
        val players = getPlayers().stream().sorted(Comparator.comparingInt { player -> roundsPlayed.getOrDefault(player.uniqueId, 0) }).collect(Collectors.toList())

        playerA = players[0]
        playerB = players[1]

        roundsPlayed.putIfAbsent(playerA!!.uniqueId, 0)
        roundsPlayed[playerA!!.uniqueId] = roundsPlayed[playerA!!.uniqueId]!! + 1

        roundsPlayed.putIfAbsent(playerB!!.uniqueId, 0)
        roundsPlayed[playerB!!.uniqueId] = roundsPlayed[playerB!!.uniqueId]!! + 1

        sendMessages("${ChatColor.RED}${ChatColor.BOLD}Next round: ${ChatColor.DARK_RED}${playerA!!.name} ${ChatColor.GRAY}vs. ${ChatColor.DARK_RED}${playerB!!.name}")
    }

    fun getDeathHeight(): Double {
        return max(votedArena!!.pointA!!.blockY, votedArena!!.pointB!!.blockY) - 2.9
    }

    override fun handleDamage(victim: Player?, damager: Player?, event: EntityDamageByEntityEvent) {
        if (state === EventGameState.RUNNING) {
            if (isPlaying(victim!!.uniqueId) && isPlaying(damager!!.uniqueId)) {
                event.damage = 0.0

                if (!isCurrentlyFighting(victim) || !isCurrentlyFighting(damager)) {
                    event.isCancelled = true
                } else {
                    victim.health = victim.maxHealth
                    victim.updateInventory()
                }
            } else {
                event.isCancelled = true
            }
        } else {
            event.isCancelled = true
        }
    }

    override fun findWinningPlayer(): Player? {
        return if (playerA == null) {
            playerB
        } else {
            playerA
        }
    }

    override fun getScoreboardLines(player: Player, lines: LinkedList<String>) {
        if (state == EventGameState.WAITING) {
            lines.add("  &cPlayers: &f" + players.size + "&7/&f" + maxPlayers)

            if (votedArena != null) {
                lines.add("  &cMap: &f" + votedArena!!.name)
            } else {
                lines.add("")
                lines.add("  &cMap Vote")

                arenaOptions.entries.stream()
                    .sorted { o1, o2 -> o2.value.get() }
                    .forEach { entry -> lines.add("  &7» " + (if (playerVotes.getOrDefault(player.uniqueId, null) === entry.key) "&l" else "") + entry.key.name + " &7(" + entry.value.get() + ")") }
            }

            if (startedAt == null) {
                val playersNeeded: Int = gameType.minPlayers - getPlayers().size
                lines.add("")
                lines.add("    &e&oWaiting for " + playersNeeded + " player" + if (playersNeeded == 1) "" else "s")
            } else {
                val remainingSeconds: Float = (startedAt!! - System.currentTimeMillis()) / 1000f
                lines.add("    &a&oStarting in " + (10.0 * remainingSeconds.toDouble()).roundToInt().toDouble() / 10.0 + "s")
            }
        } else if (state == EventGameState.RUNNING) {
            lines.add("  &cRemaining: &f" + players.size + "&7/&f" + startedWith)
            lines.add("  &cRound: &f$currentRound")

            if (playerA != null && playerB != null) {
                lines.add("")
                lines.add("  " + playerA!!.name)
                lines.add("  &7vs.")
                lines.add("  " + playerB!!.name)
            } else {
                lines.add("")
                lines.add("  &e&oSelecting new players...")
            }
        } else {
            if (winningPlayer == null) {
                lines.add("  &cWinner: &fNone")
            } else {
                lines.add("  &cWinner: &f" + winningPlayer!!.name)
            }

            lines.add("  &cRounds: &f$currentRound")
        }
    }

    override fun createHostNotification(): List<FancyMessage> {
        return listOf(
            FancyMessage(""),
            FancyMessage("███████").color(ChatColor.GRAY),
            FancyMessage("")
                .then("█").color(ChatColor.GRAY)
                .then("█████").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY),
            FancyMessage("")
                .then("█").color(ChatColor.GRAY)
                .then("█").color(ChatColor.DARK_RED)
                .then("█████").color(ChatColor.GRAY)
                .then(" Sumo Event").color(ChatColor.DARK_RED).style(ChatColor.BOLD),
            FancyMessage("")
                .then("█").color(ChatColor.GRAY)
                .then("█████").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY)
                .then(" Hosted by ").color(ChatColor.GRAY)
                .then(getHostUsername()).color(ChatColor.RED),
            FancyMessage("")
                .then("█████").color(ChatColor.GRAY)
                .then("█").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY)
                .then(" [").color(ChatColor.GRAY)
                .then("CLICK TO JOIN EVENT").color(ChatColor.GREEN).style(ChatColor.BOLD)
                .command("/events join")
                .formattedTooltip(FancyMessage("Click here to join the event.").color(ChatColor.YELLOW))
                .then("]").color(ChatColor.GRAY),
            FancyMessage("")
                .then("█").color(ChatColor.GRAY)
                .then("█████").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY),
            FancyMessage("███████").color(ChatColor.GRAY)
        )
    }

}