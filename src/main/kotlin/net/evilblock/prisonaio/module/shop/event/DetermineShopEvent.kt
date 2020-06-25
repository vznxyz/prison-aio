/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.shop.Shop
import org.bukkit.entity.Player

class DetermineShopEvent(val player: Player, var shop: Shop? = null) : PluginEvent()