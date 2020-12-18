/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.permission.GangPermission
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ManageSettingsMenu(private val gang: Gang) : Menu() {

    companion object {
        private val BUTTON_SLOTS = listOf(10, 12, 14, 16, 28, 30, 32, 34)
    }

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Settings & Permissions"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for ((index, permission) in GangPermission.values().withIndex()) {
                buttons[BUTTON_SLOTS[index]] = PermissionButton(permission)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                GangMenu(gang).openMenu(player)
            }
        }
    }

    private inner class PermissionButton(private val permission: GangPermission) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${permission.detailedName}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")

            for (line in TextSplitter.split(length = 36, text = permission.description, linePrefix = "${ChatColor.GRAY}")) {
                description.add(line)
            }

            description.add("")

            for (value in GangPermission.PermissionValue.values()) {
                if (!permission.isCompatibleWith(value)) {
                    continue
                }

                if (gang.getPermissionValue(permission) == value) {
                    description.add(" ${ChatColor.BLUE}${ChatColor.BOLD}» ${ChatColor.GREEN}${value.detailedName}")
                } else {
                    description.add("    ${ChatColor.YELLOW}${value.detailedName}")
                }
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return permission.icon
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            gang.setPermissionValue(permission, permission.getNextValue(gang.getPermissionValue(permission)))

            when (permission) {
                GangPermission.ALLOW_VISITORS -> {
                    if (gang.getPermissionValue(permission) != GangPermission.PermissionValue.VISITORS) {
                        gang.sendMessagesToAll("${ChatColor.YELLOW}${player.name} is no longer allowing visitors at their gang headquarters.")
                        gang.kickVisitors()
                    }
                }
                GangPermission.ACCESS_CONTAINERS -> {
                    for (visitor in gang.getVisitingPlayers()) {
                        if (!gang.testPermission(visitor, GangPermission.ACCESS_CONTAINERS)) {
                            visitor.closeInventory()
                        }
                    }
                }
                else -> {}
            }
        }
    }

}