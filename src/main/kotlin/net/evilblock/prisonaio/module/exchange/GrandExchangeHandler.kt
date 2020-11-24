/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange

import com.google.gson.reflect.TypeToken
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.exchange.listing.GrandExchangeListing
import net.evilblock.prisonaio.module.exchange.service.AutoSaveListingsService
import net.evilblock.prisonaio.module.exchange.service.GarbageCollectionService
import net.evilblock.prisonaio.module.exchange.service.TickListingsService
import net.evilblock.prisonaio.module.storage.StorageModule
import net.evilblock.prisonaio.service.ServiceRegistry
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object GrandExchangeHandler : PluginHandler {

    private val JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()
    private val LISTING_TYPE = object : TypeToken<GrandExchangeListing>() {}.type

    val DELETION_FEE: Long = 50_000L

    private lateinit var listingsCollection: MongoCollection<Document>

    private val listings: MutableMap<UUID, GrandExchangeListing> = ConcurrentHashMap()
    private val playerListings: MutableMap<UUID, MutableList<GrandExchangeListing>> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return GrandExchangeModule
    }

    override fun initialLoad() {
        super.initialLoad()

        listingsCollection = StorageModule.database.getCollection("grand_exchange_listings")

        for (document in listingsCollection.find()) {
            try {
                val listing = deserializeListing(document)
                trackListing(listing)
            } catch (e: Exception) {
                PrisonAIO.instance.logger.severe("Failed to deserialize GE listing: ${document.getString("id")}")
                e.printStackTrace()
            }
        }

        ServiceRegistry.register(AutoSaveListingsService, 20L * 60L)
        ServiceRegistry.register(GarbageCollectionService, 20L)
        ServiceRegistry.register(TickListingsService, 20L)
    }

    fun getAllListings(): Collection<GrandExchangeListing> {
        return listings.values
    }

    fun getListingById(id: UUID): GrandExchangeListing? {
        return listings[id]
    }

    fun trackListing(listing: GrandExchangeListing) {
        listings[listing.id] = listing

        if (playerListings.containsKey(listing.createdBy)) {
            playerListings[listing.createdBy]!!.add(listing)
        } else {
            playerListings[listing.createdBy] = arrayListOf(listing)
        }
    }

    fun forgetListing(listing: GrandExchangeListing) {
        listings.remove(listing.id)

        if (playerListings.containsKey(listing.createdBy)) {
            playerListings[listing.createdBy]!!.remove(listing)
        }
    }

    fun saveListing(listing: GrandExchangeListing) {
        listingsCollection.replaceOne(Document("id", listing.id.toString()), Document.parse(Cubed.gson.toJson(listing)), ReplaceOptions().upsert(true))
    }

    fun deleteListing(listing: GrandExchangeListing) {
        listingsCollection.deleteOne(Document("id", listing.id.toString()))
    }

    fun wipeListings(): Long {
        listings.clear()

        return listingsCollection.countDocuments().also {
            listingsCollection.drop()
        }
    }

    private fun deserializeListing(document: Document): GrandExchangeListing {
        return Cubed.gson.fromJson<GrandExchangeListing>(document.toJson(JSON_WRITER_SETTINGS), LISTING_TYPE)
    }

    fun getPlayerListings(uuid: UUID): List<GrandExchangeListing> {
        return if (playerListings.containsKey(uuid)) {
            playerListings[uuid]!!
        } else {
            emptyList()
        }
    }

}