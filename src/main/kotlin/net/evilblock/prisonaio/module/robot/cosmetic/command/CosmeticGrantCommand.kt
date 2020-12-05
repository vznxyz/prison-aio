package net.evilblock.prisonaio.module.robot.cosmetic.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.robot.cosmetic.Cosmetic
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object CosmeticGrantCommand {

    @Command(names = ["robots cosmetics grant", "robot cosmetics grant"], description = "Grant a robot cosmetic to a player", permission = "robots.cosmetics.grant", async = true)
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") uuid: UUID, @Param(name = "cosmetic") cosmetic: Cosmetic) {
        val targetName = Cubed.instance.uuidCache.name(uuid)

        if (CosmeticHandler.hasBeenGrantedCosmetic(uuid, cosmetic)) {
            sender.sendMessage("${ChatColor.RED}Player ${ChatColor.WHITE}$targetName ${ChatColor.RED}has already been granted the ${cosmetic.getName()} ${ChatColor.RED}cosmetic.")
            return
        }

        CosmeticHandler.grantCosmetic(uuid, cosmetic)

        sender.sendMessage("${ChatColor.GREEN}You granted ${ChatColor.WHITE}$targetName ${ChatColor.GREEN}the ${cosmetic.getName()} ${ChatColor.GREEN}cosmetic.")

        val targetPlayer = Bukkit.getPlayer(uuid)
        if (targetPlayer != null) {
            targetPlayer.sendMessage("")
            targetPlayer.sendMessage(" ${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Robot Cosmetic Unlocked!")
            targetPlayer.sendMessage(" ${ChatColor.GRAY}You have unlocked the ${cosmetic.getName()} ${ChatColor.GRAY}for your robots.")
            targetPlayer.sendMessage("")
        }
    }

}