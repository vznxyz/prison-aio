/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.impl

import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.tool.enchant.Enchant
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

object JackHammer : Enchant("jack-hammer", "Jack Hammer", 5000) {

    override fun getCategory(): EnchantCategory {
        return EnchantCategory.DESTRUCTIVE
    }

    override val menuDisplay: Material
        get() = Material.STONE_SLAB2

//    override fun getCost(level: Int): Long {
//        return readCost() + ((level - 1) * 50)
//    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        if (!region.supportsAbilityEnchants() || region.getBreakableCuboid() == null) {
            return
        }

        val chance = level * readChance()
        if (Chance.percent(chance)) {
            Tasks.async {
                val blocks: MutableList<Block> = ArrayList()

                for (x in region.getBreakableCuboid()!!.lowerX..region.getBreakableCuboid()!!.upperX) {
                    for (z in region.getBreakableCuboid()!!.lowerZ..region.getBreakableCuboid()!!.upperZ) {
                        val location = Location(region.getBreakableCuboid()!!.world, x.toDouble(), event.block.location.blockY.toDouble(), z.toDouble())

                        if (MineCrateHandler.isAttached(location)) {
                            val mineCrate = MineCrateHandler.getSpawnedCrate(location)
                            if (mineCrate.owner == event.player.uniqueId) {
                                mineCrate.destroy(true)
                                MineCrateHandler.forgetSpawnedCrate(mineCrate)

                                val sendMessages = UserHandler.getUser(event.player.uniqueId).settings.getSettingOption(UserSetting.MINE_CRATES_NOTIFICATIONS).getValue<Boolean>()

                                for (reward in mineCrate.rewardSet.pickRewards()) {
                                    if (sendMessages) {
                                        event.player.sendMessage("${RewardsModule.getChatPrefix()}You received ${reward.name} ${ChatColor.GRAY}from the MineCrate!")
                                    }

                                    reward.execute(event.player)
                                }
                            }

                            continue
                        }

                        blocks.add(region.getBreakableCuboid()!!.world.getBlockAt(x, event.block.location.blockY, z))
                    }
                }

                Tasks.sync {
                    val multiBlockBreakEvent = MultiBlockBreakEvent(event.player, event.block, blocks, getYield())
                    Bukkit.getPluginManager().callEvent(multiBlockBreakEvent)

                    if (multiBlockBreakEvent.isCancelled) {
                        return@sync
                    }

                    sendMessage(event.player, "The layer you were mining has collapsed!")
                }
            }
        }
    }

}