/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.creation

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.util.concurrent.TimeUnit

class SelectDurationMenu(private val select: (Pair<Long, Duration>) -> Unit) : Menu() {

    override fun getTitle(player: Player): String {
        return "How long should it last?"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[1] = DurationButton(5_000L, Duration(TimeUnit.HOURS.toMillis(1L)), CLOCK_RED)
            buttons[3] = DurationButton(15_000L, Duration(TimeUnit.HOURS.toMillis(3L)), CLOCK_YELLOW)
            buttons[5] = DurationButton(45_000L, Duration(TimeUnit.HOURS.toMillis(6L)), CLOCK_GREEN)
            buttons[7] = DurationButton(200_000L, Duration(TimeUnit.HOURS.toMillis(24L)), CLOCK_BLUE)

            for (i in 0 until 9) {
                if (!buttons.contains(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    private inner class DurationButton(private val cost: Long, private val duration: Duration, texture: String) : TexturedHeadButton(texture) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${TimeUtil.formatIntoDetailedString((duration.get() / 1000.0).toInt())}"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(text = "Click to select this duration, which has a fee of ${Formats.formatTokens(cost)}${ChatColor.GRAY}.")
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val user = UserHandler.getUser(player.uniqueId)
            if (!user.hasTokenBalance(cost)) {
                player.sendMessage("${ChatColor.RED}You don't have enough tokens for that duration's fee!")
                return
            }

            select.invoke(Pair(cost, duration))
        }
    }

    companion object {
        private const val CLOCK_RED = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjEyZjc4N2M1NGRkODlkMTI2OThkZDE3YjU2NTEyOTRjZmI4MDE3ZDZhZDRkMjZlZTZhOTFjZjFkMGMxYzQifX19"
        private const val CLOCK_YELLOW = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmExZjM0MDdiNzExZWVmODRmZTVhMWY0MjQyNTE0ZWQ2ZGEwZjI5NzVjOGE1OTA4ZjU3MzViNDYxNmUzMzk2ZSJ9fX0="
        private const val CLOCK_GREEN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JkNDdkZDdjMzMzNmU3NWE2NjM5MWNkZjljOTM1ZmFlY2E4Y2UzOGFlMjJhMWIyNzg5NWUzMGI0NTI0NWE4In19fQ=="
        private const val CLOCK_BLUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E4YTYwNDkzOTE3N2ZkNDVkZjE1ZjM1MWYzM2YxMzRhM2YwNTE4NjgyM2RkM2FlZDU3YzlmYjIyOGQ0MTcifX19"
    }

}