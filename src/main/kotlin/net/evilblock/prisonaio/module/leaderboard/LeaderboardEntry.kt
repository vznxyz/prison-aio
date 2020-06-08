package net.evilblock.prisonaio.module.leaderboard;

data class LeaderboardEntry<T>(
	var position: Int,
	val displayName: String,
	val value: T
)
