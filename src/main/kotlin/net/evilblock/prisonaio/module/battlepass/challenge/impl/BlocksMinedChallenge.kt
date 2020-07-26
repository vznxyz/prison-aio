/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.impl

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.ProgressBarBuilder
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
import java.text.NumberFormat

class BlocksMinedChallenge(id: String, internal var blocksMined: Int) : Challenge(id) {

    constructor(id: String, name: String, blocksMined: Int, xp: Int) : this(id, blocksMined) {
        this.name = name
        this.rewardXp = xp
    }

    override fun getText(): String {
        return "Mine ${NumberFormat.getInstance().format(blocksMined)} blocks"
    }

    override fun isProgressive(): Boolean {
        return true
    }

    override fun getProgressText(player: Player, user: User): String {
        val value = if (daily) {
            DailyChallengeHandler.getSession().getProgress(player.uniqueId).getBlocksMined()
        } else {
            user.statistics.getBlocksMined()
        }

        val percentage = ProgressBarBuilder.percentage(value, blocksMined)

        val progressColor = ProgressBarBuilder.colorPercentage(percentage)
        val progressBar = ProgressBarBuilder().build(percentage)

        return "${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}($progressColor${DECIMAL_FORMAT.format(percentage)}%${ChatColor.GRAY})"
    }

    override fun meetsCompletionRequirements(player: Player, user: User): Boolean {
        return if (daily) {
            DailyChallengeHandler.getSession().getProgress(player.uniqueId).getBlocksMined() >= blocksMined
        } else {
            user.statistics.getBlocksMined() >= blocksMined
        }
    }

    override fun getType(): ChallengeType {
        return BlocksMinedChallengeType
    }

    override fun getAbstractType(): Type {
        return BlocksMinedChallenge::class.java
    }

    object BlocksMinedChallengeType : ChallengeType {
        override fun getName(): String {
            return "Blocks Mined"
        }

        override fun getDescription(): String {
            return "Reach a certain amount of blocks mined"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.DIAMOND_PICKAXE)
        }

        override fun startSetupPrompt(player: Player, id: String, lambda: (Challenge) -> Unit) {
            NumberPrompt { number ->
                assert(number > 0)
                lambda.invoke(BlocksMinedChallenge(id, number))
            }.start(player)
        }
    }

}