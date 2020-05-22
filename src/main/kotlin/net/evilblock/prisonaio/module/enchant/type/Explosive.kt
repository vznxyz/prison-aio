package net.evilblock.prisonaio.module.enchant.type

import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.mechanic.region.Region
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.*

object Explosive : AbstractEnchant("explosive", "Explosive", 50) {

    private val random = Random()

    override val iconColor: Color
        get() = Color.LIME

    override val textColor: ChatColor
        get() = ChatColor.RED

    override val menuDisplay: Material
        get() = Material.SULPHUR

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        if (random.nextInt(100) > level) {
            return
        }

        val blocks: MutableList<Block> = ArrayList()
        val location = event.block.location
        val radius = 2

        for (x in location.blockX - radius..location.blockX + radius) {
            for (y in location.blockY - radius..location.blockY + radius) {
                for (z in location.blockZ - radius..location.blockZ + radius) {
                    val block = Location(location.world, x.toDouble(), y.toDouble(), z.toDouble())
                    val type = block.block.type

                    if (type != Material.ENDER_CHEST && type != Material.BEDROCK && type != Material.AIR) {
                        val isInRegion = region.getBreakableRegion() != null && region.getBreakableRegion()!!.contains(block)
                        val canBuild = WorldGuardPlugin.inst().canBuild(event.player, block)
                        val chance = random.nextInt(100) <= level

                        if (isInRegion && canBuild && chance) {
                            blocks.add(block.block)
                        }
                    }
                }
            }
        }

        if (blocks.isEmpty()) {
            return
        }

        val multiBlockBreakEvent = MultiBlockBreakEvent(event.player, event.block, blocks, 100f)
        Bukkit.getPluginManager().callEvent(multiBlockBreakEvent)
    }

    override fun getCost(level: Int): Long {
        return (7000 + ((level - 1) * 1000)).toLong()
    }
}