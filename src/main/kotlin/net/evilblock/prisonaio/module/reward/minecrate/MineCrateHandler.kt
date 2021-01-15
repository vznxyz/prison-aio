/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.minecrate

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.reward.MineCrateRewardSet
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

object MineCrateHandler : PluginHandler() {

    private var rewardSets: MutableList<MineCrateRewardSet> = arrayListOf()
    private var spawnedCrates = hashMapOf<Location, MineCrate>()

    override fun getModule(): PluginModule {
        return RewardsModule
    }

    override fun initialLoad() {
        super.initialLoad()

        loadConfig()

        loaded = true
    }

    fun isAttached(location: Location): Boolean {
        return spawnedCrates.containsKey(location)
    }

    fun getSpawnedCrate(location: Location): MineCrate {
        return spawnedCrates[location]!!
    }

    fun trackSpawnedCrate(mineCrate: MineCrate) {
        spawnedCrates[mineCrate.location] = mineCrate
    }

    fun forgetSpawnedCrate(mineCrate: MineCrate) {
        spawnedCrates.remove(mineCrate.location)
    }

    fun getSpawnedCrates(): List<MineCrate> {
        return spawnedCrates.values.toList()
    }

    fun clearSpawnedCrates() {
        for (spawnedCrate in spawnedCrates.values) {
            spawnedCrate.destroy(enforceSync = true)
        }
    }

    private fun loadConfig() {
        rewardSets.clear()

        getModule().config.getConfigurationSection("mine-crates.reward-sets").getKeys(false).forEach { key ->
            val section = getModule().config.getConfigurationSection("mine-crates.reward-sets.$key")

            val rewardSet = MineCrateRewardSet(
                id = key,
                chance = section.getDouble("chance"),
                maxRewards = section.getInt("max-rewards")
            )

            for (itemsSection in section.getList("items") as List<Map<String, Any>>) {
                rewardSet.addReward(
                    name = ChatColor.translateAlternateColorCodes('&', itemsSection["name"] as String),
                    chance = itemsSection["chance"] as Double,
                    commands = itemsSection["commands"] as List<String>
                )
            }

            rewardSets.add(rewardSet)
        }
    }

    private fun getCooldown(): Long {
        return getModule().config.getLong("mine-crates.cooldown")
    }

    fun isOnCooldown(player: Player): Boolean {
        return player.hasMetadata("MINE_CRATE_CD") && player.getMetadata("MINE_CRATE_CD")[0].asLong() >= System.currentTimeMillis()
    }

    fun resetCooldown(player: Player) {
        player.setMetadata("MINE_CRATE_CD", FixedMetadataValue(PrisonAIO.instance, System.currentTimeMillis() + getCooldown()))
    }

    fun getRewardSets(): List<MineCrateRewardSet> {
        return rewardSets.toList()
    }

    fun isOnlyOwnerCanOpen(): Boolean {
        return getModule().config.getBoolean("mine-crates.only-owner-can-open")
    }

    fun getHologramLines(): List<String> {
        return RewardsModule.config.getStringList("mine-crates.hologram-lines").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

}