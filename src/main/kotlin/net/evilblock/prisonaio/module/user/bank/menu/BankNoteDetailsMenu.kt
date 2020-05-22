package net.evilblock.prisonaio.module.user.bank.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.user.bank.BankNote
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class BankNoteDetailsMenu(private val bankNote: BankNote) : Menu() {

    override fun getTitle(player: Player): String {
        return "Bank Note Details"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0..8) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
        }

        buttons[4] = BankNoteButton()

        return buttons
    }

    private inner class BankNoteButton : Button() {
        override fun getName(player: Player): String {
            return "${bankNote.getFormattedValue()} ${ChatColor.GREEN}${ChatColor.BOLD}Bank Note"
        }

        override fun getDescription(player: Player): List<String> {

            val description = arrayListOf<String>()
            description.add("${ChatColor.GRAY}(ID: ${bankNote.uuid})")
            description.add("")

            val issuedTo = Cubed.instance.uuidCache.name(bankNote.issuedTo)
            description.add("${ChatColor.YELLOW}Issued to: ${ChatColor.RED}$issuedTo")

            val issuerName = if (bankNote.issuedBy == null) {
                "${ChatColor.BOLD}Console"
            } else {
                Cubed.instance.uuidCache.name(bankNote.issuedBy!!)
            }

            description.add("${ChatColor.YELLOW}Issued by: ${ChatColor.RED}$issuerName")
            description.add("${ChatColor.YELLOW}Issued on: ${ChatColor.RED}${TimeUtil.formatIntoCalendarString(bankNote.issuedAt)}")
            description.add("${ChatColor.YELLOW}Reason: ${ChatColor.RED}`${ChatColor.ITALIC}${bankNote.reason}${ChatColor.RED}`")

            val formattedState = if (bankNote.redeemed) {
                "${ChatColor.GREEN}True"
            } else {
                "${ChatColor.RED}False"
            }

            description.add("${ChatColor.YELLOW}Redeemed: $formattedState")
            description.add("${ChatColor.YELLOW}Duped use attempts: ${ChatColor.RED}${bankNote.dupedUseAttempts}")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.TRIPWIRE_HOOK
        }
    }

}