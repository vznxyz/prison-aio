/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.advertisement

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangsModule
import net.evilblock.prisonaio.module.gang.advertisement.service.GangAdvertisementExpiryService
import net.evilblock.prisonaio.service.ServiceRegistry
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object GangAdvertisementHandler : PluginHandler {

    val EXPIRE_TIME = TimeUnit.HOURS.toMillis(1L)

    private val advertisements: MutableSet<GangAdvertisement> = ConcurrentHashMap.newKeySet<GangAdvertisement>()
    private val advertisementByGang: MutableMap<UUID, GangAdvertisement> = ConcurrentHashMap()
    private val advertisementByPlayer: MutableMap<UUID, GangAdvertisement> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return GangsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "gangs-advertisements.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val posts = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<List<GangAdvertisement>>() {}.type) as List<GangAdvertisement>
                for (post in posts) {
                    trackAdvertisement(post)
                }
            }
        }

        ServiceRegistry.register(GangAdvertisementExpiryService, 20L, 20L * 15L)
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(advertisements, object : TypeToken<List<GangAdvertisement>>() {}.type), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getAdvertisements(): Set<GangAdvertisement> {
        return advertisements
    }

    fun getAdvertisementByPlayer(player: Player): GangAdvertisement? {
        return advertisementByPlayer[player.uniqueId]
    }

    fun getAdvertisementByGang(gang: Gang): GangAdvertisement? {
        return advertisementByGang[gang.uuid]
    }

    fun trackAdvertisement(advertisement: GangAdvertisement) {
        advertisements.add(advertisement)

        if (advertisement.type == GangAdvertisementType.PLAYER) {
            advertisementByPlayer[advertisement.createdBy] = advertisement
        } else {
            advertisementByGang[advertisement.createdBy] = advertisement
        }
    }

    fun forgetAdvertisement(advertisement: GangAdvertisement) {
        advertisements.remove(advertisement)

        if (advertisement.type == GangAdvertisementType.PLAYER) {
            advertisementByPlayer.remove(advertisement.createdBy)
        } else {
            advertisementByGang.remove(advertisement.createdBy)
        }
    }

}