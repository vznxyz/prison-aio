/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.reward.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class CrateRewardButton(protected val reward: CrateReward) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        val buttonItem = reward.getIcon().clone()
        val meta = buttonItem.itemMeta

        if (meta != null) {
            if (!preserveName) {
                meta.displayName = ChatColor.translateAlternateColorCodes('&', getName(player))
            }

            meta.lore = getDescription(player)

            val appliedMeta = applyMetadata(player, meta) ?: meta
            buttonItem.itemMeta = appliedMeta
        }

        return buttonItem
    }

}