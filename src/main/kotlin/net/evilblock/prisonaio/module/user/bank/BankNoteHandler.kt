/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.bank

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.logging.LogFile
import net.evilblock.cubed.logging.LogHandler
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.util.bukkit.HiddenLore
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.user.UsersModule
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BankNoteHandler : PluginHandler() {

    private val bankNotes: ConcurrentHashMap<UUID, BankNote> = ConcurrentHashMap()

    val logFile: LogFile = LogFile(File(File(getModule().getPluginFramework().dataFolder, "logs"), "bank-notes.txt"))

    override fun getModule(): PluginModule {
        return UsersModule
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "bank-notes.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<BankNote>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<BankNote>

                for (bankNote in list) {
                    trackBankNote(bankNote)
                }
            }
        }

        LogHandler.trackLogFile(logFile)

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(bankNotes.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun findBankNote(uuid: UUID): BankNote? {
        return bankNotes[uuid]
    }

    fun trackBankNote(bankNote: BankNote) {
        bankNotes[bankNote.uuid] = bankNote
    }

    fun extractId(itemStack: ItemStack): UUID {
        return UUID.fromString(HiddenLore.extractHiddenString(itemStack.itemMeta.lore.first()))
    }

    fun isBankNoteItemStack(itemStack: ItemStack?): Boolean {
        if (itemStack == null || itemStack.type != Material.PAPER) {
            return false
        }

        if (!itemStack.hasItemMeta() || !itemStack.itemMeta.hasDisplayName() || !itemStack.itemMeta.hasLore()) {
            return false
        }

        if (!HiddenLore.hasHiddenString(itemStack.itemMeta.lore.first())) {
            return false
        }

        val extractedId = UUID.fromString(HiddenLore.extractHiddenString(itemStack.itemMeta.lore.first()))
        return findBankNote(extractedId) != null
    }

}