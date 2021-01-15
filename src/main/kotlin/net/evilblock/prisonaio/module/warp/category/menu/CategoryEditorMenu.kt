/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.category.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.MenuButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.menu.template.menu.EditTemplateLayoutMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.prisonaio.module.warp.category.WarpCategory
import net.evilblock.prisonaio.module.warp.category.WarpCategoryHandler
import net.evilblock.prisonaio.module.warp.category.template.CategoriesMenuTemplate
import net.evilblock.prisonaio.module.warp.category.template.CategoriesTemplateHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class CategoryEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Warp Category Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[2] = MenuButton()
                .texturedIcon(Constants.GREEN_PLUS_TEXTURE)
                .name("${ChatColor.AQUA}${ChatColor.BOLD}Create New Category")
                .lore(arrayListOf<String>().also { desc ->
                    desc.add("")
                    desc.addAll(TextSplitter.split(text = "Create a new category by completing the setup procedure."))
                    desc.add("")
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to create a new category"))
                })
                .action(ClickType.LEFT) { player ->
                    InputPrompt()
                        .withText("${ChatColor.GREEN}Please input a unique ID. ${ChatColor.GRAY}(Limit of 16 characters)")
                        .withLimit(16)
                        .acceptInput { input ->
                            if (WarpCategoryHandler.getCategoryById(input) != null) {
                                player.sendMessage("${ChatColor.RED}A category by that ID already exists!")
                                return@acceptInput
                            }

                            val category = WarpCategory(input)
                            WarpCategoryHandler.trackCategory(category)

                            Tasks.async {
                                WarpCategoryHandler.saveData()
                            }

                            EditCategoryMenu(category).openMenu(player)
                        }
                        .start(player)
                }

            buttons[4] = MenuButton()
                .icon(Material.ITEM_FRAME)
                .name("${ChatColor.AQUA}${ChatColor.BOLD}Edit Template")
                .lore(arrayListOf<String>().also { desc ->
                    desc.add("")
                    desc.addAll(TextSplitter.split(text = "Change the menu template of the categories menu."))
                    desc.add("")
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit template"))
                    desc.add(styleAction(ChatColor.AQUA, "RIGHT-CLICK", "to reset template"))
                })
                .action(ClickType.LEFT) { player ->
                    EditTemplateLayoutMenu(CategoriesTemplateHandler.template).openMenu(player)
                }
                .action(ClickType.RIGHT) { player ->
                    ConfirmMenu { confirmed ->
                        if (confirmed) {
                            CategoriesTemplateHandler.template = CategoriesMenuTemplate()

                            Tasks.async {
                                CategoriesTemplateHandler.saveData()
                            }

                            openMenu(player)
                        }
                    }.openMenu(player)
                }

            for (i in 9..17) {
                buttons[i] = GlassButton(0)
            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (category in WarpCategoryHandler.getCategories()) {
                buttons[buttons.size] = MenuButton()
                    .icon(Material.CHEST)
                    .name(category.name)
                    .lore(arrayListOf<String>().also { desc ->
                        desc.add("")
                        desc.add("${ChatColor.YELLOW}${ChatColor.BOLD}Assigned Warps")

                        if (category.warps.isEmpty()) {
                            desc.add(" ${ChatColor.GRAY}None")
                        } else {
                            for ((index, warp) in category.warps.withIndex()) {
                                desc.add(" ${warp.getFormattedName()}")

                                if (index >= 4) {
                                    break
                                }
                            }

                            val overlap = category.warps.size - 5
                            if (overlap > 0) {
                                desc.add(" ${ChatColor.GRAY}${ChatColor.ITALIC}and ${NumberUtils.format(overlap)} others...")
                            }
                        }

                        desc.add("")
                        desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit category"))
                        desc.add(styleAction(ChatColor.RED, "RIGHT-CLICK", "to delete category"))
                    })
                    .action(ClickType.LEFT) { player ->
                        EditCategoryMenu(category).openMenu(player)
                    }
                    .action(ClickType.RIGHT) { player ->
                        ConfirmMenu { confirmed ->
                            if (confirmed) {
                                WarpCategoryHandler.forgetCategory(category)

                                Tasks.async {
                                    WarpCategoryHandler.saveData()
                                }

                                openMenu(player)
                            }
                        }.openMenu(player)
                    }
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

}