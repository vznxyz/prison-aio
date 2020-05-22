package net.evilblock.prisonaio.module.quest.impl.narcotic.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.impl.narcotic.command.parameter.DrugDealerAutoComplete
import net.evilblock.prisonaio.module.quest.impl.narcotic.entity.DrugDealerNpcEntity
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnDealerCommand {

    @Command(
        names = ["quest npc spawn drug-dealer", "quests npc spawn drug-dealer"],
        description = "Spawn a Drug Dealer NPC",
        permission = Permissions.QUESTS_ADMIN
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "id") drugDealer: DrugDealerAutoComplete) {
        val entity = DrugDealerNpcEntity(npcId = drugDealer.get(), location = player.location)
        entity.initializeData()

        entity.updateTexture(
            value = QuestsModule.getNpcTextureValue(drugDealer.get()),
            signature = QuestsModule.getNpcTextureSignature(drugDealer.get())
        )

        EntityManager.trackEntity(entity)
        EntityManager.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully spawned drug-dealer NPC.")
    }

}