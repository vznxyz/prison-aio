/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.File

object BackpackHandler : PluginHandler {

    private val ID_REGEX = "\\(ID: #([a-zA-Z0-9].*)\\)".toRegex()
    private val backpacks: MutableMap<String, Backpack> = hashMapOf()

    override fun getModule(): PluginModule {
        return MechanicsModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "backpacks.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<Backpack>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<Backpack>

                for (backpack in list) {
                    backpacks[backpack.id.toLowerCase()] = backpack
                }
            }
        }

        Tasks.asyncTimer(20L * 60L * 2, 20L * 60L * 2) {
            saveData()
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(backpacks.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun isBackpackItem(itemStack: ItemStack): Boolean {
        return itemStack.type == Material.CHEST
                && itemStack.hasItemMeta()
                && itemStack.itemMeta.hasDisplayName()
                && itemStack.itemMeta.hasLore()
                && itemStack.lore!!.size > 0
                && ID_REGEX.matches(ChatColor.stripColor(itemStack.lore!!.first()))
    }

    fun extractBackpack(itemStack: ItemStack): Backpack? {
        return getBackpack(ID_REGEX.find(ChatColor.stripColor(itemStack.lore!!.first()))!!.groupValues[1].toLowerCase())
    }

    fun getBackpack(id: String): Backpack? {
        return backpacks[id]
    }

    fun trackBackpack(backpack: Backpack) {
        backpacks[backpack.id.toLowerCase()] = backpack
    }

}