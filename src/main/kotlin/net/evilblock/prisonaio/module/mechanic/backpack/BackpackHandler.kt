/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.nms.NBTUtil
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.BackpackUpgrade
import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.impl.CapacityUpgrade
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object BackpackHandler : PluginHandler {

    @JvmStatic
    val CHAT_PREFIX = "${ChatColor.GRAY}[${ChatColor.GREEN}${ChatColor.BOLD}Backpack${ChatColor.GRAY}] "

    private val backpacks: ConcurrentHashMap<String, Backpack> = ConcurrentHashMap()
    private val upgrades: MutableMap<String, BackpackUpgrade> = hashMapOf()

    override fun getModule(): PluginModule {
        return MechanicsModule
    }

    override fun hasDefaultInternalData(): Boolean {
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

        upgrades[CapacityUpgrade.getId().toLowerCase()] = CapacityUpgrade
    }

    override fun saveData() {
        super.saveData()

        safeSave()
    }

    fun safeSave() {
        val jsonArray = JsonArray()

        var successful = 0
        var failed = 0

        for (backpack in getBackpacks()) {
            try {
                jsonArray.add(Cubed.gson.toJsonTree(backpack, Backpack::class.java))
                successful++
            } catch (e: Exception) {
                e.printStackTrace()
                failed++
            }
        }

        Files.write(jsonArray.toString(), getInternalDataFile(), Charsets.UTF_8)

        if (failed != 0) {
            PrisonAIO.instance.systemLog("${ChatColor.GREEN}Safe save of backpack data complete! (${NumberUtils.format(successful)} successful, ${NumberUtils.format(failed)} failed)")
        }
    }

    fun isBackpackItem(itemStack: ItemStack): Boolean {
        return itemStack.type == Material.CHEST
                && itemStack.hasItemMeta()
                && itemStack.itemMeta.hasDisplayName()
                && itemStack.itemMeta.hasLore()
                && ItemUtils.itemTagHasKey(itemStack, "BackpackID")
    }

    fun extractBackpack(itemStack: ItemStack): Backpack? {
        val nmsCopy = ItemUtils.getNmsCopy(itemStack)
        val tag = NBTUtil.getOrCreateTag(nmsCopy)
        return getBackpack(NBTUtil.getString(tag, "BackpackID"))
    }

    fun getBackpacks(): Collection<Backpack> {
        return backpacks.values
    }

    fun getBackpack(id: String): Backpack? {
        return backpacks[id]
    }

    fun trackBackpack(backpack: Backpack) {
        backpacks[backpack.id.toLowerCase()] = backpack
    }

    fun findBackpacksInInventory(player: Player): Map<ItemStack, Backpack> {
        val found = hashMapOf<ItemStack, Backpack>()
        for (item in player.inventory.storageContents) {
            if (item != null && isBackpackItem(item)) {
                val backpack = extractBackpack(item)
                if (backpack != null) {
                    found[item] = backpack
                }
            }
        }
        return found
    }

    fun wipeBackpacks() {
        backpacks.clear()
    }

    fun getUpgrades(): Collection<BackpackUpgrade> {
        return upgrades.values
    }

    fun getUpgradeById(id: String): BackpackUpgrade? {
        return upgrades[id.toLowerCase()]
    }

}