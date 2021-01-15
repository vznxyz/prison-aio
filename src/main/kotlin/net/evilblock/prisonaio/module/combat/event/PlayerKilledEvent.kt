/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.event

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player

class PlayerKilledEvent(val victim: Player, val killer: Player) : PluginEvent()