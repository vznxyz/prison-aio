/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.*

object JackHammer : AbstractEnchant("jack-hammer", "Jack Hammer", 5000) {

    override val iconColor: Color
        get() = Color.RED

    override val textColor: ChatColor
        get() = ChatColor.RED

    override val menuDisplay: Material
        get() = Material.STONE_SLAB2

    override fun getCost(level: Int): Long {
        return readCost() + ((level - 1) * 50)
    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        if (!region.supportsAbilityEnchants() || region.getBreakableCuboid() == null) {
            return
        }

        val chance = level * readChance()
        if (Chance.percent(chance)) {
            val blocks: MutableList<Block> = ArrayList()

            // get all blocks in mine region that are on the same y as the original block broken
            for (x in region.getBreakableCuboid()!!.lowerX..region.getBreakableCuboid()!!.upperX) {
                for (z in region.getBreakableCuboid()!!.lowerZ..region.getBreakableCuboid()!!.upperZ) {
                    blocks.add(region.getBreakableCuboid()!!.world.getBlockAt(x, event.block.location.blockY, z))
                }
            }

            // broadcast multi block break
            val multiBlockBreakEvent = MultiBlockBreakEvent(event.player, event.block, blocks, 100F)
            Bukkit.getPluginManager().callEvent(multiBlockBreakEvent)

            if (multiBlockBreakEvent.isCancelled) {
                return
            }

            // send notification
            sendMessage(event.player, "The layer you were mining has collapsed!")
        }
    }

    private fun readCost(): Long {
        return EnchantsModule.config.getLong("jack-hammer.cost")
    }

    private fun readChance(): Double {
        return EnchantsModule.config.getDouble("jack-hammer.chance")
    }

}