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