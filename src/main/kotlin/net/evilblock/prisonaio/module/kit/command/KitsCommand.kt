package net.evilblock.prisonaio.module.kit.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.menu.template.menu.TemplateMenu
import net.evilblock.prisonaio.module.kit.KitHandler
import net.evilblock.kits.event.OpenKitsMenuEvent
import org.bukkit.entity.Player

object KitsCommand {

    @Command(
        names = ["kits"],
        description = "Opens the Kits GUI"
    )
    @JvmStatic
    fun execute(player: Player) {
        val event = OpenKitsMenuEvent(player)
        event.call()

        if (!event.isCancelled) {
            TemplateMenu(KitHandler.getMenuTemplate()).openMenu(player)
        }
    }

}