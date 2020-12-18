/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.impl

import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.ToolsModule
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.evilblock.prisonaio.module.tool.enchant.EnchantCategory
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.*

object Cubed : Enchant("cubed", "Cubed", 3) {

    override fun getCategory(): EnchantCategory {
        return EnchantCategory.DESTRUCTIVE
    }

    override val menuDisplay: Material
        get() = Material.BEACON

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        val blocks: MutableList<Block> = ArrayList()

        val origin = event.block.location
        for (x in origin.blockX - level..origin.blockX + level) {
            for (y in origin.blockY - level..origin.blockY + level) {
                for (z in origin.blockZ - level..origin.blockZ + level) {
                    val location = Location(origin.world, x.toDouble(), y.toDouble(), z.toDouble())
                    if (MineCrateHandler.isAttached(location)) {
                        val mineCrate = MineCrateHandler.getSpawnedCrate(location)
                        if (mineCrate.owner == event.player.uniqueId) {
                            mineCrate.destroy(true)
                            MineCrateHandler.forgetSpawnedCrate(mineCrate)

                            val sendMessages = UserHandler.getUser(event.player.uniqueId).settings.getSettingOption(UserSetting.REWARD_MESSAGES).getValue<Boolean>()

                            for (reward in mineCrate.rewardSet.pickRewards()) {
                                if (sendMessages) {
                                    event.player.sendMessage("${RewardsModule.getChatPrefix()}You received ${reward.name} ${ChatColor.GRAY}from the MineCrate!")
                                }

                                reward.execute(event.player)
                            }
                        }

                        continue // skip adding block to block list
                    }

                    val type = location.block.type
                    if (type != Material.BEDROCK && type != Material.AIR) {
                        if (region.getBreakableCuboid() != null && region.getBreakableCuboid()!!.contains(location.block)) {
                            blocks.add(location.block)
                        }
                    }
                }
            }
        }

        if (blocks.isEmpty()) {
            return
        }

        val cubedNerf = readNerf()

        val yield = if (level <= cubedNerf.size && level > 0) {
            cubedNerf.sorted()[level - 1]
        } else {
            100f
        }

        MultiBlockBreakEvent(event.player, event.block, blocks, `yield`).call()
    }

//    override fun getCost(level: Int): Long {
//        return Long.MAX_VALUE
//    }

    override fun getRefundTokens(level: Int): Long {
        return 0
    }

    private fun readNerf(): Array<Float> {
        return ToolsModule.config.getFloatList("cubed.nerf").toTypedArray()
    }

}