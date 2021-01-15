/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.bounty.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.MenuButton
import net.evilblock.cubed.menu.pagination.PageButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.combat.bounty.BountyHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.MainMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.time.Instant
import java.util.*

class BountiesMenu : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "${ChatColor.RED}${ChatColor.BOLD}Bounties"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            if (page == 1) {
                buttons[0] = BackButton {
                    MainMenu(UserHandler.getUser(player)).openMenu(player)
                }
            } else {
                buttons[0] = PageButton(-1, this)
            }

            buttons[8] = PageButton(1, this)

            buttons[4] = MenuButton()
                .icon(Material.SKULL_ITEM)
                .name("${ChatColor.RED}${ChatColor.BOLD}Bounties")

            buttons[8] = MenuButton()
                .icon(ItemUtils.getPlayerHeadItem(player.name))
                .name("${ChatColor.AQUA}${ChatColor.BOLD}My Bounties")
                .action(ClickType.LEFT) {

                }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (bounty in BountyHandler.getBounties()) {
                buttons[buttons.size] = MenuButton()
                    .name {
                        "${ChatColor.RED}${ChatColor.BOLD}${bounty.getTargetUsername()} ${ChatColor.RESET}${bounty.getFormattedValue()}"
                    }
                    .lore {
                        arrayListOf<String>().also { desc ->
                            desc.add("")
                            desc.add("${ChatColor.GRAY}Created By: ${ChatColor.GREEN}${bounty.getCreatorUsername()}")
                            desc.add("${ChatColor.GRAY}Created At: ${ChatColor.GREEN}${TimeUtil.formatIntoDateString(Date.from(Instant.ofEpochMilli(bounty.createdAt)))}")

                            val contributions = bounty.getContributions()
                            if (contributions.size > 1) {
                                desc.add("")
                                desc.add("${ChatColor.YELLOW}${ChatColor.BOLD}Contributions")

                                for (contribution in contributions) {
                                    desc.add(buildString {
                                        append("${ChatColor.GREEN}${contribution.getCreatorUsername()}")
                                        append(" ${ChatColor.RESET}${contribution.getFormattedValue(bounty.currency)}")
                                        append(" ${ChatColor.GRAY}(${TimeUtil.formatIntoDateString(Date.from(Instant.ofEpochMilli(contribution.createdAt)))})")
                                    })
                                }
                            }

                            desc.add("")
                            desc.addAll(TextSplitter.split(text = "Kill ${bounty.getTargetUsername()} in PvP to receive the bounty value!"))
                            desc.add("")
                            desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to contribute to bounty"))
                        }
                    }
            }
        }
    }

}