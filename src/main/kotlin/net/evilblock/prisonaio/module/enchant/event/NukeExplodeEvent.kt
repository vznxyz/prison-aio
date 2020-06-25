/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.block.Block
import org.bukkit.entity.Player

class NukeExplodeEvent(val player: Player, val block: Block, val region: Region, val level: Int) : PluginEvent()