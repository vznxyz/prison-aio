/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.command

import me.devemilio.gkitz.kits.Kit
import me.devemilio.gkitz.profile.Profile
import net.evilblock.cubed.command.Command
import org.bukkit.entity.Player

object GKitzCommand {

    @Command(
        names = ["gkitz", "gkit", "gkits", "gk"],
        description = "View the Buycraft store"
    )
    @JvmStatic
    fun execute(player: Player) {
        val profile = Profile.getByUuidIfAvailable(player.uniqueId)
        if (profile != null) {
            Kit.buildInventory(profile)
        }
    }

}