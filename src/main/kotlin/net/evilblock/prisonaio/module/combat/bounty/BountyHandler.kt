/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.bounty

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.combat.CombatModule
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BountyHandler : PluginHandler() {

    private val bounties: MutableSet<Bounty> = ConcurrentHashMap.newKeySet()
    private val bountiesByTarget: MutableMap<UUID, MutableSet<Bounty>> = ConcurrentHashMap()
    private val bountiesByCreator: MutableMap<UUID, MutableSet<Bounty>> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return CombatModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "bounties.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val data = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<Set<Bounty>>() {}.type) as Set<Bounty>
                for (bounty in data) {
                    trackBounty(bounty)
                }
            }
        }

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(bounties, object : TypeToken<Set<Bounty>>() {}.type), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getBounties(): Set<Bounty> {
        return bounties
    }

    fun trackBounty(bounty: Bounty) {
        bounties.add(bounty)

        bountiesByTarget.putIfAbsent(bounty.target, ConcurrentHashMap.newKeySet())
        bountiesByTarget[bounty.target]!!.add(bounty)

        bountiesByCreator.putIfAbsent(bounty.createdBy, ConcurrentHashMap.newKeySet())
        bountiesByCreator[bounty.createdBy]!!.add(bounty)
    }

    fun forgetBounty(bounty: Bounty) {
        bounties.remove(bounty)

        if (bountiesByTarget.containsKey(bounty.target)) {
            bountiesByTarget[bounty.target]!!.remove(bounty)
        }

        if (bountiesByCreator.containsKey(bounty.createdBy)) {
            bountiesByCreator[bounty.createdBy]!!.remove(bounty)
        }
    }

}