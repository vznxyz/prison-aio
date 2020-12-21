/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.salvage

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.ToolsModule
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeData
import org.bukkit.inventory.ItemStack
import java.io.File

object SalvagePreventionHandler : PluginHandler() {

    private val pickaxes = arrayListOf<ItemStack>()

    override fun getModule(): PluginModule {
        return ToolsModule
    }

    override fun hasDefaultInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "salvage-prevention.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
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

    fun getRefundableEnchants(itemStack: ItemStack, pickaxeData: PickaxeData): Map<Enchant, Int> {
        val enchants = pickaxeData.enchants.toMutableMap()
        if (enchants.isEmpty()) {
            return emptyMap()
        }

        var matchingPickaxe: ItemStack? = null
        for (pickaxe in getPickaxes()) {
            if (ItemUtils.isSimilar(pickaxe, itemStack)) {
                matchingPickaxe = pickaxe
                break
            }
        }

        if (matchingPickaxe == null) {
            return enchants
        }

        val matchingPickaxeEnchants = EnchantHandler.readEnchantsFromLore(matchingPickaxe)

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

}