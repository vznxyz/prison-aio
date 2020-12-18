/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.advertisement.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisement
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisementHandler
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisementType
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class CreateAdvertisementMenu : Menu() {

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(7)
            }

            buttons[3] = CreateButton(GangAdvertisementType.PLAYER)
            buttons[5] = CreateButton(GangAdvertisementType.GANG)
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                ViewAdvertisementsMenu().openMenu(player)
            }
        }
    }

    private inner class CreateButton(private val type: GangAdvertisementType) : Button() {
        override fun getName(player: Player): String {
            return type.getColoredName() + " Advertisement"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = type.description))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to create ${type.simpleName} advertisement"))
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder
                .copyOf(type.icon)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
                .also {
                    if (type == GangAdvertisementType.PLAYER) {
                        val skullMeta = it.itemMeta as SkullMeta
                        skullMeta.owner = player.name
                        it.itemMeta = skullMeta
                    }
                }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (type == GangAdvertisementType.PLAYER) {
                    if (GangAdvertisementHandler.getAdvertisementByPlayer(player) != null) {
                        player.sendMessage("${ChatColor.RED}You already have an existing player advertisement!")
                        return
                    }

                    val advertisement = GangAdvertisement(GangAdvertisementType.PLAYER, player.uniqueId)
                    GangAdvertisementHandler.trackAdvertisement(advertisement)

                    player.sendMessage("${ChatColor.GREEN}Your advertisement has been posted!")

                    ViewAdvertisementsMenu().openMenu(player)
                } else {
                    val gang = GangHandler.getGangByPlayer(player)
                    if (gang == null) {
                        player.sendMessage("${ChatColor.RED}You don't belong to a gang!")
                        return
                    }

                    if (!gang.isLeader(player.uniqueId)) {
                        player.sendMessage("${ChatColor.RED}Only the leader can post an advertisement for your gang!")
                        return
                    }

                    if (GangAdvertisementHandler.getAdvertisementByGang(gang) != null) {
                        player.sendMessage("${ChatColor.RED}Your gang already has an existing advertisement!")
                        return
                    }

                    val advertisement = GangAdvertisement(GangAdvertisementType.GANG, gang.uuid)
                    GangAdvertisementHandler.trackAdvertisement(advertisement)

                    player.sendMessage("${ChatColor.GREEN}Your advertisement has been posted!")

                    ViewAdvertisementsMenu().openMenu(player)
                }
            }
        }
    }

}