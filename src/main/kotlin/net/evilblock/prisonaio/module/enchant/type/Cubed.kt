/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.*

object Cubed : AbstractEnchant("cubed", "Cubed", 3) {

    override val iconColor: Color
        get() = Color.ORANGE

    override val textColor: ChatColor
        get() = ChatColor.RED

    override val menuDisplay: Material
        get() = Material.BEACON

    override fun onBreak(event: BlockBreakEvent, itemStack: ItemStack?, level: Int, region: Region) {
        val blocks: MutableList<Block> = ArrayList()
        val l = event.block.location
        for (x in l.blockX - level..l.blockX + level) {
            for (y in l.blockY - level..l.blockY + level) {
                for (z in l.blockZ - level..l.blockZ + level) {
                    val block = Location(l.world, x.toDouble(), y.toDouble(), z.toDouble())
                    val type = block.block.type
                    if (type != Material.ENDER_CHEST && type != Material.BEDROCK && type != Material.AIR) {
                        if (region.getBreakableCuboid() != null && region.getBreakableCuboid()!!.contains(block.block)) {
                            blocks.add(block.block)
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

    override fun getCost(level: Int): Long {
        return Long.MAX_VALUE
    }

    override fun getRefundTokens(level: Int): Long {
        return 0
    }

    private fun readNerf(): Array<Float> {
        return EnchantsModule.config.getFloatList("cubed.nerf").toTypedArray()
    }

}