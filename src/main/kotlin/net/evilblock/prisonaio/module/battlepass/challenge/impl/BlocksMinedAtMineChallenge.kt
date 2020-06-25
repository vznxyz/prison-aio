/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.impl

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.util.ProgressBarBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeType
import net.evilblock.prisonaio.module.battlepass.challenge.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.serialize.MineReferenceSerializer
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.Constants
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import java.text.NumberFormat

class BlocksMinedAtMineChallenge(id: String, @JsonAdapter(MineReferenceSerializer::class) private var mine: Mine, internal var blocksMined: Int) : Challenge(id) {

    constructor(id: String, name: String, mine: Mine, blocksMined: Int, xp: Int) : this(id, mine, blocksMined) {
        this.name = name
        this.rewardXp = xp
    }

    override fun getText(): String {
        return "Mine ${NumberFormat.getInstance().format(blocksMined)} blocks at the ${mine.id} Mine"
    }

    override fun isProgressive(): Boolean {
        return true
    }

    override fun getProgressText(player: Player, user: User): String {
        val value = if (daily) {
            DailyChallengeHandler.getSession().getProgress(player.uniqueId).getBlocksMinedAtMine(mine)
        } else {
            user.statistics.getBlocksMinedAtMine(mine)
        }

        val percentage = ProgressBarBuilder.percentage(value, blocksMined)

        val progressColor = ProgressBarBuilder.colorPercentage(percentage)
        val progressBar = ProgressBarBuilder().build(percentage)

        return "${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}$progressBar${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE} ${ChatColor.GRAY}($progressColor${DECIMAL_FORMAT.format(percentage)}%${ChatColor.GRAY})"
    }

    override fun getType(): ChallengeType {
        return BlocksMinedAtMineChallengeType
    }

    override fun getAbstractType(): Type {
        return BlocksMinedAtMineChallenge::class.java
    }

    object BlocksMinedAtMineChallengeType : ChallengeType {
        override fun getName(): String {
            return "Blocks Mined At Mine"
        }

        override fun getDescription(): String {
            return "Reach a certain amount of blocks mined at a certain mine"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.DIAMOND_PICKAXE)
        }

        override fun startSetupPrompt(player: Player, id: String, lambda: (Challenge) -> Unit) {
            EzPrompt.Builder()
                .promptText("${ChatColor.GREEN}Please input the ID of the mine.")
                .acceptInput { player, input ->
                    val mine = MineHandler.getMineById(input)
                    if (!mine.isPresent) {
                        player.sendMessage("${ChatColor.RED}A mine by that ID doesn't exist.")
                        return@acceptInput
                    }

                    Tasks.delayed(1L) {
                        NumberPrompt { number ->
                            assert(number > 0)

                            lambda.invoke(BlocksMinedAtMineChallenge(id, mine.get(), number))
                        }.start(player)
                    }
                }
                .build()
                .start(player)
        }
    }

}