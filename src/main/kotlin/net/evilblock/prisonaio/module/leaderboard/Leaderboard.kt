/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard

import net.evilblock.cubed.CubedConfig
import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class Leaderboard(val id: String, val name: String) {

    companion object {
        internal const val CACHED_ENTRIES_SIZE = 10
    }

    internal var entries: List<LeaderboardEntry<*>> = arrayListOf()
    private var lastSkinSource: String? = null // prevents lookup of the same player texture

    abstract fun fetchEntries(): List<LeaderboardEntry<*>>

    abstract fun formatEntry(entry: LeaderboardEntry<*>): String

    fun getDisplayLines(fullView: Boolean): List<String> {
        val lines = arrayListOf<String>()
        lines.add(name)

        if (entries.isEmpty()) {
            lines.add("${ChatColor.GRAY}Loading data...")
            return lines
        }

        for ((index, entry) in entries.withIndex()) {
            lines.add(formatEntry(entry))

            if (fullView && index >= 4) {
                break
            }
        }

        return lines
    }

    fun refresh() {
        val newEntries = fetchEntries()
        var position = 1

        for (entry in newEntries) {
            entry.position = position++
        }

        this.entries = newEntries

        val firstEntry = this.entries.firstOrNull()
        val updateSkin = firstEntry?.skinSource != null || firstEntry?.skinSource != lastSkinSource

        for (npc in LeaderboardsModule.getLeaderboardNpcs()) {
            if (npc.leaderboard == this) {
                npc.updateLines(getDisplayLines(true))

                if (updateSkin) {
                    val fallbackTexture = if (CubedConfig.hasNpcTexture(LeaderboardsModule.readFallbackTextureId())) {
                        CubedConfig.getNpcTexture(LeaderboardsModule.readFallbackTextureId())
                    } else {
                        null
                    }

                    // if first entry is null or first entry skin source is null, use the fallback texture
                    if (firstEntry?.skinSource == null) {
                        if (fallbackTexture != null) {
                            npc.updateTexture(fallbackTexture.textureValue, fallbackTexture.textureSignature)
                        }
                    } else {
                        try {
                            lastSkinSource = firstEntry.skinSource

                            npc.updateTextureByUsername(username = firstEntry.skinSource, onComplete = { success, thrown ->
                                if (!success) {
                                    if (fallbackTexture != null) {
                                        npc.updateTexture(fallbackTexture.textureValue, fallbackTexture.textureSignature)
                                    }
                                }
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    object CommandParameterType : ParameterType<Leaderboard?> {
        override fun transform(sender: CommandSender, source: String): Leaderboard? {
            val leaderboard = LeaderboardsModule.getLeaderboardById(source.toLowerCase())
            if (leaderboard == null) {
                sender.sendMessage("${ChatColor.RED}Couldn't find a leaderboard by that ID.")
            }
            return leaderboard
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            val completions = arrayListOf<String>()
            for (leaderboard in LeaderboardsModule.getLeaderboards()) {
                if (leaderboard.id.startsWith(source, ignoreCase = true)) {
                    completions.add(leaderboard.id)
                }
            }
            return completions
        }
    }

}