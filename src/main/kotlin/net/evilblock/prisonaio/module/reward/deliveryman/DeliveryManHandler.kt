/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirementType
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.impl.BlocksMinedRequirement
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.impl.PlayTimeRequirement
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.impl.PrestigeRequirement
import org.bukkit.ChatColor
import java.io.File

object DeliveryManHandler : PluginHandler {

    @JvmStatic
    val REQUIREMENT_REGISTRY: MutableList<DeliveryManRewardRequirementType<*>> = arrayListOf(
        BlocksMinedRequirement.BlocksMinedRequirementType,
        PlayTimeRequirement.PlayTimeRequirementType,
        PrestigeRequirement.PrestigeRequirementType
    )

    private val rewards: MutableMap<String, DeliveryManReward> = hashMapOf()

    override fun getModule(): PluginModule {
        return RewardsModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "delivery-man-rewards.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<DeliveryManReward>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<DeliveryManReward>

                for (reward in list) {
                    trackReward(reward)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(rewards.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getRewards(): List<DeliveryManReward> {
        return rewards.values.toList()
    }

    fun getRewardById(id: String): DeliveryManReward? {
        return rewards[id.toLowerCase()]
    }

    fun trackReward(reward: DeliveryManReward) {
        rewards[reward.id.toLowerCase()] = reward
    }

    fun forgetReward(reward: DeliveryManReward) {
        rewards.remove(reward.id.toLowerCase())
    }

    fun getDeliveryManMenuTitle(): String {
        return ChatColor.translateAlternateColorCodes('&', getModule().config.getString("delivery-man.menu-title"))
    }

    fun getHologramLines(): List<String> {
        return getModule().config.getStringList("delivery-man.npc.hologram-lines").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

    fun getTextureValue(): String {
        return getModule().config.getString("delivery-man.npc.texture-value")
    }

    fun getTextureSignature(): String {
        return getModule().config.getString("delivery-man.npc.texture-signature")
    }

}