/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.placed

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.serialize.CrateReferenceSerializer
import org.bukkit.ChatColor
import org.bukkit.Location

class PlacedCrate(@JsonAdapter(CrateReferenceSerializer::class) val crate: Crate, val location: Location) {

    @Transient
    lateinit var hologram: HologramEntity

    init {
        initializeData()
    }

    fun initializeData() {
        hologram = HologramEntity(text = "", location = location.clone().add(0.5, 0.0, 0.5))
        hologram.initializeData()
        hologram.persistent = false
        hologram.updateLines(crate.getHologramLines())

        EntityManager.trackEntity(hologram)
    }

    fun destroy() {
        EntityManager.forgetEntity(hologram)

        hologram.destroyForCurrentWatchers()
    }

}