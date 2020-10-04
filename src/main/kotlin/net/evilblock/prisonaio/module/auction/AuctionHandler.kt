/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction

import com.google.gson.reflect.TypeToken
import com.mongodb.client.MongoCollection
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.auction.listing.AuctionListing
import net.evilblock.prisonaio.module.storage.StorageModule
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AuctionHandler : PluginHandler {

    private val JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()
    private val LISTING_TYPE = object : TypeToken<AuctionListing>() {}.type

    private lateinit var mongoCollection: MongoCollection<Document>

    private val listings: MutableMap<UUID, AuctionListing> = ConcurrentHashMap()
    private val playerListings: MutableMap<UUID, List<AuctionListing>> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return AuctionModule
    }

    override fun initialLoad() {
        super.initialLoad()

        mongoCollection = StorageModule.database.getCollection("auction_listings")

        for (document in mongoCollection.find()) {
            val listing = Cubed.gson.fromJson<AuctionListing>(document.toJson(JSON_WRITER_SETTINGS), LISTING_TYPE)
        }
    }

    fun trackListing(listing: AuctionListing) {

    }

    fun forgetListing(listing: AuctionListing) {

    }

}