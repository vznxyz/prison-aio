/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.bank.listener

import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.user.bank.BankNoteHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object BankNoteLogListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (BankNoteHandler.isBankNoteItemStack(event.itemDrop.itemStack)) {
            event.itemDrop.setMetadata("BN_DROP", FixedMetadataValue(PrisonAIO.instance, event.player.uniqueId))

            val bankNote = BankNoteHandler.findBankNote(BankNoteHandler.extractId(event.itemDrop.itemStack))!!

            val log = StringBuilder()
                .append("${event.player.name} (${event.player.uniqueId}, ${event.player.address.address.hostAddress})")
                .append(" dropped a bank-note at ")
                .append("${event.itemDrop.location}")
                .append(" worth ")
                .append(bankNote.getPlainFormat())

            BankNoteHandler.logFile.commit(log.toString())
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onEntityPickupItemEvent(event: EntityPickupItemEvent) {
        if (event.entity is Player && BankNoteHandler.isBankNoteItemStack(event.item.itemStack)) {
            val player = event.entity as Player
            val bankNote = BankNoteHandler.findBankNote(BankNoteHandler.extractId(event.item.itemStack))!!

            val log = StringBuilder()
                .append("${player.name} (${player.uniqueId}, ${player.address.address.hostAddress})")
                .append(" picked up a bank-note at ")
                .append("${event.item.location}")
                .append(" worth ")
                .append(bankNote.getPlainFormat())

            if (event.item.hasMetadata("BN_DROP")) {
                val droppedBy = event.item.getMetadata("BN_DROP")[0].value() as UUID
                val droppedByUsername = Cubed.instance.uuidCache.name(droppedBy)

                log.append(" dropped by $droppedByUsername ($droppedBy)")
            }

            BankNoteHandler.logFile.commit(log.toString())
        }
    }

}