package net.evilblock.prisonaio.module.leaderboard

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.leaderboard.command.RefreshCommand
import net.evilblock.prisonaio.module.leaderboard.command.SpawnCommand
import net.evilblock.prisonaio.module.leaderboard.impl.CellTopLeaderboard
import net.evilblock.prisonaio.module.leaderboard.impl.MoneyBalanceLeaderboard
import net.evilblock.prisonaio.module.leaderboard.impl.TokensBalanceLeaderboard
import net.evilblock.prisonaio.module.leaderboard.npc.LeaderboardNpcEntity

object LeaderboardsModule : PluginModule() {

	private val leaderboards: List<Leaderboard> = listOf(
		CellTopLeaderboard,
		MoneyBalanceLeaderboard,
		TokensBalanceLeaderboard
	)

	override fun getName(): String {
		return "Leaderboards"
	}

	override fun getConfigFileName(): String {
		return "leaderboards"
	}

	override fun onEnable() {
		Tasks.async {
			refreshLeaderboards()
		}

		Tasks.asyncTimer(20L * 60L * 2L, 20L * 60L * 2L) {
			refreshLeaderboards()
		}
	}

	override fun getCommands(): List<Class<*>> {
		return listOf(
			RefreshCommand.javaClass,
			SpawnCommand.javaClass
		)
	}

	override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
		return mapOf(
			Leaderboard::class.java to Leaderboard.CommandParameterType
		)
	}

	fun refreshLeaderboards() {
		for (leaderboard in leaderboards) {
			leaderboard.refresh()
		}
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
