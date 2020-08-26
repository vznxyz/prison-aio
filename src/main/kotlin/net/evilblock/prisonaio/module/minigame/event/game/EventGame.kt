/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game

import com.google.common.collect.ImmutableList
import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.minigame.event.*
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import net.evilblock.prisonaio.module.minigame.event.game.menu.MapVoteMenu
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

abstract class EventGame(val host: UUID, val gameType: EventGameType, arenaOptions: List<EventGameArena>) {

    var state = EventGameState.WAITING

    private var nextAnnouncement = System.currentTimeMillis()

    internal var startedAt: Long? = null
    internal var startedWith = 0

    internal var maxPlayers: Int = gameType.maxPlayers
    private val usedMessage: MutableSet<UUID> = HashSet()

    val arenaOptions: MutableMap<EventGameArena, AtomicInteger> = HashMap()
    internal val playerVotes: Map<UUID, EventGameArena> = HashMap()
    internal var votedArena: EventGameArena? = null

    protected var players: MutableSet<UUID> = HashSet()
    private val spectators: MutableSet<UUID> = HashSet()

    protected var winningPlayer: Player? = null

    init {
        if (arenaOptions.size == 1) {
            votedArena = arenaOptions[0]
        } else {
            for (arena in arenaOptions) {
                this.arenaOptions[arena] = AtomicInteger(0)
            }
        }
    }

    fun forceStart() {
        state = EventGameState.RUNNING
        startedWith = players.size

        for (player in getPlayersAndSpectators()) {
            player.teleport(votedArena!!.spectatorLocation)
        }

        sendMessages("${ChatColor.GOLD}The event has been forcefully started by an administrator!")
    }

