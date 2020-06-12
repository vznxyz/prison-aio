package net.evilblock.prisonaio.module.shop.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.shop.Shop
import org.bukkit.entity.Player

class DetermineShopEvent(val player: Player, var shop: Shop? = null) : PluginEvent()