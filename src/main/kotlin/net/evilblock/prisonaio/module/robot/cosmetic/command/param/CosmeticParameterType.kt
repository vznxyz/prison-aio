package net.evilblock.prisonaio.module.robot.cosmetic.command.param

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.robot.cosmetic.Cosmetic
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CosmeticParameterType : ParameterType<Cosmetic> {

    override fun transform(sender: CommandSender, source: String): Cosmetic? {
        return try {
            CosmeticHandler.getRegisteredCosmetics().first { it.getUniqueId() == source.toLowerCase() }
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Could not find cosmetic by that ID. Try using tab-completion.")
            null
        }
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completed = arrayListOf<String>()
        for (cosmetic in CosmeticHandler.getRegisteredCosmetics()) {
            if (cosmetic.getUniqueId().startsWith(source.toLowerCase())) {
                completed.add(cosmetic.getUniqueId())
            }
        }
        return completed
    }

}