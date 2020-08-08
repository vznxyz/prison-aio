/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.bukkit.ColorUtil
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta

class UserNicknameMenu(private val user: User) : Menu() {

    companion object {
        private val BLACK_SLOTS = listOf(
            0, 1, 2, 3, 5, 6, 7, 8,
            9, 17,
            18, 26,
            27, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
        )

        private val BUTTON_SLOTS = listOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
        )
    }

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Nickname Settings"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in BLACK_SLOTS) {
            buttons[i] = GlassButton(15)
        }

        buttons[4] = NicknameButton()

        var buttonIndex = 0

        for (color in UserHandler.NICKNAME_COLORS) {
            buttons[BUTTON_SLOTS[buttonIndex++]] = ColorButton(color)
        }

        for (style in UserHandler.NICKNAME_STYLES) {
            buttons[BUTTON_SLOTS[buttonIndex++]] = StyleButton(style)
        }

        return buttons
    }

    private inner class NicknameButton : Button() {
        override fun getName(player: Player): String {
            return user.getFormattedUsername(player)
        }

        override fun getDescription(player: Player): List<String> {
            return listOf("${ChatColor.GRAY}Right-click to ${ChatColor.RED}${ChatColor.BOLD}reset ${ChatColor.GRAY}your nickname.")
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isRightClick) {
                user.nicknameColor = null
                user.nicknameStyle = null
                user.requiresSave = true
            }
        }
    }

    private inner class ColorButton(private val color: ChatColor) : Button() {
        override fun getName(player: Player): String {
            val builder = StringBuilder(color.toString())
                .append(ChatColor.BOLD.toString())

            if (!user.hasNicknameColor(color, player)) {
                builder.append(ChatColor.STRIKETHROUGH.toString())
            }

            return builder.append(Formats.capitalizeFully(color.name.replace("_", " "))).toString()
        }

        override fun getDescription(player: Player): List<String> {
            return when {
                user.nicknameColor == color -> {
                    listOf("${ChatColor.GRAY}You have this color enabled!")
                }
                user.hasNicknameColor(color, player) -> {
                    listOf("${ChatColor.GRAY}Click to enable this color.")
                }
                else -> {
                    listOf("${ChatColor.GRAY}You don't have this color unlocked.")
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return ColorUtil.toWoolData(color).toByte()
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            if (user.nicknameColor == color) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
            }
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && user.hasNicknameColor(color, player)) {
                user.nicknameColor = color
                user.requiresSave = true
            }
        }
    }

    private inner class StyleButton(private val style: ChatColor) : Button() {
        override fun getName(player: Player): String {
            val builder = StringBuilder(ChatColor.GRAY.toString())
                .append(ChatColor.BOLD.toString())

            if (!user.hasNicknameColor(style, player)) {
                builder.append(ChatColor.STRIKETHROUGH.toString())
            }

            return builder.append(Formats.capitalizeFully(style.name.replace("_", " "))).toString()
        }

        override fun getDescription(player: Player): List<String> {
            return when {
                user.nicknameStyle == style -> {
                    listOf("${ChatColor.GRAY}You have this style enabled!")
                }
                user.hasNicknameColor(style, player) -> {
                    listOf("${ChatColor.GRAY}Click to enable this style.")
                }
                else -> {
                    listOf("${ChatColor.GRAY}You don't have this style unlocked.")
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            if (user.nicknameStyle == style) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
            }
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && user.hasNicknameColor(style, player)) {
                user.nicknameStyle = style
                user.requiresSave = true
            }
        }
    }

}