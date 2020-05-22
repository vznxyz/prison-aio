package net.evilblock.prisonaio.module.quest.impl.narcotic.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.entity.Player

class NarcoticsSupplierMenu : Menu() {

    override fun getTitle(player: Player): String {
        return "Pablo's Narcotics Stock"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()



        return buttons
    }

}