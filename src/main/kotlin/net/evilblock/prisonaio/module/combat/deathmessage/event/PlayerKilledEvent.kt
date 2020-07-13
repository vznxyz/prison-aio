package net.evilblock.prisonaio.module.combat.deathmessage.event

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player

class PlayerKilledEvent(val killer: Player, val victim: Player) : PluginEvent()
