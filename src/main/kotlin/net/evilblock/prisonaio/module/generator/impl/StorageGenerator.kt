/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl

import com.intellectualcrafters.plot.`object`.PlotId
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class StorageGenerator(
    instanceId: UUID,
    plotId: PlotId,
    owner: UUID,
    bounds: Cuboid,
    rotation: Rotation
) : Generator(
    instanceId,
    plotId,
    owner,
    bounds,
    rotation
) {

    var itemStorage: ArrayList<ItemStack> = arrayListOf()

    open fun getMaxStorageSize(): Int {
        return 3
    }

    fun addToItemStorage(itemStack: ItemStack): ItemStack? {
        var remainingAmount = itemStack.amount

        for (itemInSlot in itemStorage) {
            if (itemInSlot.amount >= itemInSlot.maxStackSize) {
                continue
            }

            if (!ItemUtils.isSimilar(itemInSlot, itemStack) || !ItemUtils.hasSameLore(itemInSlot, itemStack) || !ItemUtils.hasSameEnchantments(itemInSlot, itemStack)) {
                continue
            }

            val maxInsert = itemInSlot.maxStackSize - itemInSlot.amount
            if (maxInsert <= 0) {
                continue
            }

            if (remainingAmount <= maxInsert) {
                itemInSlot.amount = itemInSlot.amount + remainingAmount
                remainingAmount = 0
            } else {
                itemInSlot.amount = itemInSlot.amount + maxInsert
                remainingAmount -= maxInsert
            }

            if (remainingAmount <= 0) {
                break
            }
        }

        if (remainingAmount > 0) {
            val remainingItem = ItemBuilder.copyOf(itemStack).amount(remainingAmount.coerceAtMost(itemStack.type.maxStackSize)).build()

            if (itemStorage.size < getMaxStorageSize()) {
                itemStorage.add(remainingItem)
            } else {
                return remainingItem
            }
        }

        return null
    }

    fun removeFromItemStorage(itemStack: ItemStack) {
        var remainingAmount = itemStack.amount

        val iterator = itemStorage.iterator()
        while (iterator.hasNext()) {
            val itemInSlot = iterator.next()

            if (!ItemUtils.isSimilar(itemInSlot, itemStack) || !ItemUtils.hasSameLore(itemInSlot, itemStack) || !ItemUtils.hasSameEnchantments(itemInSlot, itemStack)) {
                continue
            }

            if (itemInSlot.amount <= remainingAmount) {
                iterator.remove()
                remainingAmount -= itemInSlot.amount
            } else {
                itemInSlot.amount -= remainingAmount
                remainingAmount = 0
            }

            if (remainingAmount <= 0) {
                break
            }
        }
    }

}