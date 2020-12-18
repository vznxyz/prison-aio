/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.auction.menu.button

import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.time.Instant
import java.util.*

class NotificationsButton(private val user: User) : TexturedHeadButton(Constants.IB_ALARM_ON_TEXTURE) {

    override fun getName(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Notifications"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            val notifications = user.auctionHouseData.getNotifications().sortedBy { it.createdAt }.reversed()
            if (notifications.isEmpty()) {
                desc.add("${ChatColor.GRAY}You don't have any notifications!")
            } else {
                val onScreen = if (notifications.size > 20) {
                    notifications.dropLast(notifications.size - 20)
                } else {
                    notifications
                }

                desc.add("")

                for (notification in onScreen) {
                    val formatted = buildString {
                        append(" ")

                        if (notification.read) {
                            append("${ChatColor.GREEN}${Constants.CHECK_SYMBOL} ")
                        } else {
                            append("${ChatColor.BLUE}${Constants.CURVED_ARROW_RIGHT} ")
                        }

                        append("${ChatColor.DARK_AQUA}")
                        append("[")
                        append(TimeUtil.formatIntoCalendarString(Date.from(Instant.ofEpochMilli(notification.createdAt))))
                        append("] ")
                        append("${ChatColor.GRAY}")
                        append(notification.message)
                    }

                    if (ChatColor.stripColor(formatted).length >= 80) {
                        var builder = StringBuilder()

                        val split = formatted.split(" ")
                        for ((index, piece) in split.withIndex()) {
                            if (ChatColor.stripColor(builder.toString() + piece).length >= 64) {
                                desc.add(builder.toString().dropLast(1))
                                builder = StringBuilder("     ${ChatColor.GRAY}$piece ")
                            } else {
                                builder.append("$piece ")
                            }

                            if (index >= split.size - 1) {
                                if (builder.isNotEmpty()) {
                                    desc.add(builder.toString())
                                }
                            }
                        }
                    } else {
                        desc.add(formatted)
                    }
                }

                if (onScreen.any { !it.read }) {
                    desc.add("")
                    desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to mark as read")
                }
            }
        }
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            user.auctionHouseData.readNotifications()
        }
    }

}