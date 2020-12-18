/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warps.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.warps.Warp
import net.evilblock.prisonaio.module.warps.WarpHandler
import net.evilblock.prisonaio.module.mechanic.economy.menu.SelectCurrencyMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditWarpMenu(private val warp: Warp) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Warp - ${warp.id}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[0] = TeleportButton()
            buttons[2] = EditNameButton()
            buttons[4] = EditDescriptionButton()
            buttons[6] = EditIconButton()
            buttons[8] = EditPriceButton()

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
                WarpEditorMenu().openMenu(player)
            }
        }
    }

    private inner class TeleportButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Teleport"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Teleport to this warp."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to teleport"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENDER_PEARL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                warp.teleport(player)
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
                desc.addAll(TextSplitter.split(text = "Edit this warp's display name, which appears in chat and menu text."))
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
                    .withText("${ChatColor.GREEN}Please input a new display name.")
                    .acceptInput { input ->
                        warp.displayName = ChatColor.translateAlternateColorCodes('&', input)

                        Tasks.async {
                            WarpHandler.saveData()
                        }

                        this@EditWarpMenu.openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class EditDescriptionButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Description"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Edit this warp's description, which appears in menu text."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit description"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}${ChatColor.BOLD}Please input a new description.")
                    .acceptInput { input ->
                        warp.description = input

                        Tasks.async {
                            WarpHandler.saveData()
                        }

                        this@EditWarpMenu.openMenu(player)
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
                desc.addAll(TextSplitter.split(text = ""))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit icon"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectItemStackMenu { selected ->
                    warp.icon = selected.clone()

                    Tasks.async {
                        WarpHandler.saveData()
                    }

                    this@EditWarpMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class EditPriceButton : TexturedHeadButton(texture = Constants.IB_WOOD_NUMBER_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Price"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Edit the price it costs to teleport to this warp."))
                desc.add("")

                if (warp.isPriceSet()) {
                    desc.add("${ChatColor.GRAY}Current Price: ${warp.currency!!.format(warp.price!!)}")
                    desc.add("")
                } else {
                    if (warp.currency != null) {
                        desc.add("${ChatColor.GRAY}Current Currency: ${warp.currency!!.displayName}")
                        desc.add("")
                    }
                }

                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit price"))
                desc.add(styleAction(ChatColor.AQUA, "RIGHT-CLICK", "to edit currency type"))
                desc.add(styleAction(ChatColor.RED, "SHIFT RIGHT-CLICK", "to unset price"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isShiftClick) {
                if (clickType.isRightClick) {
                    warp.currency = null
                    warp.price = null

                    Tasks.async {
                        WarpHandler.saveData()
                    }
                }

                return
            }

            if (clickType.isLeftClick) {
                if (warp.currency == null) {
                    player.sendMessage("${ChatColor.RED}You need to select a currency type first!")
                    return
                }

                NumberPrompt()
                    .withText("${ChatColor.GREEN}Please input a new price in ${warp.currency!!.getName()}.")
                    .acceptInput { number ->
                        warp.price = number

                        Tasks.async {
                            WarpHandler.saveData()
                        }

                        this@EditWarpMenu.openMenu(player)
                    }.start(player)
            } else if (clickType.isRightClick) {
                SelectCurrencyMenu { currency ->
                    warp.currency = currency
                    warp.price = 0

                    Tasks.async {
                        WarpHandler.saveData()
                    }

                    this@EditWarpMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}