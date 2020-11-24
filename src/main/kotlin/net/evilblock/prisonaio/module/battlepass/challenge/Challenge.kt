/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge

import net.evilblock.cubed.serialize.AbstractTypeSerializable
import net.evilblock.prisonaio.module.battlepass.BattlePassModule
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat

abstract class Challenge(val id: String, internal var daily: Boolean = false) : AbstractTypeSerializable {

    companion object {
        internal val DECIMAL_FORMAT = DecimalFormat("#.##")

        init {
            DECIMAL_FORMAT.roundingMode = RoundingMode.HALF_UP
        }
    }

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

    abstract fun meetsCompletionRequirements(player: Player, user: User): Boolean

    fun onComplete(player: Player, user: User) {
        val formattedExp = NumberFormat.getInstance().format(rewardXp)
        player.sendMessage("${BattlePassModule.CHAT_PREFIX}You have completed the ${ChatColor.YELLOW}$name ${ChatColor.GRAY}challenge! (${ChatColor.GREEN}+$formattedExp XP${ChatColor.GRAY})")

        val nextTier = user.battlePassProgress.getNextTier()

        user.battlePassProgress.completeChallenge(this)

        if (user.battlePassProgress.getNextTier() != nextTier && nextTier != null) {
            var newRewards = false

            if (nextTier.freeReward != null) {
                newRewards = true
            }

            if (nextTier.premiumReward != null && user.battlePassProgress.isPremium()) {
                newRewards = true
            }

            if (newRewards) {
                player.sendMessage("${BattlePassModule.CHAT_PREFIX}You have reached ${ChatColor.GOLD}${ChatColor.BOLD}Tier ${nextTier.number}${ChatColor.GRAY}! You have new rewards waiting to be collected!")
            } else {
                player.sendMessage("${BattlePassModule.CHAT_PREFIX}You have reached ${ChatColor.GOLD}${ChatColor.BOLD}Tier ${nextTier.number}${ChatColor.GRAY}!")
            }
        }
    }

    open fun isSetup(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        return other is Challenge && other.id == this.id && other.daily == this.daily
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + daily.hashCode()
        return result
    }

}