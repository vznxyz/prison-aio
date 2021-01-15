/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.service

import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.service.Service
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object CheckProgressService : Service {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            try {
                check(player)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun check(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        if (!user.battlePassProgress.requiresCheck) {
            return
        }

        user.battlePassProgress.requiresCheck = false

        if (user.battlePassProgress.isPremium()) {
            for (challenge in ChallengeHandler.getChallenges()) {
                if (challenge.daily) {
                    continue
                }

                if (user.battlePassProgress.hasCompletedChallenge(challenge)) {
                    continue
                }

                if (challenge.meetsCompletionRequirements(player, user)) {
                    challenge.onComplete(player, user)
                }
            }
        }

        for (challenge in DailyChallengeHandler.getSession().getChallenges()) {
            if (!challenge.daily) {
                continue
            }

            if (user.battlePassProgress.hasCompletedChallenge(challenge)) {
                continue
            }

            if (challenge.meetsCompletionRequirements(player, user)) {
                challenge.onComplete(player, user)
            }
        }
    }

}