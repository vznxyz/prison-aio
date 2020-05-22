package net.evilblock.prisonaio.module.cell.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.permission.CellPermission
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ManageSettingsMenu(private val cell: Cell) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Manage Settings"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        val startAt = 10
        CellPermission.values().forEachIndexed { index, permission ->
            buttons[startAt + (index * 2)] = PermissionButton(permission)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 27
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                JerryMenu(cell.guideNpc).openMenu(player)
            }, 1L)
        }
    }

    private inner class PermissionButton(private val permission: CellPermission) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${permission.detailedName}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")

            for (line in TextSplitter.split(36, permission.description, "${ChatColor.GRAY}", " ")) {
                description.add(line)
            }

            description.add("")

            for (value in CellPermission.PermissionValue.values()) {
                if (!permission.isCompatibleWith(value)) {
                    continue
                }

                if (cell.getPermissionValue(permission) == value) {
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
            cell.setPermissionValue(permission, permission.getNextValue(cell.getPermissionValue(permission)))

            when (permission) {
                CellPermission.ALLOW_VISITORS -> {
                    if (cell.getPermissionValue(permission) != CellPermission.PermissionValue.VISITORS) {
                        cell.kickVisitors()
                    }
                }
                CellPermission.ACCESS_CONTAINERS -> {
                    for (activePlayer in cell.getActivePlayers()) {
                        if (!cell.testPermission(activePlayer, CellPermission.ACCESS_CONTAINERS)) {
                            activePlayer.closeInventory()
                        }
                    }
                }
                else -> {}
            }
        }
    }

}