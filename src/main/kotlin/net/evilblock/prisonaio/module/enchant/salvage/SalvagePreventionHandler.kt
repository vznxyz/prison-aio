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
        val enchants = EnchantsManager.getEnchants(itemStack).toMutableMap()
        if (enchants.isEmpty()) {
            return emptyMap()
        }

        var matchingPickaxe: ItemStack? = null
        for (pickaxe in getPickaxes()) {
            if (pickaxe.isSimilar(itemStack)) {
                matchingPickaxe = pickaxe
                break
            }
        }

        if (matchingPickaxe == null) {
            return enchants
        }

        val matchingPickaxeEnchants = EnchantsManager.getEnchants(matchingPickaxe)

        for ((enchant, level) in enchants) {
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