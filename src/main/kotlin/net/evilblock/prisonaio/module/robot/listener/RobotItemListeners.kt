package net.evilblock.prisonaio.module.robot.listener

import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.entity.npc.util.NpcUtils
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.robot.mechanic.RobotMechanic
import net.evilblock.prisonaio.module.robot.mechanic.RobotMechanicHandler
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotUtils
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector

/**
 * Handles anything to do with the robot item.
 */
object RobotItemListeners : Listener {

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player

        val itemInHand = if (MinecraftReflection.getServerVersion() > 5) {
            player.inventory.itemInMainHand
        } else {
            player.inventory.itemInHand
        }

        if (event.action == Action.RIGHT_CLICK_BLOCK && itemInHand != null) {
            if (RobotUtils.isRobotItem(itemInHand)) {
                event.isCancelled = true

                if (event.blockFace != BlockFace.UP) {
                    return
                }

                if (player.gameMode == GameMode.CREATIVE && RegionBypass.hasBypass(player)) {
                    RegionBypass.attemptNotify(player)
                } else {
                    if (!RobotHandler.isPrivileged(player, event.clickedBlock.location)) {
                        return
                    }

                    // check if placing on plot if player doesn't have bypass
                    val plot = PlotUtil.getPlot(event.clickedBlock.location)
                    if (plot == null) {
                        player.sendMessage("${ChatColor.RED}Try placing that on a plot you have access to.")
                        return
                    }

                    if (RobotHandler.getRobotsByPlot(plot.id).size >= RobotsModule.getMaxRobotsPerPlot()) {
                        player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}This plot has reached the limit of ${ChatColor.BOLD}${RobotsModule.getMaxRobotsPerPlot()} ${ChatColor.RED}robots per plot!")
                        return
                    }
                }

                var yaw: Float = player.location.yaw
                if (yaw < 0.0f) {
                    yaw += 360.0f
                }

                val direction: BlockFace = when {
                    yaw >= 315.0f || yaw < 45.0f -> {
                        BlockFace.NORTH
                    }
                    yaw < 135.0f -> {
                        BlockFace.EAST
                    }
                    yaw < 225.0f -> {
                        BlockFace.SOUTH
                    }
                    else -> {
                        BlockFace.WEST
                    }
                }

                val robotLocation = event.clickedBlock.location.setDirection(Vector(direction.modX, direction.modY, direction.modZ)).add(0.5, 1.0, 0.5)
                if (robotLocation.block.type != Material.AIR) {
                    player.sendMessage("${ChatColor.RED}You can't place a robot there!")
                    return
                }

                val relativeBlock: Block = robotLocation.block.getRelative(direction)
                if (relativeBlock.type != Material.AIR) {
                    player.sendMessage("${ChatColor.RED}You can't place a robot there!")
                    return
                }

                val tier = RobotUtils.getRobotItemTier(itemInHand).coerceAtLeast(0)
                RobotsModule.getPluginFramework().logger.info("${player.name} is placing a Tier $tier Robot at ${robotLocation.world.name}, ${robotLocation.x}, ${robotLocation.y}, ${robotLocation.z}")

                if (itemInHand.amount == 1) {
                    if (MinecraftReflection.getServerVersion() > 5) {
                        event.player.inventory.itemInMainHand = null
                    } else {
                        event.player.inventory.itemInHand = null
                    }
                } else {
                    itemInHand.amount = itemInHand.amount - 1
                }

                player.updateInventory()

                Tasks.async {
                    val robot = MinerRobot(owner = player.uniqueId, location = robotLocation)
                    robot.tier = tier
                    robot.initializeData()
                    robot.spawn(player)
                    robot.setupFakeBlock(true)

                    RobotHandler.trackRobot(robot)
                }
            } else if (RobotUtils.isMechanicEggItem(itemInHand)) {
                val plot = PlotUtil.getPlot(event.clickedBlock.location)

                if (player.gameMode == GameMode.CREATIVE && RegionBypass.hasBypass(player)) {
                    RegionBypass.attemptNotify(player)
                } else {
                    if (!RobotHandler.isPrivileged(player, event.clickedBlock.location)) {
                        return
                    }

                    // check if placing on plot if player doesn't have bypass
                    if (plot == null) {
                        player.sendMessage("${ChatColor.RED}You must place the egg on a plot you have access to!")
                        return
                    }

                    if (RobotMechanicHandler.isPlotTracked(plot.id)) {
                        player.sendMessage("${ChatColor.RED}There can only be one Robot Mechanic on a plot!")
                        return
                    }
                }

                if (!NpcUtils.isEmpty(event.clickedBlock.getRelative(BlockFace.UP), 2)) {
                    player.sendMessage("${ChatColor.RED}You can't place a Robot Mechanic there!")
                    return
                }

                if (itemInHand.amount == 1) {
                    if (MinecraftReflection.getServerVersion() > 5) {
                        event.player.inventory.itemInMainHand = null
                    } else {
                        event.player.inventory.itemInHand = null
                    }
                } else {
                    itemInHand.amount = itemInHand.amount - 1
                }

                event.player.updateInventory()
                event.player.sendMessage("${ChatColor.GREEN}You placed your egg and a Robot Mechanic appeared!")

                if (plot != null) {
                    RobotMechanicHandler.trackPlot(plot.id)
                }

                Tasks.async {
                    // npc direction == flipped player yaw
                    val direction = event.player.eyeLocation.direction.multiply(-1)
                    val height = NpcUtils.BLOCK_HEIGHTS.getOrDefault(event.clickedBlock.type, 1.0)

                    val updatedLocation = event.clickedBlock.location.add(0.5, height, 0.5)
                    updatedLocation.direction = direction

                    val directionFace = AngleUtils.yawToFace(updatedLocation.yaw)
                    updatedLocation.yaw = AngleUtils.faceToYaw(directionFace).toFloat()
                    updatedLocation.pitch = 0.0F

                    val mechanic = RobotMechanic(location = updatedLocation)
                    mechanic.initializeData()
                    mechanic.spawn(player)

                    EntityManager.trackEntity(mechanic)
                }
            }
        }
    }

}