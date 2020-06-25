/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge

import net.evilblock.cubed.serialize.AbstractTypeSerializable
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat

abstract class Challenge(val id: String, internal var daily: Boolean = false) : AbstractTypeSerializable {

    var name: String = "Default name"

    internal var rewardXp: Int = 0

    abstract fun getType(): ChallengeType

    abstract fun getText(): String

    open fun isProgressive(): Boolean {
        return false
    }

    open fun getProgressText(player: Player, user: User): String {
        return ""
    }

    fun onComplete(player: Player, user: User) {
        val formattedExp = NumberFormat.getInstance().format(rewardXp)
        player.sendMessage("$CHAT_PREFIX You have completed the ${ChatColor.YELLOW}$name ${ChatColor.GRAY}challenge! (${ChatColor.GREEN}+$formattedExp XP${ChatColor.GRAY})")

        val nextTier = user.battlePassData.getNextTier()

        user.battlePassData.completeChallenge(this)

        if (user.battlePassData.getNextTier() != nextTier && nextTier != null) {
            var newRewards = false

            if (nextTier.freeReward != null) {
                newRewards = true
                user.battlePassData.addUnclaimedReward(nextTier.freeReward!!)
            }

            if (nextTier.premiumReward != null) {
                newRewards = true
                user.battlePassData.addUnclaimedReward(nextTier.premiumReward!!)
            }

            if (newRewards) {
                player.sendMessage("$CHAT_PREFIX Congratulations! You have reached ${ChatColor.GOLD}${ChatColor.BOLD}Tier ${nextTier.number}${ChatColor.GRAY}! You have new rewards waiting to be collected in the JunkiePass.")
            } else {
                player.sendMessage("$CHAT_PREFIX Congratulations! You have reached ${ChatColor.GOLD}${ChatColor.BOLD}Tier ${nextTier.number}${ChatColor.GRAY}!")
            }
        }
    }

    companion object {
        private val CHAT_PREFIX = "${ChatColor.GRAY}[${ChatColor.GOLD}${ChatColor.BOLD}JunkiePass${ChatColor.GRAY}]"
        internal val DECIMAL_FORMAT = DecimalFormat("#.##")

        init {
            DECIMAL_FORMAT.roundingMode = RoundingMode.HALF_UP
        }
    }

}