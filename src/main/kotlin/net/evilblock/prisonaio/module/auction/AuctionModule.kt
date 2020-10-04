/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO

object AuctionModule : PluginModule() {

    override fun getName(): String {
        return "Auction"
    }

    override fun getConfigFileName(): String {
        return "auction"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        AuctionHandler.initialLoad()
    }

    override fun onDisable() {

    }

}