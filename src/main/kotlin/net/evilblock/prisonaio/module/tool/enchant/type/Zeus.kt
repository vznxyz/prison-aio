/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.type

import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.tool.enchant.event.ZeusExplodeEvent
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*

object Zeus : AbilityEnchant(id = "zeus", enchant = "Zeus", maxLevel = 3) {

    override val iconColor: Color
        get() = Color.FUCHSIA

    override val textColor: ChatColor
        get() = ChatColor.LIGHT_PURPLE

//    override fun getCost(level: Int): Long {
//        return readCost() * level
//    }

    override val menuDisplay: Material?
        get() = Material.GLOWSTONE_DUST

    override fun onInteract(event: PlayerInteractEvent, enchantedItem: ItemStack, level: Int) {
        super.onInteract(event, enchantedItem, level)

        if (event.isCancelled) {
            return
        }

        if (!isOnGlobalCooldown(event.player)) {
            return
        }

        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            event.isCancelled = true

            val location = event.clickedBlock.location

            val region = RegionHandler.findRegion(location)
            if (!region.supportsAbilityEnchants() || region.getBreakableCuboid() == null) {
                return
            }

            val largeRadius = 6
            val blocks: MutableList<Block> = ArrayList()

            val sphere = generateSphere(event.clickedBlock.location, largeRadius, false).map { it.block }
            for (block in sphere) {
                if (block.y <= event.clickedBlock.y && region.getBreakableCuboid()!!.contains(block)) {
                    blocks.add(block)
                }
            }

            val maxY = region.getBreakableCuboid()!!.upperY
            for (x in -largeRadius..largeRadius) {
                for (z in -largeRadius..largeRadius) {
                    for (y in event.clickedBlock.y..maxY) {
                        val block = location.world.getBlockAt(location.blockX + x, y, location.blockZ + z)
                        if (isValidBlock(block) && region.getBreakableCuboid()!!.contains(block)) {
                            blocks.add(block)
                        }
                    }
                }
            }

            if (blocks.isEmpty()) {
                return
            }

            // broadcast zeus explode event
            val zeusExplodeEvent = ZeusExplodeEvent(event.player, blocks, event.clickedBlock, level)
            Bukkit.getPluginManager().callEvent(zeusExplodeEvent)

            if (zeusExplodeEvent.isCancelled) {
                return
            }

            // broadcast multi block break event (use rewards modifiers)
            val multiBlockBreakEvent = MultiBlockBreakEvent(event.player, zeusExplodeEvent.origin, blocks, 100F, useRewardsModifiers = true)
            multiBlockBreakEvent.call()

            // send notification
            sendMessage(event.player, "Your mighty powers jolt a lightning strike, which forms a crater!")

            // send lightning
            MinecraftProtocol.send(event.player, createLightningPacket(event.clickedBlock.getRelative(BlockFace.UP).location))
        }
    }

    private fun createLightningPacket(location: Location): Any {
        val packet = MinecraftProtocol.newPacket("PacketPlayOutSpawnEntityWeather")

        Reflection.setDeclaredFieldValue(packet, "a", 128)
        Reflection.setDeclaredFieldValue(packet, "b", (location.x * 32.0).toInt())
        Reflection.setDeclaredFieldValue(packet, "c", (location.y * 32.0).toInt())
        Reflection.setDeclaredFieldValue(packet, "d", (location.z * 32.0).toInt())
        Reflection.setDeclaredFieldValue(packet, "e", 1)

        return packet
    }

    private fun isValidBlock(block: Block): Boolean {
        return block.type != Material.BARRIER && block.type != Material.ENDER_CHEST && block.type != Material.AIR && block.type != Material.BEDROCK
    }

    private fun generateSphere(centerBlock: Location, radius: Int, hollow: Boolean): Collection<Location> {
        val circleBlocks: MutableList<Location> = ArrayList()
        val bx = centerBlock.blockX
        val by = centerBlock.blockY
        val bz = centerBlock.blockZ

        for (x in bx - radius..bx + radius) {
            for (y in by - radius..by + radius) {
                for (z in bz - radius..bz + radius) {
                    val distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y)).toDouble()
                    if (distance < radius * radius && !(hollow && distance < (radius - 1) * (radius - 1))) {
                        circleBlocks.add(Location(centerBlock.world, x.toDouble(), y.toDouble(), z.toDouble()))
                    }
                }
            }
        }

        return circleBlocks
    }

}