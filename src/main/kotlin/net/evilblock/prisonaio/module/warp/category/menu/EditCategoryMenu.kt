/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.category.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.MenuButton
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.prisonaio.module.warp.category.WarpCategory
import net.evilblock.prisonaio.module.warp.category.WarpCategoryHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class EditCategoryMenu(private val category: WarpCategory) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Category - ${category.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[1] = MenuButton()
                .icon(Material.NETHER_STAR)
                .name("${ChatColor.AQUA}${ChatColor.BOLD}Edit Icon")
                .lore(arrayListOf<String>().also { desc ->
                    desc.add("")
                    desc.addAll(TextSplitter.split(text = "Change the icon of the category, which is rendered in menus."))
                    desc.add("")
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit name"))
                })
                .action(ClickType.LEFT) { player ->
                    SelectItemStackMenu { item ->
                        category.icon = item.clone()

                        Tasks.async {
                            WarpCategoryHandler.saveData()
                        }

                        openMenu(player)
                    }.openMenu(player)
                }

            buttons[3] = MenuButton()
                .icon(Material.NAME_TAG)
                .name("${ChatColor.AQUA}${ChatColor.BOLD}Edit Name")
                .lore(arrayListOf<String>().also { desc ->
                    desc.add("")
                    desc.addAll(TextSplitter.split(text = "Change the display name of the category, which is rendered in menu text."))
                    desc.add("")
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit name"))
                })
                .action(ClickType.LEFT) { player ->
                    InputPrompt()
                        .withText("${ChatColor.GREEN}Please input a new name for the category.")
                        .acceptInput { input ->
                            category.name = ChatColor.translateAlternateColorCodes('&', input)

                            Tasks.async {
                                WarpCategoryHandler.saveData()
                            }

                            openMenu(player)
                        }
                        .start(player)
                }

            buttons[5] = MenuButton()
                .icon(Material.SIGN)
                .name("${ChatColor.AQUA}${ChatColor.BOLD}Edit Description")
                .lore(arrayListOf<String>().also { desc ->
                    desc.add("")
                    desc.addAll(TextSplitter.split(text = "Change the description of the category, which helps users find the warp they're looking for more efficiently."))
                    desc.add("")
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit description"))
                })
                .action(ClickType.LEFT) { player ->
                    EditCategoryDescriptionMenu(category).openMenu(player)
                }

            buttons[7] = MenuButton()
                .icon(Material.ENDER_PEARL)
                .name("${ChatColor.AQUA}${ChatColor.BOLD}Edit Warps")
                .lore(arrayListOf<String>().also { desc ->
                    desc.add("")
                    desc.addAll(TextSplitter.split(text = "Change the warps that are assigned to this category."))
                    desc.add("")
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit warps"))
                })
                .action(ClickType.LEFT) { player ->
                    EditCategoryWarpsMenu(category).openMenu(player)
                }

            for (i in 0 until 9) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                CategoryEditorMenu().openMenu(player)
            }
        }
    }

}