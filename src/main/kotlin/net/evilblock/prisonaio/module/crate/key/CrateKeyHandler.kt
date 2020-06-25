/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.key

import net.evilblock.cubed.util.bukkit.HiddenLore
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.PluginHandler
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CrateHandler
import net.evilblock.prisonaio.module.crate.CratesModule
import net.evilblock.prisonaio.module.crate.placed.PlacedCrateHandler
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object CrateKeyHandler : PluginHandler {

    override fun getModule(): PluginModule {
        return CratesModule
    }

    fun extractCrate(itemStack: ItemStack): Crate {
        val firstLoreLine = itemStack.itemMeta.lore[0]
        if (HiddenLore.hasHiddenString(firstLoreLine)) {
            val extracted = HiddenLore.extractHiddenString(firstLoreLine)!!
            if (extracted.startsWith("CrateKey")) {
                val crateId = extracted.split(":")[1]
                return CrateHandler.findCrate(crateId)!!
            }
        }
        throw IllegalStateException("Failed to extract crate from key!")
    }

    fun isCrateKeyItemStack(itemStack: ItemStack?): Boolean {
        if (itemStack == null || itemStack.type == Material.AIR) {
            return false
        }

        if (!itemStack.hasItemMeta() || !itemStack.itemMeta.hasDisplayName() || !itemStack.itemMeta.hasLore()) {
            return false
        }

        if (PlacedCrateHandler.isChestType(itemStack.type)) {
            return false
        }

        val firstLoreLine = itemStack.itemMeta.lore[0]
        if (HiddenLore.hasHiddenString(firstLoreLine)) {
            val extracted = HiddenLore.extractHiddenString(firstLoreLine)!!
            if (extracted.startsWith("CrateKey")) {
                val crateId = extracted.split(":")[1]
                if (CrateHandler.findCrate(crateId) != null) {
                    return true
                }
            }
        }

        return false
    }

    fun makeKeyItemStack(player: Player, crate: Crate, amount: Int, issuedBy: UUID?, reason: String): ItemStack {
        return ItemBuilder
            .copyOf(crate.keyItemStack)
            .name("${crate.name} Key")
            .setLore(listOf(
                HiddenLore.encodeString("CrateKey:${crate.id}")
            ))
            .amount(amount)
            .build()
    }

}