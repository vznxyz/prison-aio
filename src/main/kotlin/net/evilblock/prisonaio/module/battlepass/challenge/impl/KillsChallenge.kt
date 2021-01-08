/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.impl

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeType
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

class KillsChallenge(id: String, internal var kills: Int) : Challenge(id) {

    override fun getText(): String {
        return "Kill ${NumberUtils.format(kills)} players in PvP"
    }

    override fun isProgressive(): Boolean {
        return true
    }

    override fun getProgressText(player: Player, user: User): String {
        val value = if (daily) {
            DailyChallengeHandler.getSession().getProgress(player.uniqueId).getKills()
        } else {
            user.statistics.getKills()
        }

        val percentage = ProgressBarBuilder.percentage(value, kills)

        val progressColor = ProgressBarBuilder.colorPercentage(percentage)
        val progressBar = ProgressBarBuilder.DEFAULT.build(percentage)

        return "${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}($progressColor${DECIMAL_FORMAT.format(percentage)}%${ChatColor.GRAY})"
    }

    override fun meetsCompletionRequirements(player: Player, user: User): Boolean {
        return if (daily) {
            DailyChallengeHandler.getSession().getProgress(player.uniqueId).getKills() >= kills
        } else {
            user.statistics.getKills() >= kills
        }
    }

    override fun getType(): ChallengeType {
        return KillsChallengeType
    }

    override fun getAbstractType(): Type {
        return KillsChallenge::class.java
    }

    object KillsChallengeType : ChallengeType {
        override fun getName(): String {
            return "Kill Players"
        }

        override fun getDescription(): String {
            return "Kill a given amount of players"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.SKULL_ITEM)
        }

        override fun startSetupPrompt(player: Player, id: String, lambda: (Challenge) -> Unit) {
            NumberPrompt().acceptInput { number ->
                assert(number.toInt() > 0)
                lambda.invoke(KillsChallenge(id, number.toInt()))
            }.start(player)
        }
    }

}