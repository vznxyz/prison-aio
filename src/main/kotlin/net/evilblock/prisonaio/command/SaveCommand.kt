package net.evilblock.prisonaio.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.error.ErrorHandler
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object SaveCommand {

    @Command(
        names = ["prison save"],
        description = "Save all PrisonAIO data",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        PrisonAIO.instance.systemLog("Saving data...")

        val startAt = System.currentTimeMillis()

        for (world in Bukkit.getWorlds()) {
            try {
                Tasks.sync {
                    world.save()
                }
            } catch (exception: Exception) {
                ErrorHandler.generateErrorLog(
                    errorType = "saveWorld",
                    event = mapOf("WorldName" to world.name),
                    exception = exception
                )

                PrisonAIO.instance.systemLog("${ChatColor.RED}Failed to save world ${world.name}!")
            }
        }

        PrisonAIO.instance.saveModules()

        val endAt = System.currentTimeMillis()

        PrisonAIO.instance.systemLog("Finished saving data in ${endAt - startAt}ms!")
    }

}