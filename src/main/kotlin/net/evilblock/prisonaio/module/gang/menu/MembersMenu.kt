/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangMember
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.time.Instant
import java.util.*

class MembersMenu(private val gang: Gang) : Menu() {

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return "Gang Members"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (member in gang.getMembers().values) {
            buttons[buttons.size] = MemberButton(member)
        }

        return buttons
    }

    private inner class MemberButton(private val member: GangMember) : Button() {
        override fun getName(player: Player): String {
            val memberPlayer = Bukkit.getPlayer(member.uuid)

            return if (memberPlayer == null) {
                "${ChatColor.RED}${ChatColor.BOLD}${member.getUsername()}"
            } else {
                if (gang.isActivePlayer(memberPlayer)) {
                    "${ChatColor.GREEN}${ChatColor.BOLD}${member.getUsername()}"
                } else {
                    "${ChatColor.YELLOW}${ChatColor.BOLD}${member.getUsername()}"
                }
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}Role: ${member.role.color}${member.role.rendered}")
            description.add("${ChatColor.GRAY}Last Played: ${ChatColor.GREEN}${TimeUtil.formatIntoDetailedString(((System.currentTimeMillis() - member.lastPlayed) / 1000.0).toInt())} ago")
            description.add("")

            val invitedBy = if (member.invitedBy != null) {
                Cubed.instance.uuidCache.name(member.invitedBy!!)
            } else {
                "Unknown"
            }

            description.add("${ChatColor.GRAY}Invited By: ${ChatColor.YELLOW}$invitedBy")

            val invitedAt = if (member.invitedAt != null) {
                TimeUtil.formatIntoDateString(Date.from(Instant.ofEpochMilli(member.invitedAt!!)))
            } else {
                "N/A"
            }

            description.add("${ChatColor.GRAY}Invited At: ${ChatColor.YELLOW}$invitedAt")
            description.add("")
            description.add("${ChatColor.GRAY}Trophies Collected: ${ChatColor.GOLD}${NumberUtils.format(member.trophiesCollected)}")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3.toByte()
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)
            val meta = item.itemMeta as SkullMeta
            meta.owner = Cubed.instance.uuidCache.name(member.uuid)
            item.itemMeta = meta
            return item
        }
    }

}