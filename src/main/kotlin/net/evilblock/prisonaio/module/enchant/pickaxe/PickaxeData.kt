/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.pickaxe

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.serialize.EnchantsMapReferenceSerializer
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.*

class PickaxeData(val uuid: UUID) {

    @JsonAdapter(EnchantsMapReferenceSerializer::class)
    var enchants: MutableMap<AbstractEnchant, Int> = hashMapOf()

    fun addLevels(enchant: AbstractEnchant, levels: Int) {
        if (!enchants.containsKey(enchant)) {
            enchants[enchant] = levels
        } else {
            enchants[enchant] = enchants[enchant]!! + levels
        }
    }

    fun setLevel(enchant: AbstractEnchant, level: Int) {
        enchants[enchant] = level
    }

    fun removeEnchant(enchant: AbstractEnchant) {
        enchants.remove(enchant)
    }

    fun sync(itemStack: ItemStack) {
        enchants = EnchantsManager.readEnchantsFromLore(itemStack)
    }

    fun applyIdNbt(itemStack: ItemStack): ItemStack {
        val nmsItemStack = CraftItemStack.asNMSCopy(itemStack)
        var tag: NBTTagCompound? = nmsItemStack.tag

        if (tag == null) {
            tag = NBTTagCompound()
            nmsItemStack.tag = tag
        }

        tag.setUUID("PickaxeID", uuid)

        return CraftItemStack.asBukkitCopy(nmsItemStack)
    }

}