/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.enchant.event.NukeExplodeEvent
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.privatemine.PrivateMine
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

import java.util.SplittableRandom

object Nuke : AbstractEnchant("nuke", "Nuke", 2) {

	val random = SplittableRandom()

	override val iconColor: Color
		get() = Color.RED

	override val textColor: ChatColor
		get() = ChatColor.RED

	override val menuDisplay: Material?
		get() = Material.TNT

//	override fun getCost(level: Int): Long {
//		return (readCost() + ((level - 1) * 25000))
//	}

	override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
		if (region.getBreakableCuboid() == null) {
			return
		}

		if (Chance.percent(readChance())) {
			event.isCancelled = true

			Tasks.async {
				val blocks = arrayListOf<Block>()
				val explode = arrayListOf<Location>()

				region.getBreakableCuboid()?.blocks?.forEach { block ->
					blocks.add(block)

					if (Chance.percent(2)) {
						explode.add(block.location)
					}
				}

				Tasks.sync {
					val multiBlockBreakEvent = MultiBlockBreakEvent(event.player, event.block, blocks, 100F, skipBlockUpdates = true)
					Bukkit.getPluginManager().callEvent(multiBlockBreakEvent)

					if (multiBlockBreakEvent.isCancelled) {
						return@sync
					}

					val nukeEvent = NukeExplodeEvent(event.player, event.block, region, level)
					Bukkit.getPluginManager().callEvent(nukeEvent)

					Tasks.async {
						region.resetBreakableCuboid()

						if (region is Mine) {
							Bukkit.broadcastMessage("${ChatColor.RED}${event.player.name} ${ChatColor.GRAY}has nuked the ${ChatColor.RED}${region.id} ${ChatColor.GRAY}mine!")
						} else if (region is PrivateMine) {
							if (region.owner == event.player.uniqueId) {
								Bukkit.broadcastMessage("${ChatColor.RED}${event.player.name} ${ChatColor.GRAY}has nuked their ${ChatColor.RED}Private Mine${ChatColor.GRAY}!")
							} else {
								val ownerName = region.getOwnerName()
								Bukkit.broadcastMessage("${ChatColor.RED}${event.player.name} ${ChatColor.GRAY}has nuked $ownerName's ${ChatColor.RED}Private Mine${ChatColor.GRAY}!")
							}
						}

						for (location in explode) {
							location.world.spawnParticle(Particle.EXPLOSION_HUGE, location, 1)
						}
					}
				}
			}
		}
	}

}
