/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.narcotic.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.impl.narcotic.entity.PabloEscobarNpcEntity
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnPabloEscobarCommand {

    @Command(
        names = ["quest npc spawn pablo-escobar", "quests npc spawn pablo-escobar"],
        description = "Spawn the Pablo Escobar NPC",
        permission = "op"
    )
    @JvmStatic
    fun execute(player: Player) {
        val pabloEscobar = PabloEscobarNpcEntity(player.location)
        pabloEscobar.initializeData()

        pabloEscobar.updateTexture(
            value = QuestsModule.getNpcTextureValue("pablo-escobar"),
            signature = QuestsModule.getNpcTextureSignature("pablo-escobar")
        )

        EntityManager.trackEntity(pabloEscobar)

        player.sendMessage("${ChatColor.GREEN}Successfully spawned Pablo Escobar NPC.")
    }

}