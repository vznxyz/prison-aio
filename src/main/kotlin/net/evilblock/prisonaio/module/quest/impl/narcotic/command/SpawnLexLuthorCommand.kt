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
import net.evilblock.prisonaio.module.quest.impl.narcotic.entity.LexLuthorNpcEntity
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnLexLuthorCommand {

    @Command(
        names = ["quest npc spawn lex-luthor", "quests npc spawn lex-luthor"],
        description = "Spawn the Lex Luthor NPC",
        permission = Permissions.QUESTS_ADMIN
    )
    @JvmStatic
    fun execute(player: Player) {
        val lexLuthor = LexLuthorNpcEntity(player.location)
        lexLuthor.initializeData()

        lexLuthor.updateTexture(
            value = QuestsModule.getNpcTextureValue("lex-luthor"),
            signature = QuestsModule.getNpcTextureSignature("lex-luthor")
        )

        EntityManager.trackEntity(lexLuthor)

        player.sendMessage("${ChatColor.GREEN}Successfully spawned Lex Luthor NPC.")
    }

}