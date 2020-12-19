/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.robot.command.*
import net.evilblock.prisonaio.module.robot.cosmetic.Cosmetic
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import net.evilblock.prisonaio.module.robot.cosmetic.command.CosmeticGrantCommand
import net.evilblock.prisonaio.module.robot.cosmetic.command.CosmeticsCommand
import net.evilblock.prisonaio.module.robot.cosmetic.command.param.CosmeticParameterType
import net.evilblock.prisonaio.module.robot.listener.RobotBlockListeners
import net.evilblock.prisonaio.module.robot.listener.RobotInventoryListeners
import net.evilblock.prisonaio.module.robot.listener.RobotItemListeners
import net.evilblock.prisonaio.module.robot.listener.RobotPlotListeners
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object RobotsModule : PluginModule() {

    val CHAT_PREFIX = "${ChatColor.GRAY}[${ChatColor.RED}${ChatColor.BOLD}Robots${ChatColor.GRAY}] "

    override fun getName(): String {
        return "Robots"
    }

    override fun getConfigFileName(): String {
        return "robots"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        super.onEnable()

        CosmeticHandler.load()
        RobotHandler.initialLoad()
    }

    override fun onAutoSave() {
        super.onAutoSave()

        RobotHandler.saveData()
    }

    override fun onDisable() {
        super.onDisable()

        RobotHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            RobotsCommand.javaClass,
            AlignNearCommand::class.java,
            DeleteNearCommand::class.java,
            GiveRobotCommand::class.java,
            SpawnExampleRobotCommand::class.java,
            ToggleAnimationsCommand::class.java,
            ToggleMergeCommand::class.java,
            CosmeticsCommand::class.java,
            CosmeticGrantCommand::class.java
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Cosmetic::class.java to CosmeticParameterType()
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            RobotBlockListeners,
            RobotInventoryListeners,
            RobotItemListeners,
            RobotPlotListeners
        )
    }

    override fun requiresLateLoad(): Boolean {
        return true
    }

    @JvmStatic
    fun isAnimationsEnabled(): Boolean {
        return config.getBoolean("settings.animations-enabled", false)
    }

    @JvmStatic
    fun toggleAnimations() {
        config.set("settings.animations-enabled", !isAnimationsEnabled())
        saveConfig()
    }

    fun getMaxTiers(): Int {
        return config.getInt("settings.max-tiers", 10)
    }

    fun getMaxRobotsPerPlot(): Int {
        return config.getInt("settings.max-robots-per-plot", 50)
    }

    fun getUpgradesCurrency(): Currency.Type {
        return Currency.Type.valueOf(config.getString("upgrades.currency", "MONEY"))
    }

    fun getTierBaseMoney(tier: Int): Double {
        return config.getDouble("tiers.${tier}.base-money", 0.0)
    }

    fun getTierBaseTokens(tier: Int): Double {
        return config.getDouble("tiers.${tier}.base-tokens", 0.0)
    }

    fun getFortuneBaseMoney(): Double {
        return config.getDouble("upgrades.fortune.base-money", 0.0)
    }

    fun getFortuneMoneyMultiplier(): Double {
        return config.getDouble("upgrades.fortune.money-multiplier", 1.0)
    }

    fun getFortuneBaseTokens(): Double {
        return config.getDouble("upgrades.fortune.base-tokens", 1.0)
    }

    fun getFortuneTokensMultiplier(): Double {
        return config.getDouble("upgrades.fortune.tokens-multiplier", 1.0)
    }

    fun getTierName(tier: Int): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("tiers.${tier}.name", "&cDefault Tier $tier Name"))
    }

    fun hasTierTexture(tier: Int): Boolean {
        return config.contains("tiers.${tier}.texture-value") && config.contains("tiers.${tier}.texture-signature")
    }

    fun hasTierArmorColor(tier: Int): Boolean {
        return config.contains("tiers.${tier}.armor-color")
    }

    fun getTierArmorColor(tier: Int): Int {
        return config.getInt("tiers.${tier}.armor-color")
    }

    fun getTierTexture(tier: Int): Pair<String, String> {
        return Pair(config.getString("tiers.${tier}.texture-value"), config.getString("tiers.${tier}.texture-signature"))
    }

    fun getTierKeys(): List<String> {
        return ArrayList(config.getConfigurationSection("tiers").getKeys(false))
    }

}