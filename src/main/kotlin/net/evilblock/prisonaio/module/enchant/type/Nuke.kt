package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import net.evilblock.prisonaio.module.enchant.event.NukeExplodeEvent
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.module.mine.Mine
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

import java.util.SplittableRandom

object Nuke : AbstractEnchant("nuke", "Nuke", 2) {

	val random = SplittableRandom()

	override val iconColor: Color
		get() = Color.AQUA

	override val textColor: ChatColor
		get() = ChatColor.RED

	override val menuDisplay: Material?
		get() = Material.TNT

	override fun getCost(level: Int): Long {
		return (readCost() + ((level - 1) * 25000)).toLong()
	}

	override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
		if (Chance.percent(readChance())) {
			if (region.getBreakableRegion() == null) {
				return
			}

			val blocks = arrayListOf<Block>()
			val explode = arrayListOf<Location>()

			region.getBreakableRegion()?.blocks?.forEach { block ->
				blocks.add(block)

				if (Chance.percent(2)) {
					explode.add(block.location)
				}
			}

			if (region is Mine) {
				Bukkit.broadcastMessage("${ChatColor.RED}${event.player.name} ${ChatColor.GRAY}has nuked the ${ChatColor.RED}${region.id} ${ChatColor.GRAY}mine!")
			}

			val multiBlockBreakEvent = MultiBlockBreakEvent(event.player, event.block, blocks, 100F)
			Bukkit.getPluginManager().callEvent(multiBlockBreakEvent)

			if (multiBlockBreakEvent.isCancelled) {
				return
			}

			val nukeEvent = NukeExplodeEvent(event.player, event.block, region, level)
			Bukkit.getPluginManager().callEvent(nukeEvent)

			region.resetBreakableRegion()

			for (location in explode) {
				location.world.spawnParticle(Particle.EXPLOSION_HUGE, location, 1)
			}

			event.isCancelled = true
		}
	}

	private fun readCost(): Double {
		return EnchantsModule.config.getDouble("nuke.cost")
	}

	private fun readChance(): Double {
		return EnchantsModule.config.getDouble("nuke.chance")
	}

}
