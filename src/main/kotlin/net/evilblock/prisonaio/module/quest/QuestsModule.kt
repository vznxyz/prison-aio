/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.quest.command.QuestGuideCommand
import net.evilblock.prisonaio.module.quest.dialogue.command.QuestDialogueSkipCommand
import net.evilblock.prisonaio.module.quest.dialogue.listener.DialogueChatListeners
import net.evilblock.prisonaio.module.quest.dialogue.listener.DialogueSequenceListeners
import net.evilblock.prisonaio.module.quest.impl.narcotic.Narcotic
import net.evilblock.prisonaio.module.quest.impl.narcotic.command.GiveNarcoticsCommand
import net.evilblock.prisonaio.module.quest.impl.narcotic.command.SpawnDealerCommand
import net.evilblock.prisonaio.module.quest.impl.narcotic.command.SpawnPabloEscobarCommand
import net.evilblock.prisonaio.module.quest.impl.narcotic.command.SpawnLexLuthorCommand
import net.evilblock.prisonaio.module.quest.impl.narcotic.command.parameter.DrugDealerAutoComplete
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object QuestsModule : PluginModule() {

    override fun getName(): String {
        return "Quests"
    }

    override fun getConfigFileName(): String {
        return "quests"
    }

    override fun onEnable() {
        QuestHandler.initialLoad()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            GiveNarcoticsCommand.javaClass,
            SpawnDealerCommand.javaClass,
            SpawnLexLuthorCommand.javaClass,
            SpawnPabloEscobarCommand.javaClass,
            QuestDialogueSkipCommand.javaClass,
            QuestGuideCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            DrugDealerAutoComplete::class.java to DrugDealerAutoComplete.CommandParameterType,
            Narcotic::class.java to Narcotic.CommandParameterType
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(DialogueChatListeners, DialogueSequenceListeners)
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