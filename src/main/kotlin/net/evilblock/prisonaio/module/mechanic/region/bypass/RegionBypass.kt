package net.evilblock.prisonaio.module.mechanic.region.bypass

import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object RegionBypass : Listener {

    private const val METADATA_KEY = "REGION_BYPASS"
    private val noticeSent = hashSetOf<UUID>()

    @JvmStatic
    fun hasBypass(player: Player): Boolean {
        return player.hasMetadata(METADATA_KEY)
    }

    @JvmStatic
    fun setBypass(player: Player, bypass: Boolean) {
        noticeSent.remove(player.uniqueId)

        if (bypass) {
            player.setMetadata(METADATA_KEY, FixedMetadataValue(PrisonAIO.instance, true))
        } else {
            player.removeMetadata(METADATA_KEY, PrisonAIO.instance)
        }
    }

    @JvmStatic
    fun hasReceivedNotification(player: Player): Boolean {
        return noticeSent.contains(player.uniqueId)
    }

    @JvmStatic
    fun sendNotification(player: Player) {
        noticeSent.add(player.uniqueId)
        player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}WARNING: ${ChatColor.GRAY}Region bypass is currently enabled.")
    }

    /**
     * Removes the player from the [noticeSent] map when the player logs out.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        setBypass(event.player, false)
    }

}