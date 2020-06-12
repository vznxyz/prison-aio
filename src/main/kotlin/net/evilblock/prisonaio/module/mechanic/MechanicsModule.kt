package net.evilblock.prisonaio.module.mechanic

import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.mechanic.command.HotFixCommands
import net.evilblock.prisonaio.module.mechanic.command.HelpCommand
import net.evilblock.prisonaio.module.mechanic.command.SpawnCommand
import net.evilblock.prisonaio.module.mechanic.listener.*
import net.evilblock.prisonaio.module.mechanic.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.mechanic.region.command.RegionBypassCommand
import net.evilblock.prisonaio.module.mechanic.region.listener.RegionListeners
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.Listener
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

    override fun onEnable() {
        loadConfig()
    }

    override fun onReload() {
        super.onReload()

        loadConfig()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            RegionBypass,
            CraftingMechanicsListeners,
            DisableAnvilMechanicsListeners,
            DisableBrewingMechanicsListeners,
            DisableExplosionsListeners,
            DisableFarmingMechanicsListeners,
            DisableNicknameListeners,
            DisableRedstoneListeners,
            DisableSpawnMobEggsListeners,
            MiningMechanicsListeners,
            MobMechanicsListeners,
            PreventDropsInSpawnListeners,
            RegionListeners,
            StreamListeners,
            VanillaMechanicsListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            HotFixCommands.javaClass,
            HelpCommand.javaClass,
            SpawnCommand.javaClass,
            RegionBypassCommand.javaClass
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
                getPlugin().logger.warning("Error with string in auto-smelt block list config: `$string`")
            }
        }

        config.getStringList("drops-to-inv.ignored-block-list").forEach { string ->
            try {
                dropsToInvIgnoredBlocks.add(Material.valueOf(string))
            } catch (e: Exception) {
                getPlugin().logger.warning("Error with string in drops-to-inv ignored block list config: `$string`")
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

    fun canOpenEnderChestInGlobalRegion(): Boolean {
        return config.getBoolean("global-region.allow-open-enderchest", true)
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

}