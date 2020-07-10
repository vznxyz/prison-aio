/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.chat.listener

import me.clip.deluxetags.DeluxeTag
import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.rank.RanksModule
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.util.Constants
import org.apache.commons.lang.WordUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatFormatListeners : Listener {

    private const val ITEM_PLACEHOLDER = "[item]"

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        event.isCancelled = true

        val user = UserHandler.getUser(event.player.uniqueId)
        val tagPrefix = ChatColor.translateAlternateColorCodes('&', DeluxeTag.getPlayerDisplayTag(event.player.uniqueId.toString()) ?: "")

        val prestigeTag = if (user.getCurrentPrestige() >= RanksModule.getMaxPrestige()) {
            RanksModule.getMaxPrestigeTag()
        } else {
            (user.getCurrentPrestige()).toString()
        }

        val formattedMessage = String.format(event.format, event.player.displayName, "")
            .replace("{prisonRank}", user.getCurrentRank().displayName)
            .replace("{prisonPrestige}", prestigeTag)
            .replace("{tagPrefix}", tagPrefix)

        val tooltipLines = arrayListOf<FancyMessage>()
        tooltipLines.add(FancyMessage(" ${ChatColor.RED}${ChatColor.BOLD}${event.player.name}"))
        tooltipLines.add(FancyMessage(" ${ChatColor.RED}⚔ ${ChatColor.GRAY}Rank ${user.getCurrentRank().displayName}"))

        if (user.getCurrentPrestige() == 0) {
            tooltipLines.add(FancyMessage(" ${ChatColor.RED}${ChatColor.BOLD}⭑ ${ChatColor.GRAY}Not Prestiged"))
        } else {
            tooltipLines.add(FancyMessage(" ${ChatColor.RED}${ChatColor.BOLD}⭑ ${ChatColor.GRAY}Prestige ${user.getCurrentPrestige()}"))
        }

        val moneyBalance = user.getMoneyBalance()
        val formattedMoneyBalance = NumberUtils.format(moneyBalance)
        tooltipLines.add(FancyMessage(" ${ChatColor.RED}${ChatColor.BOLD}$ ${ChatColor.GRAY}$formattedMoneyBalance"))

        val formattedTokensBalance = NumberUtils.format(user.getTokensBalance())
        tooltipLines.add(FancyMessage(" ${ChatColor.RED}${ChatColor.BOLD}⏣ ${ChatColor.GRAY}$formattedTokensBalance"))

        tooltipLines.add(FancyMessage(""))
        tooltipLines.add(FancyMessage("${ChatColor.YELLOW}Click to view ${event.player.name}'s profile"))

        val lastColors = ChatColor.getLastColors(formattedMessage)

        val fancyMessage = FancyMessage(formattedMessage)
            .formattedTooltip(*tooltipLines.toTypedArray())
            .command("/prof ${event.player.name}")

        if (event.message.contains(ITEM_PLACEHOLDER, ignoreCase = true)) {
            val placeholderIndex = event.message.indexOf(ITEM_PLACEHOLDER, ignoreCase = true)
            if (placeholderIndex != event.message.lastIndexOf(ITEM_PLACEHOLDER, ignoreCase = true)) {
                event.player.sendMessage("${ChatColor.RED}Please link the item only once per message.")
                event.isCancelled = true
                return
            }

            val itemInHand = event.player.inventory.itemInMainHand
            if (itemInHand == null || itemInHand.type == Material.AIR) {
                event.player.sendMessage("${ChatColor.RED}You don't have an item in your hand.")
                event.isCancelled = true
                return
            }

            // get display name or convert item type to name
            val itemName = if (itemInHand.hasItemMeta() && itemInHand.itemMeta.hasDisplayName()) {
                itemInHand.itemMeta.displayName
            } else {
                WordUtils.capitalizeFully(itemInHand.type.name.replace("_".toRegex(), " "))
            }

            // add first part
            val firstPart = event.message.substring(0, placeholderIndex)
            fancyMessage.then(lastColors + firstPart)

            // add middle part
            fancyMessage.then("${ChatColor.DARK_GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${ChatColor.GRAY}$itemName ${ChatColor.DARK_GRAY}${Constants.DOUBLE_ARROW_LEFT}")

            // add hover to middle part
            if (itemInHand.hasItemMeta() && itemInHand.itemMeta.hasLore()) {
                fancyMessage.formattedTooltip(itemInHand.itemMeta.lore.map { FancyMessage(it) })
            }

            // add last part
            if (!event.message.endsWith(ITEM_PLACEHOLDER, ignoreCase = true)) {
                val lastPart = event.message.substring(placeholderIndex + 6)
                fancyMessage.then(ChatColor.getLastColors(firstPart) + lastPart)
            }
        } else {
            fancyMessage.then(lastColors + event.message)
        }

        for (player in event.recipients) {
            fancyMessage.send(player)
        }

        fancyMessage.send(Bukkit.getConsoleSender())
    }

}