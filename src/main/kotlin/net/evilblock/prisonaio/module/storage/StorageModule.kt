/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.storage

import com.mongodb.client.MongoDatabase
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.storage.command.WipeDatabaseCommand

object StorageModule : PluginModule() {

    val database: MongoDatabase = Cubed.instance.mongo.client.getDatabase(config.getString("mongo-database"))

    override fun getName(): String {
        return "Storage"
    }

    override fun getConfigFileName(): String {
        return "storage"
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(WipeDatabaseCommand.javaClass)
    }

}