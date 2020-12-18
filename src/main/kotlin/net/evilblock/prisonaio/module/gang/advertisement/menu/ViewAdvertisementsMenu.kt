/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.advertisement.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisement
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisementHandler
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisementType
import net.evilblock.prisonaio.module.gang.menu.GangsMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ViewAdvertisementsMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Gang Advertisement"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = BackButton { GangsMenu().openMenu(player) }
            buttons[4] = InfoButton()
            buttons[8] = CreateAdvertisementButton()
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            val advertisements = GangAdvertisementHandler.getAdvertisements().let { set ->
                if (set.isNotEmpty()) {
                    set.filter { !it.isExpired() }.sortedBy { it.createdAt }.reversed()
                } else {
                    set
                }
            }

            for (advertisement in advertisements) {
                buttons[buttons.size] = AdvertisementButton(advertisement)
            }
        }
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class InfoButton : TexturedHeadButton(Constants.IB_ICON_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Gang Advertisement"
        }
    }

    private inner class CreateAdvertisementButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Create Advertisement"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Advertise your gang or that you're looking for a gang to join."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to create advertisement"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                CreateAdvertisementMenu().openMenu(player)
            }
        }
    }

    private inner class AdvertisementButton(private val advertisement: GangAdvertisement) : Button() {
        override fun getName(player: Player): String {
            val name = if (advertisement.type == GangAdvertisementType.PLAYER) {
                Cubed.instance.uuidCache.name(advertisement.createdBy)
            } else {
                GangHandler.getGangById(advertisement.createdBy)!!.name
            }

            return "${advertisement.type.color}${ChatColor.BOLD}${name}'s ${advertisement.type.displayName} Advertisement"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val formattedTime = TimeUtil.formatIntoAbbreviatedString((advertisement.getRemainingTime() / 1000.0).toInt())

                desc.add("${ChatColor.GRAY}(Expires in ${ChatColor.RED}${ChatColor.BOLD}$formattedTime${ChatColor.GRAY})")
                desc.add("")

                if (advertisement.type == GangAdvertisementType.PLAYER) {
                    desc.addAll(TextSplitter.split(text = "I'm looking to join a gang!"))
                } else {
                    desc.addAll(TextSplitter.split(text = "My gang is looking for new members to join!"))
                }

                var hasAccess = false

                if (advertisement.type == GangAdvertisementType.PLAYER) {
                    hasAccess = player.uniqueId == advertisement.createdBy
                } else {
                    val gang = GangHandler.getGangById(advertisement.createdBy)
                    if (gang != null) {
                        hasAccess = gang.isLeader(player.uniqueId)
                    }
                }

                if (hasAccess) {
                    desc.add("")
                    desc.add(styleAction(ChatColor.RED, "RIGHT-CLICK", "to delete advertisement"))
                }
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder
                .copyOf(advertisement.type.icon)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
                .also {
                    if (advertisement.type == GangAdvertisementType.PLAYER) {
                        val skullMeta = it.itemMeta as SkullMeta
                        skullMeta.owner = Cubed.instance.uuidCache.name(advertisement.createdBy)
                        it.itemMeta = skullMeta
                    }
                }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            var hasAccess = false

            if (advertisement.type == GangAdvertisementType.PLAYER) {
                hasAccess = player.uniqueId == advertisement.createdBy
            } else {
                val gang = GangHandler.getGangById(advertisement.createdBy)
                if (gang != null) {
                    hasAccess = gang.isLeader(player.uniqueId)
                }
            }

            if (hasAccess) {
                if (clickType.isRightClick) {
                    ConfirmMenu { confirmed ->
                        if (confirmed) {
                            GangAdvertisementHandler.forgetAdvertisement(advertisement)
                            this@ViewAdvertisementsMenu.openMenu(player)
                        }
                    }.openMenu(player)
                }
            }
        }
    }

}