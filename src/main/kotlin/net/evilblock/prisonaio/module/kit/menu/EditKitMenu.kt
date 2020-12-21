/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.kit.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitHandler
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.DurationPrompt
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class EditKitMenu(internal val kit: Kit) : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Kit - ${kit.name}"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[1] = EditNameButton()
            buttons[2] = EditIconButton()
            buttons[3] = EditCooldownButton()
            buttons[4] = ToggleRequiresPermissionButton()
            buttons[5] = TogglePublicButton()

            for (i in 9..17) {
                buttons[i] = GlassButton(0)
            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (item in kit.items) {
                buttons[buttons.size] = ItemButton(item)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        return itemStack.type != Material.AIR && kit.items.add(itemStack.clone())
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                KitEditorMenu().openMenu(player)
            }
        }
    }

    private inner class ItemButton(private val itemStack: ItemStack) : Button() {
        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                if (itemStack.hasItemMeta()) {
                    if (itemStack.itemMeta.hasLore()) {
                        desc.addAll(itemStack.itemMeta.lore)
                        desc.add("${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}------------------")
                    }
                }

                desc.add("")
                desc.add(styleAction(ChatColor.RED, "RIGHT-CLICK", "to delete item"))
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder
                .copyOf(itemStack)
                .setLore(getDescription(player))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        kit.items.remove(itemStack)

                        Tasks.async {
                            KitHandler.saveData()
                        }
                    }
                }.openMenu(player)
            }
        }
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The name is how this kit appears chat and menu text."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit name"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a new name for the kit. ${ChatColor.GRAY}(Colors supported)")
                    .acceptInput { input ->
                        kit.name = ChatColor.translateAlternateColorCodes('&', input)

                        Tasks.async {
                            KitHandler.saveData()
                        }

                        this@EditKitMenu.openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class EditIconButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Icon"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The icon is how this kit is represented in menus."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit icon"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectItemStackMenu { itemStack ->
                    kit.icon = itemStack.clone()

                    Tasks.async {
                        KitHandler.saveData()
                    }

                    this@EditKitMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class EditCooldownButton : Button() {
        override fun getName(player: Player): String {
            val value = if (kit.isCooldownSet()) {
                TimeUtil.formatIntoAbbreviatedString((kit.cooldownDuration!!.get() / 1000.0).toInt())
            } else {
                "None"
            }

            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Cooldown ${ChatColor.GRAY}($value)"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Apply a cooldown to players redeeming this kit."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to apply cooldown"))
                desc.add(styleAction(ChatColor.RED, "RIGHT-CLICK", "to unset cooldown"))
                desc.add(styleAction(ChatColor.AQUA, "SHIFT LEFT-CLICK", "to reset cooldowns"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isShiftClick && clickType.isLeftClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        kit.cooldowns.clear()

                        Tasks.async {
                            KitHandler.saveData()
                        }
                    }

                    this@EditKitMenu.openMenu(player)
                }.openMenu(player)

                return
            }

            if (clickType.isLeftClick) {
                DurationPrompt { duration ->
                    if (duration < 0) {
                        player.sendMessage("${ChatColor.RED}Invalid duration! Must be more than 0!")
                        return@DurationPrompt
                    }

                    kit.cooldownDuration = Duration(duration)

                    Tasks.async {
                        KitHandler.saveData()
                    }

                    this@EditKitMenu.openMenu(player)
                }.start(player)
            } else if (clickType.isRightClick) {
                kit.cooldownDuration = null

                Tasks.async {
                    KitHandler.saveData()
                }

                this@EditKitMenu.openMenu(player)
            }
        }
    }

    private inner class ToggleRequiresPermissionButton : Button() {
        override fun getName(player: Player): String {
            val state = if (kit.requiresPermission) {
                ChatColor.GREEN.toString() + "yes"
            } else {
                ChatColor.RED.toString() + "no"
            }

            return "${ChatColor.AQUA}${ChatColor.BOLD}Toggle Requires Permission${ChatColor.WHITE}: $state"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("${ChatColor.GRAY}(kits.redeem.${kit.id.toLowerCase()})")
                desc.add("")
                desc.addAll(TextSplitter.split(text = "If this kit requires a permission to be redeemed."))
                desc.add("")

                if (kit.requiresPermission) {
                    desc.add(styleAction(ChatColor.RED, "LEFT-CLICK", "to disable require permission"))
                } else {
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to enable require permission"))
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return if (kit.requiresPermission) {
                13
            } else {
                14
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                kit.requiresPermission = !kit.requiresPermission

                Tasks.async {
                    KitHandler.saveData()
                }
            }
        }
    }

    private inner class TogglePublicButton : Button() {
        override fun getName(player: Player): String {
            val state = if (kit.public) {
                ChatColor.GREEN.toString() + "yes"
            } else {
                ChatColor.RED.toString() + "no"
            }

            return "${ChatColor.AQUA}${ChatColor.BOLD}Toggle Public${ChatColor.WHITE}: $state"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "If this kit is visible to normal players."))
                desc.add("")

                if (kit.public) {
                    desc.add(styleAction(ChatColor.RED, "LEFT-CLICK", "to make hidden"))
                } else {
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to make public"))
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return if (kit.public) {
                13
            } else {
                14
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                kit.public = !kit.public

                Tasks.async {
                    KitHandler.saveData()
                }
            }
        }
    }

}