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
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorSet
import net.evilblock.prisonaio.module.mechanic.armor.command.GiveArmorPieceCommand
import net.evilblock.prisonaio.module.mechanic.armor.command.GiveArmorSetCommand
import net.evilblock.prisonaio.module.mechanic.armor.command.SpawnDisplayCommand
import net.evilblock.prisonaio.module.mechanic.armor.listener.AbilityArmorListeners
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.backpack.command.*
import net.evilblock.prisonaio.module.mechanic.backpack.command.parameter.BackpackParameterType
import net.evilblock.prisonaio.module.mechanic.backpack.listener.BackpackListeners
import net.evilblock.prisonaio.module.mechanic.jumppad.JumpPadListeners
import net.evilblock.prisonaio.module.mechanic.jumppad.command.JumpPadCommand
import net.evilblock.prisonaio.module.mechanic.listener.*
import net.evilblock.prisonaio.module.mechanic.trade.command.TradeAcceptCommand
import net.evilblock.prisonaio.module.mechanic.trade.command.TradeCommand
import net.evilblock.prisonaio.module.mechanic.trade.command.TradeDeclineCommand
import net.evilblock.prisonaio.module.mechanic.trade.command.admin.TradeToggleCommand
import net.evilblock.prisonaio.module.mechanic.trade.listener.TradeListeners
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
        AbilityArmorHandler.initialLoad()
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
            DisableBlockDecayListeners,
            DisableBrewingMechanicsListeners,
            DisableExplosionsListeners,
            DisableFarmingMechanicsListeners,
            DisableHoppersListeners,
            DisableRedstoneListeners,
            DisableSpawnMobEggsListeners,
            FirstJoinListeners,
            MiningMechanicsListeners,
            MobMechanicsListeners,
            PlayerDeathListeners,
            SpawnListeners,
            StreamListeners,
            VanillaMechanicsListeners,
            BackpackListeners,
            AbilityArmorListeners,
            TradeListeners,
            JumpPadListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            BackpackDebugCommand.javaClass,
            BackpackGiveCommand.javaClass,
            BackpackSafeSaveCommand.javaClass,
            BackpackViewCommand.javaClass,
            BackpackWipeCommand.javaClass,
            GiveArmorPieceCommand.javaClass,
            GiveArmorSetCommand.javaClass,
            SpawnDisplayCommand.javaClass,
            TradeAcceptCommand.javaClass,
            TradeDeclineCommand.javaClass,
            TradeToggleCommand.javaClass,
            TradeCommand.javaClass,
            JumpPadCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Backpack::class.java to BackpackParameterType,
            AbilityArmorSet::class.java to AbilityArmorSet.ArmorParameterType
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

    fun isRedstoneDisabled(): Boolean {
        return config.getBoolean("vanilla-mechanics.disable-redstone", true)
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

    fun getFortuneMultiplier(): Double {
        return config.getDouble("fortune.multiplier")
    }

    fun getFortuneModifier(): Double {
        return config.getDouble("fortune.modifier")
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
        return itemStack != null && itemStack.type != Material.AIR && TOOL_IDS.contains(itemStack.type.id)
    }

    fun isPickaxe(itemStack: ItemStack?): Boolean {
        if (itemStack == null) {
            return false
        }

        if (itemStack.type == Material.AIR) {
            return false
        }

        if (!PICK_TYPES.contains(itemStack.type)) {
            return false
        }

        return true
    }

    fun getJumpPadDefaultLines(): List<String> {
        return config.getStringList("jump-pad.default-lines").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

    fun getJumpPadVelocityX(): Double {
        return config.getDouble("jump-pad.velocity.x", 2.5)
    }

    fun getJumpPadVelocityY(): Double {
        return config.getDouble("jump-pad.velocity.y", 1.0)
    }

    private val TOOL_IDS = arrayListOf(
        276, 277, 278, //diamond tools
        283, 284, 285, // gold tools
        256, 257, 267, // iron tools
        272, 273, 274, // stone tools
        268, 269, 270 // wood tools
    )

    private val PICK_TYPES = arrayListOf(
        Material.DIAMOND_PICKAXE,
        Material.GOLD_PICKAXE,
        Material.STONE_PICKAXE,
        Material.WOOD_PICKAXE
    )

}