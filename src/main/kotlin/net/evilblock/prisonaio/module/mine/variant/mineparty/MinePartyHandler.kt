package net.evilblock.prisonaio.module.mine.variant.mineparty

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.Bukkit
import org.bukkit.ChatColor

object MinePartyHandler {

    val SIMPLE_NAME = "${ChatColor.WHITE}${ChatColor.BOLD}MINE${ChatColor.AQUA}${ChatColor.BOLD}PARTY"
    val CHAT_NAME = "${ChatColor.WHITE}${ChatColor.BOLD}${ChatColor.UNDERLINE}MINE${ChatColor.AQUA}${ChatColor.BOLD}${ChatColor.UNDERLINE}PARTY${ChatColor.RESET}"

    val MAGIC_NAME = "${ChatColor.DARK_AQUA}${ChatColor.MAGIC}/${ChatColor.RESET} $CHAT_NAME ${ChatColor.DARK_AQUA}${ChatColor.MAGIC}\\"
    val FINISH_NAME = "${ChatColor.GREEN}${ChatColor.BOLD}${Constants.CHECK_SYMBOL} $CHAT_NAME ${ChatColor.GREEN}${ChatColor.BOLD}${Constants.CHECK_SYMBOL}"

    val CHAT_PREFIX = "${ChatColor.GRAY}[${CHAT_NAME}${ChatColor.GRAY}] "

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
        mine.progress = 0
        mine.startedAt = System.currentTimeMillis()
        mine.duration = duration
        mine.active = true

        activeMineParty = mine

        val messages = arrayListOf<FancyMessage>().also { messages ->
            val formattedGoal = NumberUtils.format(goal)
            val formattedTime = TimeUtil.formatIntoAbbreviatedString((duration.get() / 1000.0).toInt())

            messages.add(FancyMessage(""))
            messages.add(FancyMessage(" $MAGIC_NAME"))
            messages.add(FancyMessage(""))
            messages.add(FancyMessage(" ${ChatColor.WHITE}You have ${ChatColor.AQUA}$formattedTime ${ChatColor.WHITE}to mine ${ChatColor.AQUA}$formattedGoal ${ChatColor.WHITE}blocks at"))
            messages.add(FancyMessage(" ${ChatColor.AQUA}/mineparty ${ChatColor.WHITE}to win rewards for everyone!"))
            messages.add(FancyMessage(""))

            messages.add(
                FancyMessage("")
                    .then(" ${ChatColor.GRAY}[${ChatColor.GREEN}${ChatColor.BOLD}JOIN EVENT${ChatColor.GRAY}]")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to teleport to the Mine Party event!"))
                    .command("/mineparty")
            )

            messages.add(FancyMessage(""))
        }

        for (player in Bukkit.getOnlinePlayers()) {
            for (msg in messages) {
                msg.send(player)
            }

            player.sendTitle(MAGIC_NAME, "${ChatColor.WHITE}The event has started!", 10, 80, 10)
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
            msgs.add(" ${ChatColor.GREEN}${ChatColor.BOLD}${Constants.CHECK_SYMBOL} $CHAT_NAME ${ChatColor.GREEN}${ChatColor.BOLD}${Constants.CHECK_SYMBOL}")
            msgs.add("")
            msgs.add(" ${ChatColor.GRAY}The block goal has been reached!")
            msgs.add(" ${ChatColor.GRAY}All players that contributed have been rewarded!")
            msgs.add("")
        }

        for (player in event.getNearbyPlayers()) {
            for (msg in messages) {
                player.sendMessage(msg)
            }

            player.sendTitle(FINISH_NAME, "${ChatColor.GREEN}The block goal has been reached!", 10, 50, 10)
        }
    }

    fun cancelEvent() {
        if (activeMineParty != null) {
            val event = activeMineParty!!
            activeMineParty = null

            for (player in event.getNearbyPlayers()) {
                player.sendMessage("${CHAT_PREFIX}The event has been cancelled by an admin!")
                player.sendTitle(MAGIC_NAME, "${ChatColor.RED}The event has been cancelled!", 10, 50, 10)
            }
        }
    }

}