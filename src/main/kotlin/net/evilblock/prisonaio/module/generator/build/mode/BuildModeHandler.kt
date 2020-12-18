/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.build.mode

import net.evilblock.cubed.util.bukkit.EventUtils
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.generator.schematic.rotate.RotateUtil
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.*
import org.bukkit.event.server.PluginDisableEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BuildModeHandler : Listener {

    private val tracked: MutableMap<UUID, BuildMode> = ConcurrentHashMap()

    @JvmStatic
    fun isInMode(player: Player): Boolean {
        return tracked.containsKey(player.uniqueId)
    }

    @JvmStatic
    fun getModeData(player: Player): BuildMode {
        return tracked[player.uniqueId]!!
    }

    @JvmStatic
    fun startTracking(player: Player, buildMode: BuildMode) {
        tracked[player.uniqueId] = buildMode
    }

    @JvmStatic
    fun stopTracking(player: Player) {
        tracked.remove(player.uniqueId)?.stop()
    }

    @EventHandler
    fun onPluginDisableEvent(event: PluginDisableEvent) {
        if (event.plugin != PrisonAIO.instance) {
            return
        }

        for (mode in tracked.values) {
            mode.stop()
        }

        tracked.clear()
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        if (isInMode(event.player)) {
            stopTracking(event.player)
        }
    }

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (isInMode(event.player)) {
            val modeData = getModeData(event.player)
            if (modeData.previewing) {
                return
            }

            if (EventUtils.hasPlayerMoved(event)) {
                val fromPlot = PlotUtil.getPlot(event.from)
                val toPlot = PlotUtil.getPlot(event.to)

                if (fromPlot == null || toPlot == null || fromPlot.id != toPlot.id) {
                    stopTracking(event.player)
                    event.player.sendMessage("${ChatColor.RED}You moved out of the plot, so Build Mode has been de-activated!")
                } else {
                    modeData.updateLocation(event.to)
                }
            }

            val newRotation = RotateUtil.getFacing(event.player).getOpposite()
            if (newRotation != modeData.rotation) {
                modeData.updateRotation(newRotation)
            }
        }
    }

    @EventHandler
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        if (isInMode(event.player)) {
            val modeData = getModeData(event.player)
            if (modeData.previewing && event.to == modeData.previewLocation) {
                return
            }

            val fromPlot = PlotUtil.getPlot(event.from)
            val toPlot = PlotUtil.getPlot(event.to)

            if (fromPlot == null || toPlot == null || fromPlot.id != toPlot.id) {
                stopTracking(event.player)
                event.player.sendMessage("${ChatColor.RED}You teleported out of the plot, so Build Mode has been de-activated!")
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        if (isInMode(event.player)) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You can't use commands while in Build Mode!")
        }
    }

    @EventHandler
    fun onItemRightClick(event: PlayerInteractEvent) {
        if (isInMode(event.player)) {
            event.isCancelled = true

            if (event.action != Action.RIGHT_CLICK_BLOCK && event.action != Action.RIGHT_CLICK_AIR) {
                return
            }

            if (event.item == null) {
                return
            }

            val modeData = getModeData(event.player)

            when (event.item) {
                BuildModeItems.CONFIRM -> {
                    if (!modeData.canBuild(event.player, modeData.getBounds(), true)) {
                        return
                    }

                    val cost = modeData.getCost()

                    val user = UserHandler.getUser(event.player)
                    if (!user.hasTokenBalance(cost)) {
                        event.player.sendMessage("${ChatColor.RED}You can't afford to purchase a ${modeData.type.getProperName()}!")
                        return
                    }

                    user.subtractTokensBalance(cost)
                    stopTracking(event.player)

                    val generator = modeData.type.createInstance(modeData.plot, event.player.uniqueId, modeData.getBounds(), modeData.rotation)
                    generator.level = modeData.level.number
                    generator.initializeData()
                    generator.startBuild()

                    GeneratorHandler.trackGenerator(generator)
                }
                BuildModeItems.PREVIEW -> {
                    if (!modeData.canBuild(event.player, modeData.getBounds(), true)) {
                        return
                    }

                    modeData.togglePreview()
                }
                BuildModeItems.EXIT -> {
                    stopTracking(event.player)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (isInMode(event.whoClicked as Player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryDragEvent(event: InventoryDragEvent) {
        if (isInMode(event.whoClicked as Player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (isInMode(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerAttemptPickupItemEvent(event: PlayerAttemptPickupItemEvent) {
        if (isInMode(event.player)) {
            event.isCancelled = true
        }
    }

}