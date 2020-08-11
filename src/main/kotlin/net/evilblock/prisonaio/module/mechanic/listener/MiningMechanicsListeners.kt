/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.perk.Perk
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.floor
import kotlin.random.Random

/**
 * Handles the server's mining mechanics.
 */
object MiningMechanicsListeners : Listener {

    /**
     * Handles auto-sell and drops-to-inventory for a single block break.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        // get the item in the player's hand
        val itemInHand = event.player.inventory.itemInMainHand ?: return

        // make sure the item is a tool
        if (!MechanicsModule.isTool(itemInHand)) {
            return
        }

        // add drops to inventory if the block is on the drops-to-inv ignore list
        if (MechanicsModule.getDropsToInvIgnoredBlocks().contains(event.block.type)) {
            return
        }

        // we never want items to drop naturally
        event.isDropItems = false

        val user = UserHandler.getUser(event.player.uniqueId)
        val drops = getBlockDrops(itemInHand, user, event.player, event.block)

        // simulate block breaking
        event.block.type = Material.AIR
        event.block.state.update()

        val region = RegionsModule.findRegion(event.block.location)

        Tasks.async {
            val backpacks = BackpackHandler.findBackpacksInInventory(event.player)

            if (region.supportsAutoSell() && user.perks.isAutoSellEnabled(event.player)) {
                ShopHandler.sellItems(event.player, drops, true)

                for (backpack in backpacks) {
                    ShopHandler.sellItems(event.player, backpack.contents.values, true)
                }
            }

            drops.forEach { drop ->
                val notInserted = event.player.inventory.addItem(drop)
                if (notInserted.isNotEmpty() && backpacks.isNotEmpty()) {
                    for (backpack in backpacks) {
                        val backpackMods = hashMapOf<Int, ItemStack?>()
                        for ((key, item) in notInserted) {
                            backpackMods[key] = backpack.addItem(item)
                        }

                        for ((key, item) in backpackMods) {
                            if (item == null) {
                                notInserted.remove(key)
                            } else {
                                notInserted[key] = item
                            }
                        }

                        if (notInserted.isEmpty()) {
                            break
                        }
                    }
                }
            }

            event.player.updateInventory()
        }
    }

    /**
     * Handles auto-sell and drops-to-inventory for a multi block break.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
        // get the item in the player's hand
        val itemInHand = event.player.inventory.itemInMainHand ?: return

        // make sure the item is a tool
        if (!MechanicsModule.isTool(itemInHand)) {
            return
        }

        val user = UserHandler.getUser(event.player.uniqueId)
        val ignoredBlocks = MechanicsModule.getDropsToInvIgnoredBlocks()
        val validBlocks = event.blockList.filter { !ignoredBlocks.contains(it.type) && (event.yield == 100F || Random.nextInt(100) < event.yield) }

        val drops = arrayListOf<ItemStack>()
        for (block in validBlocks) {
            drops.addAll(getBlockDrops(itemInHand, user, event.player, block))
        }

        // simulate block breaking
        if (!event.skipBlockUpdates) {
            for (block in validBlocks) {
                block.type = Material.AIR
                block.state.update()
            }
        }

        val backpacks = BackpackHandler.findBackpacksInInventory(event.player)

        val region = RegionsModule.findRegion(event.block.location)
        if (region.supportsAutoSell() && user.perks.isAutoSellEnabled(event.player)) {
            ShopHandler.sellItems(event.player, drops, true)

            for (backpack in backpacks) {
                ShopHandler.sellItems(event.player, backpack.contents.values, true)
            }
        }

        Tasks.async {
            drops.forEach { drop ->
                val notInserted = event.player.inventory.addItem(drop)
                if (notInserted.isNotEmpty() && backpacks.isNotEmpty()) {
                    for (backpack in backpacks) {
                        val backpackMods = hashMapOf<Int, ItemStack?>()
                        for ((key, item) in notInserted) {
                            backpackMods[key] = backpack.addItem(item)
                        }

                        for ((key, item) in backpackMods) {
                            if (item == null) {
                                notInserted.remove(key)
                            } else {
                                notInserted[key] = item
                            }
                        }

                        if (notInserted.isEmpty()) {
                            break
                        }
                    }
                }
            }
        }
    }

    private fun isGlass(type: Material): Boolean {
        return type == Material.GLASS || type == Material.STAINED_GLASS || type == Material.STAINED_GLASS_PANE
    }

    private fun getBlockDrops(itemInHand: ItemStack, user: User, player: Player, block: Block): MutableList<ItemStack> {
        val toAdd = arrayListOf<ItemStack>()

        // 1. if block is glass & tool is silk touch
        // 2. if block drops with item context is not empty
        // 3. natural block drops
        val drops = if (isGlass(block.type) && itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
            listOf(ItemBuilder.of(block.type).amount(1).data(block.state.data.data.toShort()).build())
        } else {
            val vanillaDrops = block.getDrops(itemInHand)
            if (vanillaDrops.isNotEmpty()) {
                vanillaDrops
            } else {
                block.drops
            }
        }

        val autoSmeltEnabled = UsersModule.isAutoSmeltPerkEnabledByDefault() || user.perks.isPerkEnabled(Perk.AUTO_SMELT) && user.perks.hasPerk(player, Perk.AUTO_SMELT)
        val region = RegionsModule.findRegion(block.location)
        val useFortune = region.supportsAbilityEnchants() && itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)

        val dropsIterator = drops.iterator()
        while (dropsIterator.hasNext()) {
            var drop = dropsIterator.next()

            if (itemInHand.containsEnchantment(Enchantment.SILK_TOUCH) && !autoSmeltEnabled) {
                drop.amount = 1

                drop.type = if (block.type == Material.REDSTONE_ORE || block.type == Material.GLOWING_REDSTONE_ORE) {
                    Material.REDSTONE_ORE
                } else {
                    block.type
                }

                toAdd.add(drop)
                break
            }

            if (autoSmeltEnabled) {
                drop = getSmelted(block, drop)
            }

            if (useFortune) {
                if (MechanicsModule.isFortuneBlock(block.type)) {
                    drop.amount = getFortuneAmount(itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS))
                }
            }

            toAdd.add(drop)
        }

        return toAdd
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
        val isRandomAmount = MechanicsModule.isFortuneRandom()
        val multiplier = MechanicsModule.getFortuneMultiplier()
        var modifier = MechanicsModule.getFortuneModifier()
        val minDrops = MechanicsModule.getFortuneMinDrops()
        val maxDrops = MechanicsModule.getFortuneMaxDrops()
        var amountToDrop = (floor(level * multiplier) + 1.0).toInt()

        when {
            amountToDrop > maxDrops -> {
                amountToDrop = if (isRandomAmount) {
                    Random.nextInt(maxDrops) + minDrops
                } else {
                    maxDrops + minDrops
                }
            }
            isRandomAmount -> {
                amountToDrop = Random.nextInt(amountToDrop) + minDrops
            }
            else -> {
                amountToDrop += minDrops
            }
        }

        if (modifier > 0) {
            modifier = Random.nextInt(modifier)
        }

        if (modifier <= 0) {
            return amountToDrop
        }

        if (Random.nextBoolean()) {
            return amountToDrop + modifier
        }

        return if (amountToDrop - modifier > 1) {
            amountToDrop - modifier
        } else {
            1
        }
    }

}