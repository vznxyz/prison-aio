package net.evilblock.prisonaio.module.quest.impl.narcotic.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import org.bukkit.ChatColor
import org.bukkit.Location

class PabloEscobarNpcEntity(location: Location) : NpcEntity(lines = listOf(""), location = location) {

    override fun initializeData() {
        super.initializeData()

        updateLines(lines = arrayListOf(
            "${ChatColor.YELLOW}${ChatColor.BOLD}Pablo Escobar",
            "${ChatColor.GRAY}(Narcotics Supplier)"
        ))
    }

}