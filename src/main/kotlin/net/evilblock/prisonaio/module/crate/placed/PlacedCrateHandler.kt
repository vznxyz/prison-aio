/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.placed

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.crate.CratesModule
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.io.File

object PlacedCrateHandler : PluginHandler {

    private const val SELECTION_METADATA_KEY = "CRATE_SELECTION"

    private val placedCrates = hashMapOf<Location, PlacedCrate>()

    override fun getModule(): PluginModule {
        return CratesModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "placed-crates.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        if (getInternalDataFile().exists()) {
            Files.newReader(getInternalDataFile(), Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<PlacedCrate>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<PlacedCrate>

                for (placedCrate in list) {
                    placedCrate.initializeData()
                    placedCrates[placedCrate.location] = placedCrate
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(placedCrates.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getPlacedCrates(): List<PlacedCrate> {
        return placedCrates.values.toList()
    }

    fun isAttachedToCrate(block: Block): Boolean {
        return placedCrates.containsKey(block.location)
    }

    fun getPlacedCrate(block: Block): PlacedCrate {
        if (!isAttachedToCrate(block)) {
            throw IllegalStateException("Block is not linked to a crate")
        }
        return placedCrates[block.location]!!
    }

    fun trackPlacedCrate(placedCrate: PlacedCrate) {
        placedCrates[placedCrate.location] = placedCrate
    }

    fun forgetPlacedCrate(placedCrate: PlacedCrate) {
        placedCrates.remove(placedCrate.location)
    }

    fun hasSelectionHandlerAttached(player: Player): Boolean {
        return player.hasMetadata(SELECTION_METADATA_KEY)
    }

    fun attachSelectionHandler(player: Player, lambda: (Block) -> Unit) {
        player.setMetadata(SELECTION_METADATA_KEY, FixedMetadataValue(Cubed.instance, lambda))
    }

    fun handleSelection(player: Player, block: Block) {
        if (!hasSelectionHandlerAttached(player)) {
            throw IllegalStateException("Player does not have selection handler attached")
        }

        val callbackFunction = player.getMetadata(SELECTION_METADATA_KEY)[0].value() as (Block) -> Unit
        player.removeMetadata(SELECTION_METADATA_KEY, Cubed.instance)
        callbackFunction.invoke(block)
    }

    fun isChestType(material: Material): Boolean {
        return material.name.endsWith("CHEST")
    }

}