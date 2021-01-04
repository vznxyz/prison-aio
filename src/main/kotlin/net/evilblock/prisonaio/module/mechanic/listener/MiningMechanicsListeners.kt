/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.tool.enchant.impl.Fortune
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.tool.enchant.impl.TokenPouch
import net.evilblock.prisonaio.module.tool.enchant.impl.Tokenator
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.perk.Perk
import net.minecraft.server.v1_12_R1.BlockPosition
import net.minecraft.server.v1_12_R1.ChunkCoordIntPair
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange
import net.minecraft.server.v1_12_R1.PacketPlayOutMultiBlockChange
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Handles the server's mining mechanics.
 */
object MiningMechanicsListeners : Listener {

    /**
     * Handles auto-sell and drops-to-inventory for a single block break.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val itemInHand = event.player.inventory.itemInMainHand ?: return

        if (!MechanicsModule.isTool(itemInHand)) {
            return
        }

        if (MechanicsModule.getDropsToInvIgnoredBlocks().contains(event.block.type)) {
            return
        }

        event.isDropItems = false

        val user = UserHandler.getUser(event.player.uniqueId)
        val autoSmeltEnabled = UsersModule.isAutoSmeltPerkEnabledByDefault() || user.perks.isPerkEnabled(Perk.AUTO_SMELT) && user.perks.hasPerk(event.player, Perk.AUTO_SMELT)
        val region = RegionHandler.findRegion(event.block.location)

        val fortuneLevel = if (region.supportsAbilityEnchants() && itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)
        } else {
            -1
        }

        val blockDrops = arrayListOf<ItemStack>()
        getBlockDroppedItems(user, event.player, event.block, region, blockDrops, itemInHand, autoSmeltEnabled, fortuneLevel)

        event.block.type = Material.AIR
        event.block.state.update()

        Tasks.async {
            val backpacks = BackpackHandler.findBackpacksInInventory(event.player)

            Tasks.sync {
                if (region.supportsAutoSell() && user.perks.isAutoSellEnabled(event.player)) {
                    ShopHandler.sellItems(event.player, blockDrops, true)

                    for ((backpackItem, backpack) in backpacks) {
                        ShopHandler.sellItems(event.player, backpack.contents, true)
                    }
                }

                for (drop in blockDrops) {
                    var toAdd: ItemStack? = drop
                    for ((backpackItem, backpack) in backpacks) {
                        toAdd = backpack.addItem(toAdd!!)

                        if (toAdd == null) {
                            break
                        }
                    }

                    if (toAdd != null) {
                        event.player.inventory.addItem(toAdd)
                    }
                }
            }
        }
    }

    /**
     * Handles auto-sell and drops-to-inventory for a multi block break.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
        val itemInHand = event.player.inventory.itemInMainHand ?: return
        val pickaxe = PickaxeHandler.getPickaxeData(itemInHand) ?: return

        val user = UserHandler.getUser(event.player.uniqueId)
        val autoSmeltEnabled = UsersModule.isAutoSmeltPerkEnabledByDefault() || user.perks.isPerkEnabled(Perk.AUTO_SMELT) && user.perks.hasPerk(event.player, Perk.AUTO_SMELT)
        val region = RegionHandler.findRegion(event.block.location)

        val ignoredBlocks = MechanicsModule.getDropsToInvIgnoredBlocks()
        val validBlocks = event.blockList.filter { isValidBlock(it) && !ignoredBlocks.contains(it.type) && (event.yield == 100F || ThreadLocalRandom.current().nextInt(100) < event.yield) }

        val fortuneLevel = if (!pickaxe.isEnchantDisabled(Fortune) && region.supportsAbilityEnchants() && pickaxe.enchants.containsKey(Fortune)) {
            pickaxe.enchants[Fortune]!!
        } else {
            -1
        }

        val collectedDrops = arrayListOf<ItemStack>()

        for (block in validBlocks) {
            getBlockDroppedItems(user, event.player, block, region, collectedDrops, itemInHand, autoSmeltEnabled, fortuneLevel)

            if (event.useRewardsModifiers) {
                if (pickaxe.enchants.containsKey(Tokenator)) {
                    val tokenAmount = (pickaxe.enchants[Tokenator]!! * Tokenator.readMultiplier()).coerceAtLeast(1.0).roundToInt().toLong()
                    user.addTokensBalance(tokenAmount)
                }

                if (pickaxe.enchants.containsKey(TokenPouch)) {
                    val tokenPouchLevel = pickaxe.enchants[TokenPouch]!!
                    TokenPouch.attemptFindPouch(event.player, tokenPouchLevel)
                }
            }
        }

        if (collectedDrops.isNotEmpty()) {
            Tasks.async {
                val backpacks = BackpackHandler.findBackpacksInInventory(event.player)

                if (!event.skipBlockUpdates) {
                    val world = (event.block.world as CraftWorld).handle
                    val blockData = net.minecraft.server.v1_12_R1.Block.getById(0).blockData

                    for (block in validBlocks) {
                        val chunk = world.getChunkAt(block.x shr 4, block.z shr 4)
                        chunk.sections[block.y shr 4].setType(block.x and 15, block.y and 15, block.z and 15, blockData)
                    }

                    if (validBlocks.size == 1) {
                        val block = validBlocks.first()
                        val packet = PacketPlayOutBlockChange((block.world as CraftWorld).handle, BlockPosition(block.x, block.y, block.z))
                        MinecraftProtocol.send(event.player, packet)
                    } else {
                        val modifiedChunks = hashMapOf<ChunkCoordIntPair, Pair<PacketPlayOutMultiBlockChange, MutableList<PacketPlayOutMultiBlockChange.MultiBlockChangeInfo>>>()
                        val chunkViewers = hashMapOf<ChunkCoordIntPair, List<Player>>()

                        for (block in validBlocks) {
                            val chunkCoords = ChunkCoordIntPair(block.chunk.x, block.chunk.z)
                            if (!chunkViewers.containsKey(chunkCoords)) {
                                chunkViewers[chunkCoords] = Bukkit.getOnlinePlayers().filter { it.world == block.world && isChunkInViewDistance(it, chunkCoords) }
                            }

                            if (!modifiedChunks.containsKey(chunkCoords)) {
                                val packet = PacketPlayOutMultiBlockChange()
                                Reflection.setDeclaredFieldValue(packet, "a", chunkCoords)
                                modifiedChunks[chunkCoords] = Pair(packet, arrayListOf())
                            }

                            val packet = modifiedChunks[chunkCoords]!!
                            packet.second.add(packet.first.MultiBlockChangeInfo(toIndex(block.x and 15, block.y, block.z and 15), net.minecraft.server.v1_12_R1.Block.getById(0).blockData))
                        }

                        for ((chunkCoords, chunkPacket) in modifiedChunks) {
                            val multiBlockChangeInfoArray = chunkPacket.second.toTypedArray()
                            Reflection.setDeclaredFieldValue(chunkPacket.first, "b", multiBlockChangeInfoArray)

                            if (chunkViewers.containsKey(chunkCoords)) {
                                for (player in chunkViewers[chunkCoords]!!) {
                                    MinecraftProtocol.send(player, chunkPacket.first)
                                }
                            }
                        }
                    }
                }

                if (region.supportsAutoSell() && user.perks.isAutoSellEnabled(event.player)) {
                    ShopHandler.sellItems(event.player, collectedDrops, true)

                    for ((backpackItem, backpack) in backpacks) {
                        ShopHandler.sellItems(event.player, backpack.contents, true)
                    }
                }

                Tasks.sync {
                    for (drop in collectedDrops) {
                        var drop: ItemStack? = drop
                        for ((backpackItem, backpack) in backpacks) {
                            drop = backpack.addItem(drop!!)

                            if (drop == null) {
                                break
                            }
                        }

                        if (drop == null) {
                            continue
                        }

                        event.player.inventory.addItem(drop)
                    }
                }
            }
        }
    }

    private fun isGlass(type: Material): Boolean {
        return type == Material.GLASS || type == Material.STAINED_GLASS || type == Material.STAINED_GLASS_PANE
    }

    private fun getBlockDroppedItems(user: User, player: Player, block: Block, region: Region, list: MutableList<ItemStack>, itemInHand: ItemStack, autoSmelt: Boolean, fortune: Int) {
        val unprocessedDrops = if (isGlass(block.type) && itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
            listOf(ItemBuilder.of(block.type).amount(1).data(block.state.data.data.toShort()).build())
        } else {
            val vanillaDrops = block.getDrops(itemInHand)
            if (vanillaDrops.isNotEmpty()) {
                vanillaDrops
            } else {
                block.drops
            }
        }

        val unprocessedIterator = unprocessedDrops.iterator()
        while (unprocessedIterator.hasNext()) {
            var drop = unprocessedIterator.next()

            if (itemInHand.containsEnchantment(Enchantment.SILK_TOUCH) && !autoSmelt) {
                drop.amount = 1

                drop.type = if (block.type == Material.REDSTONE_ORE || block.type == Material.GLOWING_REDSTONE_ORE) {
                    Material.REDSTONE_ORE
                } else {
                    block.type
                }

                list.add(drop)
                break
            }

            if (autoSmelt) {
                drop = getSmelted(block, drop)
            }

            if (fortune != -1) {
                if (MechanicsModule.isFortuneBlock(block.type)) {
                    drop.amount = getFortuneAmount(fortune)
                }
            }

            list.add(drop)
        }
    }

    private fun isChunkInViewDistance(player: Player, chunk: ChunkCoordIntPair): Boolean {
        val viewDistanceChunks = Bukkit.getServer().viewDistance
        return player.location.chunk.x in (chunk.x - viewDistanceChunks - 2)..(chunk.x + viewDistanceChunks + 2)
                || player.location.chunk.z in (chunk.z - viewDistanceChunks - 2)..(chunk.z + viewDistanceChunks + 2)
    }

    private fun getSmelted(broken: Block, returnItem: ItemStack): ItemStack {
        val autoSmeltBlocks = MechanicsModule.getAutoSmeltBlocks()
        if (autoSmeltBlocks.isEmpty()) {
            return returnItem
        }

        for ((source, product) in autoSmeltBlocks) {
            if (broken.type == source) {
                returnItem.type = product
                break
            }
        }

        return returnItem
    }

    private fun getFortuneAmount(level: Int): Int {
        val multiplier = MechanicsModule.getFortuneMultiplier()
        val modifier = MechanicsModule.getFortuneModifier()
        val minDrops = MechanicsModule.getFortuneMinDrops()
        val maxDrops = MechanicsModule.getFortuneMaxDrops()

        var drops = min((1.0 + (level * multiplier)).toInt(), maxDrops)

        if (modifier > 0) {
            drops -= ThreadLocalRandom.current().nextDouble(level * modifier).toInt()
        }

        return drops.coerceAtLeast(minDrops)
    }

    private fun toIndex(x: Int, y: Int, z: Int): Short {
        return ((x shl 12) or (z shl 8) or y).toShort()
    }

    private fun isValidBlock(block: Block): Boolean {
        return block.type != Material.BARRIER && block.type != Material.ENDER_CHEST && block.type != Material.AIR && block.type != Material.BEDROCK
    }

}