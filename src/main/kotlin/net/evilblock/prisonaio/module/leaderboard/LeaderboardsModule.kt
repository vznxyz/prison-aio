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
import net.evilblock.prisonaio.module.leaderboard.command.*
import net.evilblock.prisonaio.module.leaderboard.event.LeaderboardsRefreshedEvent
import net.evilblock.prisonaio.module.leaderboard.impl.*
import net.evilblock.prisonaio.module.leaderboard.npc.LeaderboardNpcEntity
import java.util.*

object LeaderboardsModule : PluginModule() {

	private val leaderboards: List<Leaderboard> = listOf(
		MoneyBalanceLeaderboard,
		TopTokensLeaderboard,
		PrestigeLeaderboard,
		BlocksMinedLeaderboard,
		TopTimePlayedLeaderboard,
		GangTrophiesLeaderboard,
		GangValueLeaderboard
	)

	val exemptions: MutableSet<UUID> = hashSetOf()

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
		Tasks.asyncTimer(60L, 20L * 60L * 2L) {
			refreshLeaderboards()
		}

		loadExemptions()
	}

	override fun onReload() {
		super.onReload()

		loadExemptions()
	}

	override fun getCommands(): List<Class<*>> {
		return listOf(
			ExemptionAddCommand.javaClass,
			ExemptionRemoveCommand.javaClass,
			LeaderboardsCommand.javaClass,
			RefreshCommand.javaClass,
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

	fun readFallbackTextureId(): String {
		return config.getString("fallback-texture-id", "leaderboards")
	}

	private fun loadExemptions() {
		exemptions.clear()

		if (config.contains("exemptions")) {
			for (value in config.getStringList("exemptions")) {
				exemptions.add(UUID.fromString(value))
			}
		}
	}

	fun saveExemptions() {
		config.set("exemptions", exemptions.map { it.toString() })
		saveConfig()
	}

}
