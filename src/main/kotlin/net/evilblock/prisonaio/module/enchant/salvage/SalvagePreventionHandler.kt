/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.salvage

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import org.bukkit.inventory.ItemStack
import java.io.File

object SalvagePreventionHandler : PluginHandler {

    private val pickaxes = arrayListOf<ItemStack>()

    override fun getModule(): PluginModule {
        return EnchantsModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "salvage-prevention.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        if (getInternalDataFile().exists()) {
            Files.newReader(getInternalDataFile(), Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<ItemStack>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<ItemStack>

                pickaxes.addAll(list)
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(pickaxes), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getPickaxes(): List<ItemStack> {
        return pickaxes.toList()
    }

    fun trackPickaxe(itemStack: ItemStack) {
        pickaxes.add(itemStack)
    }

    fun forgetPickaxe(itemStack: ItemStack) {
        pickaxes.remove(itemStack)
    }

    fun getSalvageableLevels(itemStack: ItemStack): Map<AbstractEnchant, Int> {
        val enchants = EnchantsManager.readEnchantsFromLore(itemStack).toMutableMap()
        if (enchants.isEmpty()) {
            return emptyMap()
        }

        var matchingPickaxe: ItemStack? = null
        for (pickaxe in getPickaxes()) {
            if (isSimilar(pickaxe, itemStack)) {
                matchingPickaxe = pickaxe
                break
            }
        }

        if (matchingPickaxe == null) {
            return enchants
        }

        val matchingPickaxeEnchants = EnchantsManager.readEnchantsFromLore(matchingPickaxe)

        for ((enchant, level) in enchants.toMap()) {
            if (matchingPickaxeEnchants.containsKey(enchant)) {
                val salvageableLevels = level - matchingPickaxeEnchants.getOrDefault(enchant, 0)
                if (salvageableLevels <= 0) {
                    enchants.remove(enchant)
                } else {
                    enchants[enchant] = salvageableLevels
                }
            }
        }

        return enchants
    }

    private fun isSimilar(first: ItemStack, second: ItemStack): Boolean {
        if (first.type != second.type) {
            return false
        }

        if (first.hasItemMeta() != second.hasItemMeta()) {
            return false
        }

        if (first.itemMeta.hasDisplayName() != second.itemMeta.hasDisplayName()) {
            return false
        }

        if (first.itemMeta.hasDisplayName()) {
            if (first.itemMeta.displayName != second.itemMeta.displayName) {
                return false
            }
        }

        if (first.itemMeta.hasLore() != second.itemMeta.hasLore()) {
            return false
        }

        return true
    }

}