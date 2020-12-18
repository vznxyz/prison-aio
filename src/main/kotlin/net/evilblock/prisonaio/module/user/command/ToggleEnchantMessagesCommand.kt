/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.ToggleEnchantMessagesMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ToggleEnchantMessagesCommand {

    @Command(
        names = ["toggle-enchant-messages", "tem", "enchant-msgs", "enchant-messages"],
        description = "Opens the Toggle Enchant Messages GUI"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't open menus while your combat timer is active!")
            return
        }

        ToggleEnchantMessagesMenu(UserHandler.getUser(player)).openMenu(player)
    }

}