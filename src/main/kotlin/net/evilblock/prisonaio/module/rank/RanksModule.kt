package net.evilblock.prisonaio.module.rank

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.rank.command.RankEditorCommand
import net.evilblock.prisonaio.module.rank.command.parameter.RankParameterType

object RanksModule : PluginModule() {

    override fun getName(): String {
        return "Ranks"
    }

    override fun getConfigFileName(): String {
        return "ranks"
    }

    override fun onEnable() {
        RankHandler.initialLoad()
    }

    override fun onAutoSave() {
        RankHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            RankEditorCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(Rank::class.java to RankParameterType)
    }

    fun readCommands(id: String): List<String> {
        return config.getStringList("ranks.$id.commands")
    }

    fun readPermissions(id: String): List<String> {
        return config.getStringList("ranks.$id.permissions")
    }

    fun getDefaultPermissions(): List<String> {
        return config.getStringList("default-permissions")
    }

}