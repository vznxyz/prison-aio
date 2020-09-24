/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.quest.command.QuestGuideCommand
import net.evilblock.prisonaio.module.quest.dialogue.command.QuestDialogueSkipCommand
import net.evilblock.prisonaio.module.quest.dialogue.listener.DialogueChatListeners
import net.evilblock.prisonaio.module.quest.dialogue.listener.DialogueSequenceListeners
import net.evilblock.prisonaio.module.quest.listener.QuestListeners
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object QuestsModule : PluginModule() {

    override fun getName(): String {
        return "Quests"
    }

    override fun getConfigFileName(): String {
        return "quests"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        QuestHandler.initialLoad()
    }

    override fun onDisable() {
        QuestHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        val list = arrayListOf<Class<*>>(QuestDialogueSkipCommand.javaClass, QuestGuideCommand.javaClass)

        for (quest in QuestHandler.getQuests()) {
            list.addAll(quest.getCommands())
        }

        return list
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        val map = hashMapOf<Class<*>, ParameterType<*>>()

        for (quest in QuestHandler.getQuests()) {
            map.putAll(quest.getCommandParameterTypes())
        }

        return map
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            QuestListeners,
            DialogueChatListeners,
            DialogueSequenceListeners
        )
    }

    fun getNpcName(npc: String): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("npc.$npc.name"))
    }

    fun getNpcTextureValue(npc: String): String {
        return config.getString("npc.$npc.texture-value")
    }

    fun getNpcTextureSignature(npc: String): String {
        return config.getString("npc.$npc.texture-signature")
    }

    fun getNpcIds(): List<String> {
        return config.getConfigurationSection("npc").getKeys(false).toList()
    }

}