/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.impl

import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeType
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

class ExecuteCommandChallenge(id: String, internal var command: String) : Challenge(id) {

    override fun getText(): String {
        return "Execute the '$command' command"
    }

    override fun getType(): ChallengeType {
        return ExecuteCommandChallengeType
    }

    override fun getAbstractType(): Type {
        return ExecuteCommandChallenge::class.java
    }

    override fun meetsCompletionRequirements(player: Player, user: User): Boolean {
        return if (daily) {
            DailyChallengeHandler.getSession().getProgress(player.uniqueId).hasExecutedCommand(command)
        } else {
            user.battlePassProgress.hasExecutedCommand(command)
        }
    }

    object ExecuteCommandChallengeType : ChallengeType {
        override fun getName(): String {
            return "Execute Command"
        }

        override fun getDescription(): String {
            return "Execute a certain command"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.DIAMOND_PICKAXE)
        }

        override fun startSetupPrompt(player: Player, id: String, lambda: (Challenge) -> Unit) {
            EzPrompt.Builder()
                .promptText("${ChatColor.GREEN}Please input the command.")
                .acceptInput { input ->
                    lambda.invoke(ExecuteCommandChallenge(id, input.trim()))
                }
                .build()
                .start(player)
        }
    }

}