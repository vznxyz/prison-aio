package net.evilblock.prisonaio.module.mine.variant.mineparty

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MinePartyHandler {

    val EVENT_NAME = "${ChatColor.WHITE}${ChatColor.BOLD}Mine${ChatColor.AQUA}${ChatColor.BOLD}Party"
    val CHAT_PREFIX = "${ChatColor.GRAY}[${EVENT_NAME}${ChatColor.GRAY}] "

    private var activeMineParty: MinePartyMine? = null

    fun isEventActive(): Boolean {
        return activeMineParty != null
    }

    fun getEvent(): MinePartyMine? {
        return activeMineParty
    }

    fun startEvent(mine: MinePartyMine, goal: Int, duration: Duration) {
        mine.resetRegion()

        mine.goal = goal
        mine.progress = goal
        mine.startedAt = System.currentTimeMillis()
        mine.active = true

        activeMineParty = mine

        val messages = arrayListOf<FancyMessage>().also { msgs ->
            val formattedTime = TimeUtil.formatIntoAbbreviatedString((duration.get() / 1000.0).toInt())

            msgs.add(FancyMessage(""))
            msgs.add(FancyMessage("      $EVENT_NAME ${ChatColor.GRAY}($formattedTime)"))
            msgs.add(FancyMessage(""))
            msgs.add(FancyMessage(" ${ChatColor.GRAY}Time is of the essence! Mine the block"))

            msgs.add(FancyMessage(" ${ChatColor.GRAY}goal of ${NumberUtils.format(goal)}")
                .then("${ChatColor.AQUA}${ChatColor.BOLD}/mineparty")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to teleport to the MineParty event!"))
                .command("/mineparty")
                .then("${ChatColor.GRAY} to win a reward!"))

            msgs.add(FancyMessage(""))
        }

        for (player in Bukkit.getOnlinePlayers()) {
            for (msg in messages) {
                msg.send(player)
            }

            player.sendTitle(EVENT_NAME, "${ChatColor.WHITE}The event has started!", 1, 5, 1)
        }
    }

    fun finishEvent() {
        if (activeMineParty == null) {
            return
        }

        val event = activeMineParty!!
        event.active = false
        event.goal = 0
        event.progress = 0
        event.startedAt = -1

        activeMineParty = null

        val messages = arrayListOf<String>().also { msgs ->
            msgs.add("")
            msgs.add("      ${ChatColor.GREEN}${ChatColor.BOLD}${Constants.CHECK_SYMBOL} $EVENT_NAME ${ChatColor.GREEN}${ChatColor.BOLD}${Constants.CHECK_SYMBOL}")
            msgs.add("")
            msgs.add(" ${ChatColor.GRAY}The block goal has been reached!")
            msgs.add(" ${ChatColor.GRAY}All players that contributed have been rewarded!")
            msgs.add("")
        }

        for (player in getPlayersInEvent()) {
            for (msg in messages) {
                player.sendMessage(msg)
            }

            player.sendTitle(EVENT_NAME, "${ChatColor.GREEN}The block goal has been reached!", 1, 5, 1)
        }
    }

    fun cancelEvent() {
        if (activeMineParty != null) {
            activeMineParty = null

            for (player in getPlayersInEvent()) {
                player.sendMessage("${CHAT_PREFIX}The event has been cancelled by an admin!")
                player.sendTitle(EVENT_NAME, "${ChatColor.RED}The event has been cancelled!", 1, 5, 1)
            }
        }
    }

    fun getPlayersInEvent(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { activeMineParty!!.isNearbyMine(it) }
    }

}