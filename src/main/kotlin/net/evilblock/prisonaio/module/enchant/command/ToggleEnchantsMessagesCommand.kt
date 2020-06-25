/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

object ToggleEnchantsMessagesCommand {

    @Command(names = ["enchant tm", "tem"], description = "Toggle enchant messages")
    @JvmStatic
    fun execute(player: Player) {
        val hasMetadata = player.hasMetadata("ENCHANT_MSGS_DISABLED")
        if (hasMetadata) {
            player.removeMetadata("ENCHANT_MSGS_DISABLED", PrisonAIO.instance)
            player.sendMessage("${EnchantsManager.CHAT_PREFIX}Enchant messages enabled.")
        } else {
            player.setMetadata("ENCHANT_MSGS_DISABLED", FixedMetadataValue(PrisonAIO.instance, true))
            player.sendMessage("${EnchantsManager.CHAT_PREFIX}Enchant messages disabled.")
        }
    }

}