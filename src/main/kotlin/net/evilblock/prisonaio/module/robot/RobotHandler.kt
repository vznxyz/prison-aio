package net.evilblock.prisonaio.module.robot

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import com.intellectualcrafters.plot.`object`.PlotId
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import net.evilblock.prisonaio.module.robot.tick.RobotThread
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object RobotHandler : PluginHandler() {

    private val dataType = object : TypeToken<List<Robot>>() {}.type
    private val backupsFolder = File(getModule().getPluginFramework().dataFolder, "backups")

    private val robotsByUuid: MutableMap<UUID, Robot> = ConcurrentHashMap()
    private val robotsByPlot: MutableMap<PlotId, MutableList<Robot>> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return RobotsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "robots.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            backupsFolder.mkdir()
            Files.copy(dataFile, findNextBackupFile())

            try {
                Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                    val robots = Cubed.gson.fromJson(reader, dataType) as List<Robot>

                    for (robot in robots) {
                        robot.initializeData()
                        trackRobot(robot)
                    }

                    for (robot in robots) {
                        if (robot is MinerRobot) {
                            robot.setupFakeBlock(false)
                        }
                    }

                    getModule().getPluginFramework().logger.info("Loaded ${robots.size} robots from robots.json!")
                }
            } catch (e: Exception) {
                getModule().getPluginFramework().logger.severe("Failed to parse robots.json!")
                e.printStackTrace()
            }
        }

        RobotThread.start()
    }

    override fun saveData() {
        try {
            java.nio.file.Files.write(getInternalDataFile().toPath(), Cubed.gson.toJson(robotsByUuid.values, dataType).toByteArray())
        } catch (e: Exception) {
            getModule().getPluginFramework().systemLog("${ChatColor.RED}Failed to save robots to robots.json!")
            e.printStackTrace()
        }
    }

    /**
     * Tracks the given [robot].
     */
    fun trackRobot(robot: Robot) {
        robotsByUuid[robot.uuid] = robot
        EntityManager.trackEntity(robot)

        val plot = PlotUtil.getPlot(robot.location)
        if (plot != null) {
            robotsByPlot.putIfAbsent(plot.id, arrayListOf())
            robotsByPlot[plot.id]!!.add(robot)
        }
    }

    /**
     * Forgets the given [robot].
     */
    fun forgetRobot(robot: Robot) {
        robotsByUuid.remove(robot.uuid)

        EntityManager.forgetEntity(robot)
        EntityManager.forgetEntity(robot.hologram)

        if (robot.location.world != null) {
            val plot = PlotUtil.getPlot(robot.location)
            if (plot != null) {
                robotsByPlot[plot.id]?.remove(robot)
            }
        }
    }

    /**
     * Gets a copy of the tracked robots list.
     */
    fun getRobots(): Collection<Robot> {
        return robotsByUuid.values
    }

    /**
     * Tries to get a [Robot] by its [UUID].
     */
    fun getRobotById(uuid: UUID): Robot? {
        return robotsByUuid[uuid]
    }

    /**
     * Tries to get a list of [Robot] from the given [plotId].
     */
    fun getRobotsByPlot(plotId: PlotId): List<Robot> {
        return robotsByPlot.getOrDefault(plotId, emptyList())
    }

    fun isPrivileged(player: Player, location: Location): Boolean {
        if (RegionBypass.hasBypass(player)) {
            RegionBypass.attemptNotify(player)
            return true
        }

        val plot = PlotUtil.getPlot(location)
        if (plot != null) {
            if (plot.owners.contains(player.uniqueId) || plot.isAdded(player.uniqueId)) {
                return true
            }
        }

        player.sendMessage("${ChatColor.RED}You are not a member of this plot, so you can't do that!")
        return false
    }

    private fun findNextBackupFile(): File {
        return File(backupsFolder, "robots-" + SimpleDateFormat("yyyy-dd-MM_hh.mm.ss").format(Date()) + ".json")
    }

}