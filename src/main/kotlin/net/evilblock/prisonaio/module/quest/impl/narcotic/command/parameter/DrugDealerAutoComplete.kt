package net.evilblock.prisonaio.module.quest.impl.narcotic.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.quest.QuestsModule
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

data class DrugDealerAutoComplete(private val id: String) {

    fun get(): String {
        return id
    }

    object CommandParameterType : ParameterType<DrugDealerAutoComplete> {
        override fun transform(sender: CommandSender, source: String): DrugDealerAutoComplete? {
            for (npcId in QuestsModule.getNpcIds()) {
                if (npcId.equals(source, ignoreCase = true)) {
                    return DrugDealerAutoComplete(npcId)
                }
            }

            sender.sendMessage("${ChatColor.RED}Could not find NPC by that ID in configuration.")
            return null
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            val completions = arrayListOf<String>()

            for (npcId in QuestsModule.getNpcIds()) {
                if (npcId.startsWith(source, ignoreCase = true)) {
                    completions.add(npcId)
                }
            }

            return completions
        }
    }

}