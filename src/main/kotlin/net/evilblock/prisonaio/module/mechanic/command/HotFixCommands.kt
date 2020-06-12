package net.evilblock.prisonaio.module.mechanic.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

object HotFixCommands {

    @Command(
        names = ["remove-armor-stand-radius"],
        permission = "op"
    )
    @JvmStatic
    fun removeArmorStands(player: Player, @Param(name = "radius") radius: Int) {
        for (entity in player.location.world.entities) {
            if (entity.location.distanceSquared(player.location) <= radius) {
                if (entity.type == EntityType.ARMOR_STAND) {
                    entity.remove()
                }
            }
        }
    }

    @Command(
        names = ["remove-villagers-radius"],
        permission = "op"
    )
    @JvmStatic
    fun removeVillagers(player: Player, @Param(name = "radius") radius: Int) {
        for (entity in player.location.world.entities) {
            if (entity.location.distanceSquared(player.location) <= radius) {
                if (entity.type == EntityType.VILLAGER) {
                    entity.remove()
                }
            }
        }
    }

}