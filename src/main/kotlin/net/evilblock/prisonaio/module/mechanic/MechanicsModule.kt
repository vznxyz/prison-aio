/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.backpack.command.BackpackGiveCommand
import net.evilblock.prisonaio.module.mechanic.backpack.command.BackpackViewCommand
import net.evilblock.prisonaio.module.mechanic.backpack.command.parameter.BackpackParameterType
import net.evilblock.prisonaio.module.mechanic.backpack.listener.BackpackListeners
import net.evilblock.prisonaio.module.mechanic.command.HotFixCommands
import net.evilblock.prisonaio.module.mechanic.command.HelpCommand
import net.evilblock.prisonaio.module.mechanic.command.SpawnCommand
import net.evilblock.prisonaio.module.mechanic.listener.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.*

object MechanicsModule : PluginModule() {

    private var autoSmeltBlocksMap: EnumMap<Material, Material> = EnumMap(Material::class.java)
    private var dropsToInvIgnoredBlocks: HashSet<Material> = hashSetOf()

    override fun getName(): String {
        return "Mechanics"
    }

    override fun getConfigFileName(): String {
        return "mechanics"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        loadConfig()

        BackpackHandler.initialLoad()
    }

    override fun onReload() {
        super.onReload()

        loadConfig()
    }

    override fun onDisable() {
        BackpackHandler.saveData()
    }

    override fun onAutoSave() {
        BackpackHandler.saveData()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            CraftingMechanicsListeners,
            AnvilMechanicsListeners,
            DisableBrewingMechanicsListeners,
            DisableExplosionsListeners,
            DisableFarmingMechanicsListeners,
            DisableNicknameListeners,
            DisableRedstoneListeners,
            DisableSpawnMobEggsListeners,
            MiningMechanicsListeners,
            MobMechanicsListeners,
            PreventDropsInSpawnListeners,
            StreamListeners,
            VanillaMechanicsListeners,
            BackpackListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            HotFixCommands.javaClass,
            HelpCommand.javaClass,
            SpawnCommand.javaClass,
            BackpackGiveCommand.javaClass,
            BackpackViewCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Backpack::class.java to BackpackParameterType
        )
    }

    private fun loadConfig() {
        autoSmeltBlocksMap.clear()
        dropsToInvIgnoredBlocks.clear()

        config.getStringList("auto-smelt.block-list").forEach { string ->
            val split = string.split(",")

            try {
                autoSmeltBlocksMap[Material.valueOf(split[0])] = Material.valueOf(split[1])
            } catch (e: Exception) {
                getPluginFramework().logger.warning("Error with string in auto-smelt block list config: `$string`")
            }
        }

        config.getStringList("drops-to-inv.ignored-block-list").forEach { string ->
            try {
                dropsToInvIgnoredBlocks.add(Material.valueOf(string))
            } catch (e: Exception) {
                getPluginFramework().logger.warning("Error with string in drops-to-inv ignored block list config: `$string`")
            }
        }
    }

    fun getHelpMessages(): List<String> {
        return config.getStringList("help-messages").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

    fun isFallDamageDisabled(): Boolean {
        return config.getBoolean("vanilla-mechanics.disable-fall-damage", true)
    }

    fun isSuffocationDamageDisabled(): Boolean {
        return config.getBoolean("vanilla-mechanics.disable-suffocation-damage", true)
    }

    fun isDrowningDamageDisabled(): Boolean {
        return config.getBoolean("vanilla-mechanics.disable-drowning-damage", true)
    }

    fun isItemDamageDisabled(): Boolean {
        return config.getBoolean("vanilla-mechanics.disable-item-damage", true)
    }

    fun areAnvilMechanicsDisabled(): Boolean {
        return config.getBoolean("vanilla-mechanics.disable-anvil-mechanics", true)
    }

    fun areFarmingMechanicsDisabled(): Boolean {
        return config.getBoolean("vanilla-mechanics.disable-farming-mechanics", true)
    }

    fun areBrewingMechanicsDisabled(): Boolean {
        return config.getBoolean("vanilla-mechanics.disable-brewing-mechanics", true)
    }

    /**
     * Gets a copy of the auto-smelt block list.
     */
    fun getAutoSmeltBlocks(): Map<Material, Material> {
        return autoSmeltBlocksMap
    }

    /**
     * Gets a copy of the drops-to-inv ignored blocks list.
     */
    fun getDropsToInvIgnoredBlocks(): Set<Material> {
        return dropsToInvIgnoredBlocks
    }

    fun isFortuneRandom(): Boolean {
        return config.getBoolean("fortune.random-drop-amount")
    }

    fun getFortuneMultiplier(): Double {
        return config.getDouble("fortune.multiplier")
    }

    fun getFortuneModifier(): Int {
        return config.getInt("fortune.modifier")
    }

    fun getFortuneMinDrops(): Int {
        return config.getInt("fortune.min-drops")
    }

    fun getFortuneMaxDrops(): Int {
        return config.getInt("fortune.max-drops")
    }

    fun isFortuneBlock(type: Material): Boolean {
        return config.getStringList("fortune.block-list").contains(type.name)
    }

    fun isTool(itemStack: ItemStack?): Boolean {
        return itemStack != null && itemStack.type != Material.AIR && TOOL_IDS.contains(itemStack.type.ordinal)
    }

    private val TOOL_IDS = arrayListOf(255, 256, 257, 260, 268, 269, 270, 272, 274, 276, 277, 278, 283, 284, 285, 358)

}