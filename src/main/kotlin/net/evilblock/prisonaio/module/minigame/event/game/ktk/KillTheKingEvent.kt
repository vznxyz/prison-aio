package net.evilblock.prisonaio.module.minigame.event.game.ktk

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.minigame.event.EventUtils
import net.evilblock.prisonaio.module.minigame.event.config.EventConfigHandler
import net.evilblock.prisonaio.module.minigame.event.game.EventGame
import net.evilblock.prisonaio.module.minigame.event.game.EventGameState
import net.evilblock.prisonaio.module.minigame.event.game.EventGameType
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

class KillTheKingEvent(val king: Player, host: UUID, arenaOptions: List<EventGameArena>) : EventGame(host, EventGameType.KILL_THE_KING, arenaOptions) {

    var kingKills: Int = 0
    var kingKiller: Player? = null

    override fun startGame() {
        super.startGame()

        Tasks.asyncTimer(object : BukkitRunnable() {
            override fun run() {
                if (state === EventGameState.ENDED) {
                    cancel()
                    return
                }

                if (state === EventGameState.RUNNING) {
                    startedAt = System.currentTimeMillis()

                    king.gameMode = GameMode.SURVIVAL
                    king.teleport(votedArena!!.pointB)

                    EventUtils.resetInventoryNow(king)
                    EventConfigHandler.config.ktkKingKit!!.giveToPlayer(king)

                    for (uuid in players) {
                        if (uuid != king.uniqueId) {
                            Bukkit.getPlayer(uuid)?.let { player ->
                                player.gameMode = GameMode.SURVIVAL
                                player.teleport(votedArena!!.pointA)
                                player.sendMessage("${ChatColor.GRAY}Prepare yourself to fight!")

                                EventUtils.resetInventoryNow(player)
                                EventConfigHandler.config.ktkAttackerKit!!.giveToPlayer(player)
                            }
                        }
                    }

                    Tasks.asyncTimer(object : BukkitRunnable() {
                        private var i = 6

                        override fun run() {
                            if (state === EventGameState.ENDED) {
                                cancel()
                                return
                            }

                            i--

                            if (i == 0) {
                                sendMessages("${ChatColor.GRAY}The hunt has started!")
                                sendSound(Sound.BLOCK_NOTE_PLING, 1f, 2f)
                            } else {
                                sendMessages("${ChatColor.GRAY}The hunt is starting in ${ChatColor.DARK_RED}${i} ${ChatColor.GRAY}${TextUtil.pluralize(i, "second", "seconds")}...")
                                sendSound(Sound.BLOCK_NOTE_PLING, 1f, 1f)
                            }

                            if (i <= 0) {
                                cancel()
                            }
                        }
                    }, 20L, 20L)

                    cancel()
                }
            }
        }, 10L, 10L)
    }

    override fun eliminatePlayer(player: Player, killer: Player?) {
        EventUtils.resetInventoryNow(player)

        if (killer == null) {
            if (player.uniqueId == king.uniqueId) {
                endGame()
                sendMessages("${ChatColor.RED}${ChatColor.BOLD}The king, ${ChatColor.DARK_RED}${player.name} ${ChatColor.RED}${ChatColor.BOLD}has disconnected, so the event has been cancelled!")
                return
            } else {
                removePlayer(player)
                sendMessages("${ChatColor.RED}${player.name} ${ChatColor.GRAY}has disconnected and has been disqualified! ${ChatColor.GRAY}(${ChatColor.RED}${getPlayers().size}/$startedWith${ChatColor.GRAY})")
            }
        } else {
            when {
                player.uniqueId == king.uniqueId -> {
                    this.kingKiller = killer

                    val messages = arrayListOf<String>()

                    messages.add("")
                    messages.add("${ChatColor.GRAY}█${ChatColor.DARK_RED}█████${ChatColor.GRAY}█")
                    messages.add("${ChatColor.DARK_RED}███████")
                    messages.add("${ChatColor.GRAY}█${ChatColor.DARK_RED}██${ChatColor.GRAY}█${ChatColor.DARK_RED}██${ChatColor.GRAY}█ ${ChatColor.DARK_RED}The King Has Fallen!")
                    messages.add("${ChatColor.DARK_RED}███████ ${ChatColor.DARK_RED}${king.name} ${ChatColor.GRAY}killed by ${ChatColor.RED}${killer.name}${ChatColor.GRAY}!")
                    messages.add("${ChatColor.GRAY}█${ChatColor.DARK_RED}█████${ChatColor.GRAY}█")
                    messages.add("${ChatColor.GRAY}█${ChatColor.DARK_RED}█${ChatColor.GRAY}█${ChatColor.DARK_RED}█${ChatColor.GRAY}█${ChatColor.DARK_RED}█${ChatColor.GRAY}█")
                    messages.add("")

                    sendMessages(*messages.toTypedArray())

                    endGame()
                }
                killer.uniqueId == king.uniqueId -> {
                    kingKills++

                    sendMessages("${ChatColor.RED}${player.name} ${ChatColor.GRAY}was killed by the king, ${ChatColor.DARK_RED}${killer.name}${ChatColor.GRAY}! (${ChatColor.RED}${NumberUtils.format(kingKills)} kills${ChatColor.GRAY})")
                }
                else -> {
                    sendMessages("${ChatColor.RED}${player.name} ${ChatColor.GRAY}was killed by ${ChatColor.RED}${killer.name}${ChatColor.GRAY}!")
                }
            }
        }
    }

