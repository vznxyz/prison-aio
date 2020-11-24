/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.trade

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.math.ceil

class Trade (
    val sender: Player,
    val target: Player
) {

    val id: UUID = UUID.randomUUID()

    val senderOfferings: MutableList<ItemStack> = arrayListOf()
    val targetOfferings: MutableList<ItemStack> = arrayListOf()

    var senderAccepted: Boolean = false
    var targetAccepted: Boolean = false

    var lockedIn: Boolean = false
    var lockedInTask: BukkitTask? = null

    var completed: Boolean = false
    var completedAt: Long = -1L

    var cancelled: Boolean = false

    fun isSender(player: Player): Boolean {
        return player.uniqueId == sender.uniqueId
    }

    fun isTarget(player: Player): Boolean {
        return player.uniqueId == target.uniqueId
    }

    fun getOfferings(player: Player): MutableList<ItemStack> {
        return if (isSender(player)) {
            senderOfferings
        } else {
            targetOfferings
        }
    }

    private fun cancelLockIn() {
        senderAccepted = false
        targetAccepted = false

        lockedIn = false
        lockedInTask?.cancel()
        lockedInTask = null
        completedAt = -1L
    }

    private fun checkBothAccepted() {
        if (!cancelled && !completed && !lockedIn) {
            if (senderAccepted && targetAccepted) {
                lockedIn = true
                completedAt = System.currentTimeMillis() + 5_000L

                lockedInTask = Tasks.asyncTimer(object : BukkitRunnable() {
                    override fun run() {
                        if (cancelled || completed) {
                            cancel()
                            return
                        }

                        if (lockedIn && senderAccepted && targetAccepted) {
                            if (System.currentTimeMillis() >= completedAt) {
                                TradeHandler.forgetActiveTrade(this@Trade)

                                cancel()
                                complete()
                            }
                        }
                    }
                }, 10L, 10L)
            }
        }
    }

    fun start() {
        TradeMenu().openMenu(target)
        TradeMenu().openMenu(sender)
    }

    fun complete() {
        completed = true

        closeMenus()

        for (item in senderOfferings) {
            target.inventory.addItem(item)
            target.updateInventory()
        }

        for (item in targetOfferings) {
            sender.inventory.addItem(item)
            target.updateInventory()
        }

        sender.sendMessage("${ChatColor.GREEN}You've completed your trade with ${Formats.formatPlayer(target)}${ChatColor.GREEN}!")
        UserHandler.getUser(sender.uniqueId).statistics.addTradeCompleted()

        target.sendMessage("${ChatColor.GREEN}You've completed your trade with ${Formats.formatPlayer(sender)}${ChatColor.GREEN}!")
        UserHandler.getUser(target.uniqueId).statistics.addTradeCompleted()
    }

    fun cancel() {
        cancelled = true

        closeMenus()

        for (item in senderOfferings) {
            sender.inventory.addItem(item)
        }

        sender.updateInventory()

        for (item in targetOfferings) {
            target.inventory.addItem(item)
        }

        target.updateInventory()
    }

    private fun closeMenus() {
        if (Menu.currentlyOpenedMenus.containsKey(sender.uniqueId) && Menu.currentlyOpenedMenus[sender.uniqueId]!! is TradeMenu) {
            sender.closeInventory()
        }

        if (Menu.currentlyOpenedMenus.containsKey(target.uniqueId) && Menu.currentlyOpenedMenus[target.uniqueId]!! is TradeMenu) {
            target.closeInventory()
        }
    }

    fun sendMessage(message: String) {
        sender.sendMessage(message)
        target.sendMessage(message)
    }

    private inner class TradeMenu : Menu() {
        init {
            updateAfterClick = true
            autoUpdate = true
        }

        override fun getAutoUpdateTicks(): Long {
            return 500L
        }

        override fun getTitle(player: Player): String {
            return if (isSender(player)) {
                "Trading with ${target.name}"
            } else {
                "Trading with ${sender.name}"
            }
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            val left = if (isSender(player)) {
                sender
            } else {
                target
            }

            val right = if (isSender(player)) {
                target
            } else {
                sender
            }

            buttons[2] = ParticipantHeadButton(left)
            buttons[47] = ParticipantStatusButton(left)

            buttons[6] = ParticipantHeadButton(right)
            buttons[51] = ParticipantStatusButton(right)

            buttons[49] = TradeStatusButton()

            for ((index, item) in getOfferings(left).withIndex()) {
                buttons[LEFT_OFFERING_SLOTS[index]] = OfferedItemButton(item, left)
            }

            for ((index, item) in getOfferings(right).withIndex()) {
                buttons[RIGHT_OFFERING_SLOTS[index]] = OfferedItemButton(item, right)
            }

            for (i in BORDER_SLOTS) {
                buttons[i] = GlassButton(7)
            }

            return buttons
        }

        override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
            if (cancelled || completed) {
                return false
            }

            if (lockedIn) {
                cancelLockIn()
                sendMessage("${Formats.formatPlayer(player)} ${ChatColor.YELLOW}changed their offer! Both players will need to re-accept the trade.")
                return false
            }

            if (isSender(player)) {
                if (senderOfferings.size >= LEFT_OFFERING_SLOTS.size) {
                    player.sendMessage("${ChatColor.RED}You've reached the maximum amount of items you can offer in one trade!")
                    return false
                }

                if (player.inventory.contains(itemStack)) {
                    senderOfferings.add(itemStack)
                    return true
                }
            } else {
                if (targetOfferings.size >= RIGHT_OFFERING_SLOTS.size) {
                    player.sendMessage("${ChatColor.RED}You've reached the maximum amount of items you can offer in one trade!")
                    return false
                }

                if (player.inventory.contains(itemStack)) {
                    targetOfferings.add(itemStack)
                    return true
                }
            }

            return false
        }

        override fun onClose(player: Player, manualClose: Boolean) {
            if (!cancelled && !completed) {
                TradeHandler.forgetActiveTrade(this@Trade)

                cancel()
                sendMessage("${Formats.formatPlayer(player)} ${ChatColor.RED}declined the trade!")
            }
        }
    }

    private inner class TradeStatusButton : Button() {
        override fun getName(player: Player): String {
            return if (lockedIn && senderAccepted && targetAccepted) {
                val remainingSeconds = (ceil((completedAt - System.currentTimeMillis()) / 1000.0)).toInt()
                "${ChatColor.GREEN}${ChatColor.BOLD}Completing trade in $remainingSeconds seconds..."
            } else if (senderAccepted && !targetAccepted) {
                "${ChatColor.BLUE}${ChatColor.BOLD}Waiting for ${target.name} to accept..."
            } else if (!senderAccepted && targetAccepted) {
                "${ChatColor.BLUE}${ChatColor.BOLD}Waiting for ${sender.name} to accept..."
            } else {
                if (senderOfferings.isEmpty() && targetOfferings.isEmpty()) {
                    "${ChatColor.RED}${ChatColor.BOLD}Waiting for both players to offer..."
                } else {
                    "${ChatColor.BLUE}${ChatColor.BOLD}Waiting for both players to accept..."
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return if (lockedIn && senderAccepted && targetAccepted) {
                5.toByte()
            } else if (!senderAccepted || !targetAccepted) {
                11.toByte()
            } else {
                if (senderOfferings.isEmpty() && targetOfferings.isEmpty()) {
                    14.toByte()
                } else {
                    11.toByte()
                }
            }
        }
    }

    private inner class ParticipantHeadButton(private val participant: Player) : Button() {
        override fun getName(player: Player): String {
            return if (player.uniqueId == participant.uniqueId) {
                "${ChatColor.GREEN}${ChatColor.BOLD}Your Offerings"
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}${participant.name}'s Offerings"
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3.toByte()
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            val meta = itemMeta as SkullMeta
            meta.owner = participant.name
            return meta
        }

        override fun getDescription(player: Player): List<String> {
            val user = UserHandler.getUser(participant.uniqueId)
            return listOf("${ChatColor.GRAY}Completed trades: ${ChatColor.GREEN}${user.statistics.getTradesCompleted()}")
        }
    }

    private inner class ParticipantStatusButton(private val participant: Player) : Button() {
        override fun getName(player: Player): String {
            if (player.uniqueId == participant.uniqueId) {
                return if (isSender(player)) {
                    if (senderAccepted) {
                        "${ChatColor.GREEN}${ChatColor.BOLD}Accepted trade"
                    } else {
                        "${ChatColor.GREEN}${ChatColor.BOLD}Click to accept trade"
                    }
                } else {
                    if (targetAccepted) {
                        "${ChatColor.GREEN}${ChatColor.BOLD}Accepted trade"
                    } else {
                        "${ChatColor.GREEN}${ChatColor.BOLD}Click to accept trade"
                    }
                }
            }

            return if (isSender(participant)) {
                if (senderAccepted) {
                    "${ChatColor.GREEN}${ChatColor.BOLD}Accepted trade"
                } else {
                    "${ChatColor.RED}${ChatColor.BOLD}Hasn't accepted trade"
                }
            } else {
                if (targetAccepted) {
                    "${ChatColor.GREEN}${ChatColor.BOLD}Accepted trade"
                } else {
                    "${ChatColor.RED}${ChatColor.BOLD}Hasn't accepted trade"
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            if (player.uniqueId == participant.uniqueId) {
                val accepted = (isSender(player) && senderAccepted) || (isTarget(player) && targetAccepted)

                return if (accepted) {
                    5
                } else {
                    13
                }
            }

            return if (isSender(participant)) {
                if (senderAccepted) {
                    5
                } else {
                    14
                }
            } else {
                if (targetAccepted) {
                    5
                } else {
                    14
                }
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && player.uniqueId == participant.uniqueId) {
                if (cancelled || completed) {
                    return
                }

                if (isSender(player)) {
                    if (!senderAccepted) {
                        senderAccepted = true
                        checkBothAccepted()
                    }
                } else {
                    if (!targetAccepted) {
                        targetAccepted = true
                        checkBothAccepted()
                    }
                }
            }
        }
    }

    private inner class OfferedItemButton(private val item: ItemStack, private val offeredBy: Player) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return item.clone()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType == ClickType.SHIFT_LEFT && player.uniqueId == offeredBy.uniqueId) {
                if (completed || cancelled) {
                    return
                }

                if (lockedIn) {
                    cancelLockIn()
                    sendMessage("${Formats.formatPlayer(player)} ${ChatColor.YELLOW}changed their offer! Both players will need to re-accept the trade.")
                    return
                }

                if (isSender(player)) {
                    if (!senderOfferings.contains(item)) {
                        return
                    }

                    senderOfferings.remove(item)
                } else {
                    if (!targetOfferings.contains(item)) {
                        return
                    }

                    targetOfferings.remove(item)
                }

                player.inventory.addItem(item)
                player.updateInventory()
            }
        }
    }

    companion object {
        private val BORDER_SLOTS = listOf(
            0, 1, 3, 4, 5, 7, 8,
            9, 13, 17,
            18, 22, 26,
            27, 31, 35,
            36, 40, 44,
            45, 46, 48, 50, 52, 53
        )

        private val LEFT_OFFERING_SLOTS = listOf(
            10, 11, 12,
            19, 20, 21,
            28, 29, 30,
            37, 38, 39
        )

        private val RIGHT_OFFERING_SLOTS = listOf(
            14, 15, 16,
            23, 24, 25,
            32, 33, 34,
            41, 42, 43
        )
    }

}