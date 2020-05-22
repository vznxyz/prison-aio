package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import java.text.NumberFormat

class TokenBalanceButton : Button() {

    override fun getName(player: Player): String {
        return ChatColor.GRAY.toString() + "» " + ChatColor.GOLD + ChatColor.BOLD + "Token Balance" + ChatColor.GRAY + " «"
    }

    override fun getDescription(player: Player): List<String> {
        val user = UserHandler.getUser(player.uniqueId)
        return listOf(ChatColor.GRAY.toString() + "Tokens: " + ChatColor.GOLD + NumberFormat.getInstance().format(user.getTokensBalance()))
    }

    override fun getMaterial(player: Player): Material {
        return Material.PAPER
    }

}