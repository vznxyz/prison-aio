package net.evilblock.prisonaio.module.mechanic.listener

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.mechanic.region.Regions
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
import java.util.*
import kotlin.math.floor
import kotlin.random.Random

/**
 * Handles the server's mining mechanics.
 */
object MiningMechanicsListeners : Listener {

    private val SPLITTABLE_RANDOM = SplittableRandom()

    /**
     * Handles auto-sell and drops-to-inventory for a single block break.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onBlockBreakEventHighest(event: BlockBreakEvent) {
        // get the item in the player's hand
        val itemInHand = event.player.inventory.itemInMainHand ?: return

        // make sure the item is a tool
        if (!isTool(itemInHand)) {
            return
        }

        // add drops if block is not on drops-to-inv ignored block list
        if (MechanicsModule.getDropsToInvIgnoredBlocks().contains(event.block.type)) {
            return
        }

        val user = UserHandler.getUser(event.player.uniqueId)

        // get drops based on tool and block
        val drops = getBlockDrops(itemInHand, user, event.player, event.block)

        // if auto-sell is enabled, sell our drops instead of adding them to inventory
        if (user.perks.isPerkEnabled(Perk.AUTO_SELL) && user.perks.hasPerk(event.player, Perk.AUTO_SELL)) {
            try {
                val dropsRemaining = ShopHandler.sellDrops(event.player, drops)
                if (dropsRemaining.isNotEmpty()) {
                    dropsRemaining.forEach { drop -> event.player.inventory.addItem(drop) }
                    event.player.updateInventory()
                }
            } catch (e: IllegalStateException) {}
        } else {
            drops.forEach { drop -> event.player.inventory.addItem(drop) }
            event.player.updateInventory()
        }

        // send inventory updates
        event.player.updateInventory()

        // simulate block breaking
        event.block.type = Material.AIR
        event.block.state.update()

        // we drop the blocks ourselves
        event.isDropItems = false
    }

    /**
     * Handles auto-sell and drops-to-inventory for a multi block break.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
        // get the item in the player's hand
        val itemInHand = event.player.inventory.itemInMainHand
        if (itemInHand != null) {
            // make sure the item is a tool
            if (!isTool(itemInHand)) {
                return
            }

            val user = UserHandler.getUser(event.player.uniqueId)
            val ignoredBlocks = MechanicsModule.getDropsToInvIgnoredBlocks()
            val validBlocks = event.blockList.filter { !ignoredBlocks.contains(it.type) && (event.yield == 100F || SPLITTABLE_RANDOM.nextInt(100) < event.yield) }

            // get drops based on tool and block
            val drops = arrayListOf<ItemStack>()
            for (block in validBlocks) {
                drops.addAll(getBlockDrops(itemInHand, user, event.player, block))
            }

            // if auto-sell is enabled, sell our drops instead of adding them to inventory
            if (user.perks.isPerkEnabled(Perk.AUTO_SELL) && user.perks.hasPerk(event.player, Perk.AUTO_SELL)) {
                try {
                    val dropsRemaining = ShopHandler.sellDrops(event.player, drops)
                    if (dropsRemaining.isNotEmpty()) {
                        dropsRemaining.forEach { drop -> event.player.inventory.addItem(drop) }
                        event.player.updateInventory()
                    }
                } catch (e: IllegalStateException) {}
            } else {
                drops.forEach { drop -> event.player.inventory.addItem(drop) }
                event.player.updateInventory()
            }

            // send inventory updates
            event.player.updateInventory()

            // simulate block breaking
            for (block in validBlocks) {
                block.type = Material.AIR
                block.state.update()
            }
        }
    }

    private fun isGlass(type: Material): Boolean {
        return type == Material.GLASS || type == Material.STAINED_GLASS || type == Material.STAINED_GLASS_PANE
    }

    private fun isTool(itemStack: ItemStack?): Boolean {
        if (itemStack == null || itemStack.type == Material.AIR) {
            return false
        }

        return when(itemStack.type.ordinal) {
            255, 256, 257, 260, 268, 269, 270, 272, 274, 276, 277, 278, 283, 284, 285, 358 -> true
            else -> false
        }
    }

    private fun getSmelted(broken: Block, returnItem: ItemStack): ItemStack {
        val autoSmeltBlocks = MechanicsModule.getAutoSmeltBlocks()
        if (autoSmeltBlocks.isEmpty()) {
            return returnItem
        }

        for (smelt in autoSmeltBlocks) {
            if (broken.type == smelt.key) {
                returnItem.type = smelt.value
                break
            }
        }

        return returnItem
    }

    private fun getFortuneAmount(level: Int): Int {
        val mechanics = MechanicsModule
        val isRandomAmount = mechanics.isFortuneRandom()
        val multiplier = mechanics.getFortuneMultiplier()
        var modifier = mechanics.getFortuneModifier()
        val minDrops = mechanics.getFortuneMinDrops()
        val maxDrops = mechanics.getFortuneMaxDrops()
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

    private fun getBlockDrops(_itemInHand: ItemStack, user: User, player: Player, block: Block): List<ItemStack> {
        var itemInHand = _itemInHand
        if (itemInHand.type == Material.BOW) {
            itemInHand = ItemStack(Material.DIAMOND_PICKAXE, 1)
        }

        val toAdd = arrayListOf<ItemStack>()

        var drops = if (block.getDrops(itemInHand).isNotEmpty()) {
            block.getDrops(itemInHand)
        } else {
            block.drops
        }

        if (isGlass(block.type) && itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
            drops = listOf(
                ItemBuilder.of(block.type)
                    .amount(1)
                    .data(block.state.data.data.toShort())
                    .build()
            )
        }

        val isTool = isTool(itemInHand)
        val autoSmeltEnabled = UsersModule.isAutoSmeltPerkEnabledByDefault() || user.perks.isPerkEnabled(Perk.AUTO_SMELT) && user.perks.hasPerk(player, Perk.AUTO_SMELT)
        val hasSilkTouch = itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)
        val hasFortune = itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)
        val dropsIterator = drops.iterator()
        val region = Regions.findRegion(block.location)

        while (dropsIterator.hasNext()) {
            var breaks = false
            var drop = dropsIterator.next()

            if (isTool) {
                if (autoSmeltEnabled) {
                    drop = getSmelted(
                        block,
                        drop
                    )
                }

                if (hasSilkTouch && !autoSmeltEnabled) {
                    drop.amount = 1

                    drop.type = if (block.type == Material.REDSTONE_ORE || block.type == Material.GLOWING_REDSTONE_ORE) {
                        Material.REDSTONE_ORE
                    } else {
                        block.type
                    }

                    breaks = true
                }

                if (region != null && region.supportsEnchants()) {
                    if (hasFortune) {
                        val mechanics = MechanicsModule
                        if (mechanics.isFortuneBlock(block.type)) {
                            drop.amount = getFortuneAmount(itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS))
                        }
                    }
                }
            }

            toAdd.add(drop)

            if (breaks) {
                break
            }
        }

        return toAdd
    }

}