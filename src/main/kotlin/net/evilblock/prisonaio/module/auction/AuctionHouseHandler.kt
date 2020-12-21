/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction

import com.google.gson.reflect.TypeToken
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.auction.listing.Listing
import net.evilblock.prisonaio.module.auction.service.AutoSaveListingsService
import net.evilblock.prisonaio.module.auction.service.GarbageCollectionService
import net.evilblock.prisonaio.module.auction.service.TickListingsService
import net.evilblock.prisonaio.service.ServiceRegistry
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AuctionHouseHandler : PluginHandler() {

    private val JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()
    private val LISTING_TYPE = object : TypeToken<Listing>() {}.type

    val DELETION_FEE: Long = 50_000L

    private lateinit var listingsCollection: MongoCollection<Document>

    private val listings: MutableMap<UUID, Listing> = ConcurrentHashMap()
    private val playerListings: MutableMap<UUID, MutableList<Listing>> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return AuctionHouseModule
    }

    override fun initialLoad() {
        super.initialLoad()

        listingsCollection = PrisonAIO.instance.database.getCollection("auction_house_listings")

        for (document in listingsCollection.find()) {
            try {
                val listing = deserializeListing(document)
                trackListing(listing)
            } catch (e: Exception) {
                PrisonAIO.instance.logger.severe("Failed to deserialize AH listing: ${document.getString("id")}")
                e.printStackTrace()
            }
        }

        ServiceRegistry.register(AutoSaveListingsService, 20L * 60L)
        ServiceRegistry.register(GarbageCollectionService, 20L)
        ServiceRegistry.register(TickListingsService, 20L)
    }

    fun getAllListings(): Collection<Listing> {
        return listings.values
    }

    fun getListingById(id: UUID): Listing? {
        return listings[id]
    }

    fun trackListing(listing: Listing) {
        listings[listing.id] = listing

        if (playerListings.containsKey(listing.createdBy)) {
            playerListings[listing.createdBy]!!.add(listing)
        } else {
            playerListings[listing.createdBy] = arrayListOf(listing)
        }
    }

    fun forgetListing(listing: Listing) {
        listings.remove(listing.id)

        if (playerListings.containsKey(listing.createdBy)) {
            playerListings[listing.createdBy]!!.remove(listing)
        }
    }

    fun saveListing(listing: Listing) {
        listingsCollection.replaceOne(Document("id", listing.id.toString()), Document.parse(Cubed.gson.toJson(listing)), ReplaceOptions().upsert(true))
    }

    fun deleteListing(listing: Listing) {
        listingsCollection.deleteOne(Document("id", listing.id.toString()))
    }

    fun wipeListings(): Long {
        listings.clear()

        return listingsCollection.countDocuments().also {
            listingsCollection.drop()
        }
    }

    private fun deserializeListing(document: Document): Listing {
        return Cubed.gson.fromJson<Listing>(document.toJson(JSON_WRITER_SETTINGS), LISTING_TYPE)
    }

    fun getPlayerListings(uuid: UUID): List<Listing> {
        return if (playerListings.containsKey(uuid)) {
            playerListings[uuid]!!
        } else {
            emptyList()
        }
    }

}