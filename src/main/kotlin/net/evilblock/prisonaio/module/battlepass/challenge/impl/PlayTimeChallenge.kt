/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.impl

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.prompt.DurationPrompt
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeType
import net.evilblock.prisonaio.module.battlepass.challenge.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import kotlin.math.roundToInt

class PlayTimeChallenge(id: String, internal var duration: Long) : Challenge(id) {

    override fun getText(): String {
        return "Play on the server for ${TimeUtil.formatIntoDetailedString((duration / 1000.0).toInt())}"
    }

    override fun isProgressive(): Boolean {
        return true
    }

    override fun getProgressText(player: Player, user: User): String {
        val value = if (daily) {
            (DailyChallengeHandler.getSession().getProgress(player.uniqueId).getPlayTime() / 1000.0).roundToInt()
        } else {
            (user.statistics.getLivePlayTime() / 1000.0).roundToInt()
        }

        val goal = (duration / 1000.0).toInt()
        val percentage = ProgressBarBuilder.percentage(value, goal)

        val progressColor = ProgressBarBuilder.colorPercentage(percentage)
        val progressBar = ProgressBarBuilder().build(percentage)

        return "${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}($progressColor${DECIMAL_FORMAT.format(percentage)}%${ChatColor.GRAY})"
    }

    override fun getType(): ChallengeType {
        return PlayTimeChallengeChallengeType
    }

    override fun getAbstractType(): Type {
        return PlayTimeChallenge::class.java
    }

    object PlayTimeChallengeChallengeType : ChallengeType {
        override fun getName(): String {
            return "Play Time"
        }

        override fun getDescription(): String {
            return "Reach a certain amount of time played"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.WATCH)
        }

        override fun startSetupPrompt(player: Player, id: String, lambda: (Challenge) -> Unit) {
            DurationPrompt { duration ->
                assert(duration > 0)

                lambda.invoke(PlayTimeChallenge(id, duration))
            }.start(player)
        }
    }

}