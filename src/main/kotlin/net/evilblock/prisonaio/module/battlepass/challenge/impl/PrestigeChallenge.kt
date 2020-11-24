/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.impl

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.bukkit.ConversationUtil
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

class PrestigeChallenge(id: String, internal var prestige: Int) : Challenge(id) {

    override fun getText(): String {
        return "Reach prestige $prestige"
    }

    override fun isProgressive(): Boolean {
        return true
    }

    override fun getProgressText(player: Player, user: User): String {
        val value = if (daily) {
            DailyChallengeHandler.getSession().getProgress(player.uniqueId).getTimesPrestiged()
        } else {
            user.getPrestige()
        }

        val percentage = ProgressBarBuilder.percentage(value, prestige)

        val progressColor = ProgressBarBuilder.colorPercentage(percentage)
        val progressBar = ProgressBarBuilder().build(percentage)

        return "${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}($progressColor${DECIMAL_FORMAT.format(percentage)}%${ChatColor.GRAY})"
    }

    override fun meetsCompletionRequirements(player: Player, user: User): Boolean {
        return if (daily) {
            DailyChallengeHandler.getSession().getProgress(player.uniqueId).getTimesPrestiged() >= prestige
        } else {
            user.getPrestige() >= prestige
        }
    }

    override fun getType(): ChallengeType {
        return PrestigeRequirementType
    }

    override fun getAbstractType(): Type {
        return PrestigeChallenge::class.java
    }

    object PrestigeRequirementType : ChallengeType {
        override fun getName(): String {
            return "Prestige"
        }

        override fun getDescription(): String {
            return "Reach a certain level of prestige"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.NETHER_STAR)
        }

        override fun startSetupPrompt(player: Player, id: String, lambda: (Challenge) -> Unit) {
            NumberPrompt().acceptInput { number ->
                assert(number.toInt() > 0)
                lambda.invoke(PrestigeChallenge(id, number.toInt()))
            }.start(player)
        }
    }

}