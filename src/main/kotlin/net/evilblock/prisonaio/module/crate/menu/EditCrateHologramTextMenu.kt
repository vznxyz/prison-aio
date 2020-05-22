package net.evilblock.prisonaio.module.crate.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CrateHandler
import net.evilblock.prisonaio.module.crate.placed.PlacedCrateHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditCrateHologramTextMenu(private val crate: Crate) : TextEditorMenu(lines = crate.hologramLines.toMutableList()) {

    init {
        supportsColors = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Hologram Text - ${crate.name}"
    }

    override fun onSave(player: Player, list: List<String>) {
        crate.hologramLines = list.toMutableList()
        CrateHandler.saveData()

        for (placedCrate in PlacedCrateHandler.getPlacedCrates()) {
            if (placedCrate.crate == crate) {
                placedCrate.hologram.updateLines(crate.getHologramLines())
            }
        }
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditCrateMenu(crate).openMenu(player)
        }
    }

    override fun getPromptBuilder(player: Player): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please enter the new text.")
            .charLimit(100)
    }

}