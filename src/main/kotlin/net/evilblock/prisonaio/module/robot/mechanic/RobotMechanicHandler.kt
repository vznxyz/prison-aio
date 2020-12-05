package net.evilblock.prisonaio.module.robot.mechanic

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import com.intellectualcrafters.plot.`object`.PlotId
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.robot.RobotsModule
import org.bukkit.ChatColor
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object RobotMechanicHandler : PluginHandler {

    private val dataType = object : TypeToken<Set<PlotId>>() {}.type

    private val usedPlots: MutableSet<PlotId> = ConcurrentHashMap.newKeySet()

    override fun getModule(): PluginModule {
        return RobotsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "robot-mechanics.json")
    }

    override fun initialLoad() {
        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                try {
                    val data = Cubed.gson.fromJson(reader, dataType) as Collection<PlotId>
                    usedPlots.addAll(data)

                    getModule().getPluginFramework().logger.info("Loaded ${usedPlots.size} plots from mechanics.json!")
                } catch (e: Exception) {
                    getModule().getPluginFramework().logger.severe("Failed to parse mechanics.json!")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun saveData() {
        try {
            java.nio.file.Files.write(getInternalDataFile().toPath(), Cubed.gson.toJson(usedPlots, dataType).toByteArray())
        } catch (e: Exception) {
            getModule().getPluginFramework().systemLog("${ChatColor.RED}Failed to save mechanics to robot-mechanics.json!")
            e.printStackTrace()
        }
    }

    fun isPlotTracked(plotId: PlotId): Boolean {
        return usedPlots.contains(plotId)
    }

    fun trackPlot(plotId: PlotId) {
        usedPlots.add(plotId)
    }

    fun forgetPlot(plotId: PlotId) {
        usedPlots.remove(plotId)
    }

    fun getMechanicTexture(): Pair<String, String> {
        return Pair(getModule().config.getString("mechanic.texture-value"), getModule().config.getString("mechanic.texture-signature"))
    }

}