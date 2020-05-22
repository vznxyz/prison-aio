package net.evilblock.prisonaio.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.text.DecimalFormat

object ReloadCommand {

    @Command(
        names = ["prison reload"],
        description = "Reload PrisonAIO",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        val startAt = System.currentTimeMillis()

        PrisonAIO.instance.systemLog("Reloading plugin...")

        PrisonAIO.instance.enabledModules.forEach { module ->
            PrisonAIO.instance.logger.info("Reloading module ${module.getName()}...")

            try {
                module.onReload()
                PrisonAIO.instance.logger.info("Reloaded module ${module.getName()}!")
            } catch (e: Exception) {
                PrisonAIO.instance.systemLog("${ChatColor.RED}Failed to reload module ${module.getName()}!")
                PrisonAIO.instance.logger.severe("Failed to reload module ${module.getName()}!")
            }
        }

        val endAt = System.currentTimeMillis()

        PrisonAIO.instance.systemLog("Finished reloading in ${endAt - startAt}ms!")
    }

}