    open fun startGame() {
        for (player in getPlayersAndSpectators()) {
            player.teleport(votedArena!!.spectatorLocation)
        }

        object : BukkitRunnable() {
            override fun run() {
                if (state !== EventGameState.WAITING) {
                    cancel()
                    return
                }

                if (System.currentTimeMillis() > nextAnnouncement) {
                    nextAnnouncement = System.currentTimeMillis() + 15000L

                    val notificationMessages = createHostNotification()
                    for (player in Bukkit.getOnlinePlayers()) {
                        for (message in notificationMessages) {
                            message.send(player)
                        }
                    }
                }

                if (startedAt == null) {
                    if (players.size >= gameType.minPlayers) {
                        startedAt = System.currentTimeMillis() + 30000L
                    }
                } else {
                    if (System.currentTimeMillis() > startedAt!!) {
                        state = EventGameState.RUNNING
                        startedWith = players.size
                    } else {
                        if (System.currentTimeMillis() > startedAt!! - 5000L && votedArena == null) {
                            votedArena = arenaOptions.entries.stream().sorted { o1, o2 -> o1.value.get() }.collect(Collectors.toList())[0].key
                            sendMessages("${ChatColor.GOLD}${votedArena!!.name} ${ChatColor.YELLOW}has won the map vote!")
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(PrisonAIO.instance, 10L, 10L)

        object : BukkitRunnable() {
            private val expiresAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3L)

            override fun run() {
                if (state !== EventGameState.WAITING) {
                    cancel()
                    return
                }

                if (System.currentTimeMillis() >= expiresAt) {
                    sendMessages(ChatColor.DARK_RED.toString() + "The event has been cancelled because it couldn't get enough players!")
                    endGame()

                    EventGameHandler.endGame()

                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(PrisonAIO.instance, 10L, 10L)
    }

    fun endGame() {
        state = EventGameState.ENDED
        winningPlayer = findWinningPlayer()

        object : BukkitRunnable() {
            private var i = 5

            override fun run() {
                i--

                if (i in 1..3 && winningPlayer != null) {
                    sendMessages("${ChatColor.GREEN}${ChatColor.BOLD}Winner: ${winningPlayer!!.displayName} ${ChatColor.GRAY}won the ${gameType.displayName} event!")
                }

                if (i <= 0) {
                    cancel()

                    // this block of code should never throw errors, but just in case it does,
                    // lets wrap in a try-catch so the game gets cleared from the game handler
                    try {
                        object : BukkitRunnable() {
                            override fun run() {
                                for (player in getPlayers()) {
                                    removePlayer(player)
                                }

                                for (spectator in getSpectators()) {
                                    removeSpectator(spectator)
                                }
                            }
                        }.runTask(PrisonAIO.instance)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    EventGameHandler.endGame()
                }
            }
        }.runTaskTimerAsynchronously(PrisonAIO.instance, 20L, 20L)
    }

    fun isPlaying(player: UUID?): Boolean {
        return players.contains(player)
    }

    fun isSpectating(player: UUID?): Boolean {
        return spectators.contains(player)
    }

    fun isPlayingOrSpectating(player: UUID?): Boolean {
        return isPlaying(player) || isSpectating(player)
    }

    @Throws(IllegalStateException::class)
    fun addPlayer(player: Player) {
        check(!(state !== EventGameState.WAITING)) { "That event has already started. Try spectating instead with /event spectate." }
        check(players.size < maxPlayers) { "The event is full!" }

        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't join the event while combat tagged!")
            return
        }

        if (!EventUtils.hasEmptyInventory(player)) {
            player.sendMessage("${ChatColor.RED}You need to have an empty inventory to join the event!")
            return
        }

        players.add(player.uniqueId)

        EventUtils.resetPlayer(player)

        if (EventConfig.lobbyLocation != null) {
            player.teleport(EventConfig.lobbyLocation)
        }

        if (!usedMessage.contains(player.uniqueId)) {
            sendMessages("${player.displayName} ${ChatColor.GRAY}has joined the ${ChatColor.RED}${ChatColor.BOLD}${gameType.displayName} ${ChatColor.GRAY}event! (${ChatColor.RED}${players.size}/${maxPlayers}${ChatColor.GRAY})")
        }

        if (votedArena == null && arenaOptions.size > 1) {
            MapVoteMenu(this).openMenu(player)
        }
    }

    @Throws(IllegalStateException::class)
    fun forceAddPlayer(player: Player) {
        check(!CombatTimerHandler.isOnTimer(player)) { "You can't join the event while combat tagged!" }
        check(EventUtils.hasEmptyInventory(player)) { "You need to have an empty inventory to join the event!" }

        players.add(player.uniqueId)

        EventUtils.resetPlayer(player)

        if (EventConfig.lobbyLocation != null) {
            player.teleport(EventConfig.lobbyLocation)
        }

        if (votedArena == null && arenaOptions.size > 1) {
            MapVoteMenu(this).openMenu(player)
        }
    }

    fun removePlayer(player: Player) {
        players.remove(player.uniqueId)

        EventUtils.resetInventoryNow(player)
        player.teleport(PrisonAIO.instance.getSpawnLocation())

        if (state === EventGameState.WAITING && !usedMessage.contains(player.uniqueId)) {
            usedMessage.add(player.uniqueId)
            sendMessages("${player.displayName} ${ChatColor.GRAY}has left the ${ChatColor.RED}${ChatColor.BOLD}${gameType.displayName} ${ChatColor.GRAY}event! (${ChatColor.RED}${players.size}/$maxPlayers${ChatColor.GRAY})")
        }
    }

    open fun eliminatePlayer(player: Player, killer: Player?) {
        players.remove(player.uniqueId)

        EventUtils.resetInventoryNow(player)

        if (killer == null) {
            sendMessages("${ChatColor.DARK_RED}${player.name} ${ChatColor.GRAY}has disconnected and has been disqualified! ${ChatColor.GRAY}(${ChatColor.RED}${getPlayers().size}/$startedWith${ChatColor.GRAY})")
        }

        if (killer != null) {
            addSpectator(player)
        }
    }

    fun addSpectator(player: Player) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't join the event while combat tagged!")
            return
        }

        if (!EventUtils.hasEmptyInventory(player)) {
            player.sendMessage("${ChatColor.RED}You need to have an empty inventory to join the event!")
            return
        }

        spectators.add(player.uniqueId)

        EventUtils.resetPlayer(player)
        player.teleport(votedArena!!.spectatorLocation)
    }

    fun removeSpectator(player: Player) {
        spectators.remove(player.uniqueId)

        EventUtils.resetInventoryNow(player)
        player.teleport(PrisonAIO.instance.getSpawnLocation())
    }

    fun getHostUsername(): String {
        return Cubed.instance.uuidCache.name(host)
    }

    fun getPlayers(): List<Player> {
        if (players.isEmpty()) {
            return ImmutableList.of()
        }

        val players: MutableList<Player> = ArrayList()
        for (uuid in this.players) {
            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                players.add(player)
            }
        }

        return players
    }

    fun getSpectators(): List<Player> {
        if (spectators.isEmpty()) {
            return ImmutableList.of()
        }

        val spectators: MutableList<Player> = ArrayList()
        for (uuid in this.spectators) {
            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                spectators.add(player)
            }
        }

        return spectators
    }

    fun getPlayersAndSpectators(): List<Player> {
        val playersAndSpectators: MutableList<Player> = ArrayList()
        playersAndSpectators.addAll(getPlayers())
        playersAndSpectators.addAll(getSpectators())
        return playersAndSpectators
    }

    fun sendMessages(vararg messages: String?) {
        for (player in getPlayersAndSpectators()) {
            for (message in messages) {
                player.sendMessage(message)
            }
        }
    }

    fun sendSound(sound: Sound?, volume: Float, pitch: Float) {
        for (player in getPlayersAndSpectators()) {
            player.playSound(player.location, sound, volume, pitch)
        }
    }

    open fun handleDamage(victim: Player?, damager: Player?, event: EntityDamageByEntityEvent) {
        event.isCancelled = true
    }

    abstract fun findWinningPlayer(): Player?
    abstract fun getScoreboardLines(player: Player, lines: LinkedList<String>)
    abstract fun createHostNotification(): List<FancyMessage>

}