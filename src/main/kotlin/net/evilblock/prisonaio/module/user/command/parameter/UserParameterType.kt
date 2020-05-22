package net.evilblock.prisonaio.module.user.command.parameter

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object UserParameterType : ParameterType<User?> {

    override fun transform(sender: CommandSender, source: String): User? {
        if (source == "self") {
            if (sender is Player) {
                return UserHandler.getUser(sender.uniqueId)
            } else {
                throw IllegalStateException("Can't transform sender to User")
            }
        }

        val uuid: UUID? = try {
            UUID.fromString(source)
        } catch (e: Exception) {
            Cubed.instance.uuidCache.uuid(source)
        }

        if (uuid == null) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a player by the name or ID '$source'.")
            return null
        }

        return try {
            UserHandler.fetchUser(uuid, true)
        } catch (e: IllegalStateException) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a player by the name or ID '$source'.")
            null
        }
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completions = arrayListOf<String>()

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.name.toLowerCase().startsWith(source.toLowerCase())) {
                completions.add(onlinePlayer.name)
            }
        }

        return completions
    }

}