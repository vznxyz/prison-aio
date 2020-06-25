/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.key.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.crate.key.CrateKey
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class KeyDetailsMenu(private val key: CrateKey) : Menu() {

    override fun getTitle(player: Player): String {
        return "Key Details"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0..8) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
        }

        buttons[4] = KeyButton()

        return buttons
    }

    private inner class KeyButton : Button() {
        override fun getName(player: Player): String {
            return "${key.crate.name} Key"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("${ChatColor.GRAY}(ID: ${key.uuid})")
            description.add("")

            val issuedTo = Cubed.instance.uuidCache.name(key.issuedTo)
            description.add("${ChatColor.YELLOW}Issued to: ${ChatColor.RED}$issuedTo")

            val issuerName = if (key.issuedBy == null) {
                "${ChatColor.BOLD}Console"
            } else {
                Cubed.instance.uuidCache.name(key.issuedBy!!)
            }

            description.add("${ChatColor.YELLOW}Issued by: ${ChatColor.RED}$issuerName")
            description.add("${ChatColor.YELLOW}Issued on: ${ChatColor.RED}${TimeUtil.formatIntoCalendarString(key.issuedAt)}")
            description.add("${ChatColor.YELLOW}Reason: ${ChatColor.RED}`${ChatColor.ITALIC}${key.reason}${ChatColor.RED}`")
            description.add("${ChatColor.YELLOW}Keys used: ${ChatColor.RED}${key.uses}/${key.maxUses}")
            description.add("${ChatColor.YELLOW}Duped use attempts: ${ChatColor.RED}${key.dupedUseAttempts}")
            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.TRIPWIRE_HOOK
        }
    }

}