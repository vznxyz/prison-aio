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