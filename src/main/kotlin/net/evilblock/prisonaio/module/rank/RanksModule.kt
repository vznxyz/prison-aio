/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.rank

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.rank.command.RankEditorCommand
import net.evilblock.prisonaio.module.rank.command.RankScaleCommand
import net.evilblock.prisonaio.module.rank.command.parameter.RankParameterType
import org.bukkit.ChatColor

object RanksModule : PluginModule() {

    private val prestigeRankPriceMultipliers: MutableMap<Int, Double> = hashMapOf(0 to 1.0)

    override fun getName(): String {
        return "Ranks"
    }

    override fun getConfigFileName(): String {
        return "ranks"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        loadConfig()

        RankHandler.initialLoad()
    }

    override fun onReload() {
        super.onReload()

        loadConfig()
    }

    override fun onAutoSave() {
        RankHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            RankEditorCommand.javaClass,
            RankScaleCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(Rank::class.java to RankParameterType())
    }

    fun readCommands(id: String): List<String> {
        return config.getStringList("ranks.$id.commands")
    }

    fun readPermissions(id: String): List<String> {
        return config.getStringList("ranks.$id.permissions")
    }

    fun getDefaultPermissions(): List<String> {
        return config.getStringList("default-permissions")
    }

    private fun loadConfig() {
        prestigeRankPriceMultipliers.clear()
        prestigeRankPriceMultipliers[0] = 1.0
        prestigeRankPriceMultipliers.putAll(readPrestigeRankPriceMultipliers())
    }

    private fun readPrestigeRankPriceMultipliers(): Map<Int, Double> {
        return config.getConfigurationSection("prestige.rank-price-multipliers")
            .getKeys(false)
            .map { key -> key.toInt() to config.getDouble("prestige.rank-price-multipliers.$key") }
            .toMap()
    }

    fun getPrestigeRankPriceMultipliers(): Map<Int, Double> {
        return prestigeRankPriceMultipliers
    }

    fun getPrestigeRankPriceMultiplier(prestige: Int): Double {
        return if (prestigeRankPriceMultipliers.containsKey(prestige)) {
            prestigeRankPriceMultipliers[prestige]!!
        } else {
            prestigeRankPriceMultipliers.maxBy { prestige }!!.value
        }
    }

    fun getMaxPrestige(): Int {
        return config.getInt("prestige.max-prestige", 50)
    }

    fun getMaxPrestigeTag(): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("prestige.max-prestige-tag"))
    }

    fun getPrestigeBlocksMinedRequirementBase(): Int {
        return config.getInt("prestige.blocks-mined-requirement.base")
    }

    fun getPrestigeBlocksMinedRequirementModifier(): Int {
        return config.getInt("prestige.blocks-mined-requirement.modifier")
    }

    fun getPrestigeCommands(): List<String> {
        return config.getStringList("prestige.commands")
    }

}