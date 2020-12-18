/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta

class ToggleEnchantMessagesMenu(
    private val user: User,
    private val returnTo: Menu? = null
) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Toggle Enchant Messages"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0..7) {
                buttons[i] = GlassButton(7)
            }

            buttons[0] = BackButton {
                if (returnTo != null) {
                    returnTo.openMenu(player)
                } else {
                    MainMenu(user).openMenu(player)
                }
            }

            buttons[8] = ToggleAllButton()

            val sortedEnchants = EnchantHandler.getRegisteredEnchants().sortedWith(EnchantHandler.ENCHANT_COMPARATOR)
            for ((index, enchant) in sortedEnchants.withIndex()) {
                buttons[9 + index] = ToggleButton(enchant)
            }
        }
    }

    private inner class ToggleButton(private val enchant: Enchant) : Button() {
        override fun getName(player: Player): String {
            return if (user.settings.isEnchantMessagesDisabled(enchant)) {
                "${ChatColor.RED}${ChatColor.BOLD}${enchant.enchant}"
            } else {
                "${ChatColor.GREEN}${ChatColor.BOLD}${enchant.enchant}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                if (user.settings.isEnchantMessagesDisabled(enchant)) {
                    desc.addAll(TextSplitter.split(text = "You have ${enchant.enchant} messages ${ChatColor.RED}${ChatColor.BOLD}disabled${ChatColor.GRAY}."))
                } else {
                    desc.addAll(TextSplitter.split(text = "You have ${enchant.enchant} messages ${ChatColor.GREEN}${ChatColor.BOLD}enabled${ChatColor.GRAY}."))
                }

                desc.add("")

                if (user.settings.isEnchantMessagesDisabled(enchant)) {
                    desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to enable ${enchant.enchant} messages")
                } else {
                    desc.add("${ChatColor.RED}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.RED}to disable ${enchant.enchant} messages")
                }
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.of(Material.FIREWORK_CHARGE)
                .name(getName(player))
                .setLore(getDescription(player))
                .addFlags(
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_POTION_EFFECTS
                )
                .build()
                .also { item ->
                    val fireworkEffectMeta = item.itemMeta as FireworkEffectMeta
                    fireworkEffectMeta.effect = FireworkEffect.builder().withColor(enchant.getCategory().iconColor).build()
                    item.itemMeta = fireworkEffectMeta
                }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                user.settings.toggleEnchantMessages(enchant)
            }
        }
    }

    private inner class ToggleAllButton : Button() {
        override fun getName(player: Player): String {
            val anyEnabled = EnchantHandler.getRegisteredEnchants().any { !user.settings.isEnchantMessagesDisabled(it) }
            return if (anyEnabled) {
                "${ChatColor.RED}${ChatColor.BOLD}Disable All"
            } else {
                "${ChatColor.GREEN}${ChatColor.BOLD}Enable All"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val anyEnabled = EnchantHandler.getRegisteredEnchants().any { !user.settings.isEnchantMessagesDisabled(it) }
            return if (anyEnabled) {
                TextSplitter.split(text = "Click to disable all enchant messages.")
            } else {
                TextSplitter.split(text = "Click to enable all enchant messages.")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.HOPPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val anyEnabled = EnchantHandler.getRegisteredEnchants().any { !user.settings.isEnchantMessagesDisabled(it) }
            if (anyEnabled) {
                for (enchant in EnchantHandler.getRegisteredEnchants()) {
                    if (!user.settings.isEnchantMessagesDisabled(enchant)) {
                        user.settings.toggleEnchantMessages(enchant)
                    }
                }
            } else {
                for (enchant in EnchantHandler.getRegisteredEnchants()) {
                    if (user.settings.isEnchantMessagesDisabled(enchant)) {
                        user.settings.toggleEnchantMessages(enchant)
                    }
                }
            }
        }
    }

}