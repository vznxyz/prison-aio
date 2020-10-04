/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.leaderboard.command.RefreshCommand
import net.evilblock.prisonaio.module.leaderboard.command.IndexCommand
import net.evilblock.prisonaio.module.leaderboard.command.ResultsCommand
import net.evilblock.prisonaio.module.leaderboard.command.SpawnLeaderboardCommand
import net.evilblock.prisonaio.module.leaderboard.event.LeaderboardsRefreshedEvent
import net.evilblock.prisonaio.module.leaderboard.impl.*
import net.evilblock.prisonaio.module.leaderboard.npc.LeaderboardNpcEntity

object LeaderboardsModule : PluginModule() {

	private val leaderboards: List<Leaderboard> = listOf(
		GangTrophiesLeaderboard,
		GangValueLeaderboard,
		BlocksMinedLeaderboard,
		MoneyBalanceLeaderboard,
		PrestigeLeaderboard,
		TopTimePlayedLeaderboard,
		TopTokensLeaderboard
	)

	override fun getName(): String {
		return "Leaderboards"
	}

	override fun getConfigFileName(): String {
		return "leaderboards"
	}

	override fun getPluginFramework(): PluginFramework {
		return PrisonAIO.instance
	}

	override fun onEnable() {
		Tasks.asyncDelayed(60L) {
			refreshLeaderboards()
		}

		Tasks.asyncTimer(20L * 60L * 2L, 20L * 60L * 2L) {
			refreshLeaderboards()
		}
	}

	override fun getCommands(): List<Class<*>> {
		return listOf(
			RefreshCommand.javaClass,
			IndexCommand.javaClass,
			ResultsCommand.javaClass,
			SpawnLeaderboardCommand.javaClass
		)
	}

	override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
		return mapOf(
			Leaderboard::class.java to Leaderboard.CommandParameterType
		)
	}

	fun refreshLeaderboards() {
		for (leaderboard in leaderboards) {
			try {
				leaderboard.refresh()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}

		LeaderboardsRefreshedEvent().call()
	}

	fun getLeaderboards(): List<Leaderboard> {
		return leaderboards.toList()
	}

	fun getLeaderboardById(id: String): Leaderboard? {
		return leaderboards.firstOrNull { it.id.equals(id, ignoreCase = true) }
	}

	fun getLeaderboardNpcs(): List<LeaderboardNpcEntity> {
		return EntityManager.getEntities().filterIsInstance(LeaderboardNpcEntity::class.java)
	}

}
