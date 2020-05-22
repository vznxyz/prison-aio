package net.evilblock.prisonaio.module.enchant.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.mechanic.region.Region
import org.bukkit.block.Block
import org.bukkit.entity.Player

class NukeExplodeEvent(val player: Player, val block: Block, val region: Region, val level: Int) : PluginEvent()