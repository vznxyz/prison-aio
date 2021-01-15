/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.category.template

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.MenuButton
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.warp.WarpsModule
import net.evilblock.prisonaio.module.warp.category.WarpCategory
import net.evilblock.prisonaio.module.warp.category.WarpCategoryHandler
import net.evilblock.prisonaio.module.warp.category.menu.CategoryEditorMenu
import net.evilblock.prisonaio.module.warp.menu.WarpsMenu
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.lang.reflect.Type

class CategoriesMenuTemplate : MenuTemplate<WarpCategory>("categories") {

    override fun getName(): String {
        return WarpsModule.getMenuTitle()
    }

    override fun getAbstractType(): Type {
        return CategoriesMenuTemplate::class.java
    }

    override fun getListEntries(): List<WarpCategory> {
        return WarpCategoryHandler.getCategories().toList()
    }

    override fun createEntryButton(entry: WarpCategory): Button {
        return MenuButton()
            .icon(entry.icon)
            .name(entry.name)
            .lore(arrayListOf<String>().also { desc ->
                if (entry.description.isNotEmpty()) {
                    desc.add("")
                    desc.addAll(entry.description)
                }

                desc.add("")
                desc.add("${ChatColor.YELLOW}${ChatColor.BOLD}Warps")

                if (entry.warps.isEmpty()) {
                    desc.add(" ${ChatColor.GRAY}None")
                } else {
                    for ((index, warp) in entry.warps.withIndex()) {
                        desc.add(" ${warp.getFormattedName()}")

                        if (index >= 4) {
                            break
                        }
                    }

                    val overlap = entry.warps.size - 5
                    if (overlap > 0) {
                        desc.add(" ${ChatColor.GRAY}${ChatColor.ITALIC}and ${NumberUtils.format(overlap)} others...")
                    }
                }

                desc.add("")
                desc.add(Menu.styleAction(ChatColor.GREEN, "LEFT-CLICK", "to view category"))
            })
            .action(ClickType.LEFT) { player ->
                WarpsMenu(entry.warps.toList()).openMenu(player)
            }
    }

    override fun onEditorClose(player: Player) {
        Tasks.delayed(1L) {
            CategoryEditorMenu().openMenu(player)
        }
    }

}