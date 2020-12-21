/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.kit.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.menu.template.menu.EditTemplateLayoutMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitHandler
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class KitEditorMenu : PaginatedMenu() {

    init {
        autoUpdate = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Kit Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[2] = AddKitButton()
            buttons[4] = EditPublicKitsTemplateButton()

            for (i in 9..17) {
                buttons[i] = GlassButton(0)
            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (kit in KitHandler.getKits()) {
                buttons[buttons.size] = KitButton(kit)
            }
        }
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class AddKitButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Kit"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Create a new kit by completing the setup procedure."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to create a new kit"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText(EzPrompt.IDENTIFIER_PROMPT)
                    .charLimit(16)
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .acceptInput { input ->
                        if (KitHandler.getKitById(input) != null) {
                            player.sendMessage("${ChatColor.RED}A kit's ID must be unique, and a kit with the ID `$input` already exists.")
                            return@acceptInput
                        }

                        val kit = Kit(input)

                        KitHandler.trackKit(kit)
                        KitHandler.saveData()

                        openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class EditPublicKitsTemplateButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}Edit Public Kits Template"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = ""))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to edit template"))
                desc.add(styleAction(ChatColor.GREEN, "RIGHT-CLICK", "to reset template"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ITEM_FRAME
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditTemplateLayoutMenu(KitHandler.getMenuTemplate()).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu("Reset template?") { confirmed ->
                    if (confirmed) {
                        KitHandler.resetMenuTemplate()

                        Tasks.async {
                            KitHandler.saveData()
                        }

                        this@KitEditorMenu.openMenu(player)
                    }
                }.openMenu(player)
            }
        }
    }

    private inner class KitButton(private val kit: Kit) : Button() {
        override fun getName(player: Player): String {
            return buildString {
                append(kit.name)

                if (kit.isCooldownSet()) {
                    val formattedCooldown = TimeUtil.formatIntoAbbreviatedString((kit.cooldownDuration!!.get() / 1000.0).toInt())
                    append(" ${ChatColor.GRAY}(${ChatColor.RED}${ChatColor.BOLD}$formattedCooldown ${ChatColor.GRAY}Cooldown)")
                } else {
                    append(" ${ChatColor.GRAY}(No Cooldown)")
                }
            }
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                val public = if (kit.public) {
                    ChatColor.GREEN.toString() + "yes"
                } else {
                    ChatColor.RED.toString() + "no"
                }

                val requirePermission = if (kit.requiresPermission) {
                    ChatColor.GREEN.toString() + "yes" + ChatColor.GRAY.toString() + ", " + kit.getPermission()
                } else {
                    ChatColor.RED.toString() + "no"
                }

                desc.add("${ChatColor.GRAY}(ID: ${kit.id})")
                desc.add("${ChatColor.GRAY}(Public: ${public}${ChatColor.GRAY})")
                desc.add("${ChatColor.GRAY}(Permission: ${requirePermission}${ChatColor.GRAY})")
                desc.add("")
                desc.add("${ChatColor.YELLOW}This kit contains...")

                if (kit.items.isEmpty()) {
                    desc.add("  ${ChatColor.GRAY}No items")
                } else {
                    for ((index, item) in kit.items.withIndex()) {
                        desc.add("  ${formatItemName(item)}")

                        if (index >= 4) {
                            break
                        }
                    }

                    if (kit.items.size > 5) {
                        val overlap = kit.items.size - 5
                        desc.add("  ${ChatColor.GRAY}and $overlap more ${TextUtil.pluralize(overlap, "item", "items")}...")
                    }
                }

                desc.add("")

                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit kit")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete kit")
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(kit.icon).name(getName(player)).setLore(getDescription(player)).build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditKitMenu(kit).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        KitHandler.forgetKit(kit)
                        KitHandler.saveData()
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

    companion object {
        private fun formatItemName(itemStack: ItemStack): String {
            val name = if (itemStack.itemMeta.hasDisplayName()) {
                itemStack.itemMeta.displayName
            } else {
                "${ChatColor.GRAY}${ItemUtils.getName(itemStack)}"
            }

            return "${ChatColor.GRAY}${itemStack.amount}x $name"
        }
    }

}