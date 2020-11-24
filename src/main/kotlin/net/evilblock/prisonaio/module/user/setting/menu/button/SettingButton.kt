/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class SettingButton(private val user: User, private val setting: UserSetting) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        val lore = arrayListOf<String>()
        lore.add("")
        lore.addAll(TextSplitter.split(length = 40, text = setting.getDescription(), linePrefix = "${ChatColor.GRAY}"))
        lore.add("")

        for (option in setting.getOptions<UserSettingOption>()) {
            val builder = StringBuilder()

            builder.append(if (user.settings.getSettingOption(setting) == option) {
                " ${ChatColor.BLUE}${ChatColor.BOLD}» ${ChatColor.GREEN}${option.getName()}"
            } else {
                "    ${ChatColor.YELLOW}${option.getName()}"
            })

            if (setting.getDefaultOption() == option) {
                builder.append(" ${ChatColor.GRAY}(Default)")
            }

            lore.add(builder.toString())
        }

        lore.add("")
        lore.add("${ChatColor.BLUE}${Constants.ARROW_UP} ${ChatColor.YELLOW}${ChatColor.BOLD}LEFT-CLICK")
        lore.add("${ChatColor.BLUE}${Constants.ARROW_DOWN} ${ChatColor.YELLOW}${ChatColor.BOLD}RIGHT-CLICK")

        val item = setting.icon.invoke(user.settings.getSettingOption(setting))

        return ItemBuilder.copyOf(item)
            .name("${ChatColor.YELLOW}${ChatColor.BOLD}${setting.getDisplayName()}")
            .setLore(lore)
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            user.settings.updateSettingOption(setting, setting.getPreviousValue(user.settings.getSettingOption(setting)))
            setting.onUpdate.invoke(user, user.settings.getSettingOption(setting))
        } else if (clickType.isRightClick) {
            user.settings.updateSettingOption(setting, setting.getNextValue(user.settings.getSettingOption(setting)))
            setting.onUpdate.invoke(user, user.settings.getSettingOption(setting))
        }
    }

}