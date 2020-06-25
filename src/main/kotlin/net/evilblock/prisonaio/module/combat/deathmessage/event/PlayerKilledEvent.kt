package net.evilblock.prisonaio.module.combat.deathmessage.event

import net.evilblock.prisonaio.module.PluginEvent
import org.bukkit.entity.Player

class PlayerKilledEvent(val killer: Player, val victim: Player) : PluginEvent()