    override fun handleRespawn(player: Player) {
        if (state == EventGameState.RUNNING && isPlaying(player.uniqueId)) {
            if (player.uniqueId != king.uniqueId) {
                Tasks.delayed(2L) {
                    player.teleport(votedArena!!.pointA)
                    EventConfigHandler.config.ktkAttackerKit!!.giveToPlayer(player)
                }
            }
        }
    }

    override fun handleDamage(victim: Player?, damager: Player?, event: EntityDamageByEntityEvent) {
        if (state === EventGameState.RUNNING) {
            if (!isPlaying(victim!!.uniqueId) || !isPlaying(damager!!.uniqueId)) {
                event.isCancelled = true
            }
        } else {
            event.isCancelled = true
        }
    }

    fun getDeathHeight(): Double {
        return max(votedArena!!.pointA!!.blockY, votedArena!!.pointB!!.blockY) - 2.9
    }

    override fun findWinningPlayer(): Player? {
        return kingKiller
    }

    fun sendMessagesToAttackers(vararg messages: String) {
        for (player in players) {
            if (player != king.uniqueId) {
                Bukkit.getPlayer(player)?.let {
                    for (message in messages) {
                        it.sendMessage(message)
                    }
                }
            }
        }
    }

    fun sendMessagesToKing(vararg messages: String) {
        for (message in messages) {
            king.sendMessage(message)
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
                lines.add("")
                lines.add("    &a&oStarting in " + (10.0 * remainingSeconds.toDouble()).roundToInt().toDouble() / 10.0 + "s")
            }
        } else if (state == EventGameState.RUNNING) {
            if (player.uniqueId == king.uniqueId) {
                lines.add("  &4&lYOU ARE THE KING!")
                lines.add("  &7Kills: &c&l${NumberUtils.format(kingKills)}")
                lines.add("  &7Attackers: &c&l${NumberUtils.format(players.size - 1)}&7/${NumberUtils.format(startedWith)}")
            } else {
                lines.add("  &4&lKILL ${king.name}")
                lines.add("  &7(&c${floor(king.location.x).toInt()}&7, &c${floor(king.location.y).toInt()}&7, &c${floor(king.location.z).toInt()}&7)")
                lines.add("")
                lines.add("  &7King's Kills: &c&l${NumberUtils.format(kingKills)}")
                lines.add("  &7Attackers: &c&l${NumberUtils.format(players.size - 1)}&7/${NumberUtils.format(startedWith)}")
            }
        } else {
            if (winningPlayer == null) {
                lines.add("  &c&lKING HAS DISCONNECTED")
                lines.add("  &7The event has been cancelled!")
            } else {
                lines.add("  &a&lTHE KING HAS FALLEN")
                lines.add("")
                lines.add("  &4&l${king.name}")
                lines.add("  &7killed by")
                lines.add("  &c&l${kingKiller!!.name}")
            }
        }
    }

    override fun createHostNotification(): List<FancyMessage> {
        return listOf(
            FancyMessage(""),
            FancyMessage("█████████").color(ChatColor.GRAY),
            FancyMessage("")
                .then("██").color(ChatColor.GRAY)
                .then("█████").color(ChatColor.DARK_RED)
                .then("██").color(ChatColor.GRAY),
            FancyMessage("")
                .then("█").color(ChatColor.GRAY)
                .then("███████").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY)
                .then(" ${ChatColor.DARK_RED}${ChatColor.BOLD}KILL THE KING"),
            FancyMessage("")
                .then("█").color(ChatColor.GRAY)
                .then("█").color(ChatColor.DARK_RED)
                .then("██").color(ChatColor.RED)
                .then("█").color(ChatColor.DARK_RED)
                .then("██").color(ChatColor.RED)
                .then("█").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY)
                .then(" ${ChatColor.GRAY}The King: ${ChatColor.DARK_RED}${ChatColor.BOLD}${king.name}"),
            FancyMessage("")
                .then("█").color(ChatColor.GRAY)
                .then("███████").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY)
                .then(" [").color(ChatColor.GRAY)
                .then("CLICK TO JOIN EVENT").color(ChatColor.GREEN).style(ChatColor.BOLD)
                .command("/events join")
                .formattedTooltip(FancyMessage("Click here to join the event.").color(ChatColor.YELLOW))
                .then("]").color(ChatColor.GRAY),
            FancyMessage("")
                .then("██").color(ChatColor.GRAY)
                .then("█████").color(ChatColor.DARK_RED)
                .then("██").color(ChatColor.GRAY),
            FancyMessage("")
                .then("██").color(ChatColor.GRAY)
                .then("█").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY)
                .then("█").color(ChatColor.DARK_RED)
                .then("█").color(ChatColor.GRAY)
                .then("█").color(ChatColor.DARK_RED)
                .then("██").color(ChatColor.GRAY)
        )
    }

}