/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.pickaxe

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.Reflection
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

object PickaxeHandler : PluginHandler {

    private val pickaxes: MutableMap<UUID, PickaxeData> = hashMapOf()

    override fun getModule(): PluginModule {
        return EnchantsModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "pickaxes.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val dataType = object : TypeToken<List<PickaxeData>>() {}.type
                val data = Cubed.gson.fromJson(reader, dataType) as List<PickaxeData>

                for (pickaxe in data) {
                    trackPickaxeData(pickaxe)
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(pickaxes.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun trackPickaxeData(data: PickaxeData) {
        pickaxes[data.uuid] = data
    }

    fun forgetPickaxeData(data: PickaxeData) {
        pickaxes.remove(data.uuid)
    }

    fun getPickaxeData(itemStack: ItemStack?): PickaxeData? {
        if (itemStack == null) {
            return null
        }

        val uuid = readPickaxeId(itemStack) ?: return null
        return pickaxes[uuid]
    }

    private fun readPickaxeId(itemStack: ItemStack): UUID? {
        val handle = CraftItemStack.asNMSCopy(itemStack)
        if (handle.hasTag() && handle.tag!!.hasKey("PickaxeIDMost")) {
            return handle.tag!!.getUUID("PickaxeID")
        }
        return null
    }

}