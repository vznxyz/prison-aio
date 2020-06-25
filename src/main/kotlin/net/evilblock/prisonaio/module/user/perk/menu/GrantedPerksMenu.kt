/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.perk.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.perk.PerkGrant
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class GrantedPerksMenu(private val user: User) : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "${user.getUsername()}'s Granted Perks"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (grant in user.perks.getPerkGrants().sortedBy { !it.isExpired() }) {
            buttons[buttons.size] = GrantButton(grant)
        }

        return buttons
    }

    private inner class GrantButton(private val grant: PerkGrant) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${grant.perk.displayName} Perk"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            val issuerName = if (grant.issuedBy == null) {
                "${ChatColor.BOLD}Console"
            } else {
                Cubed.instance.uuidCache.name(grant.issuedBy!!)
            }

            val formattedDate = TimeUtil.formatIntoCalendarString(grant.issuedAt)

            description.add("")
            description.add("${ChatColor.YELLOW}Issued by: ${ChatColor.RED}$issuerName")
            description.add("${ChatColor.YELLOW}Issued on: ${ChatColor.RED}$formattedDate")
            description.add("${ChatColor.YELLOW}Reason: ${ChatColor.RED}`${ChatColor.ITALIC}${grant.getReason()}${ChatColor.RED}`")
            description.add("")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete grant")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return grant.perk.icon.type
        }

        override fun getDamageValue(player: Player): Byte {
            return grant.perk.icon.durability.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        user.perks.forgetPerkGrant(grant)
                        player.sendMessage("${ChatColor.GREEN}Successfully removed grant.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

}