package net.evilblock.prisonaio.module.kit.menu.template

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.KitHandler
import net.evilblock.prisonaio.module.kit.menu.KitEditorMenu
import net.evilblock.prisonaio.module.kit.KitsModule
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

class PublicKitsTemplate : MenuTemplate<Kit>(id = "kits") {

    override fun getName(): String {
        return KitsModule.getMenuTitle()
    }

    override fun createEntryButton(entry: Kit): Button {
        return KitButton(entry)
    }

    override fun getListEntries(): List<Kit> {
        return KitHandler.getKits().filter { it.public }
    }

    override fun onEditorClose(player: Player) {
        Tasks.async {
            KitHandler.saveData()
        }

        Tasks.delayed(1L) {
            KitEditorMenu().openMenu(player)
        }
    }

    override fun getAbstractType(): Type {
        return PublicKitsTemplate::class.java
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

                if (kit.isCooldownSet() && kit.isOnCooldown(player)) {
                    val formattedCooldown = TimeUtil.formatIntoAbbreviatedString((kit.getRemainingCooldown(player) / 1000.0).toInt())
                        .split(" ")
                        .joinToString { ChatColor.BOLD.toString() + it }

                    desc.addAll(TextSplitter.split(text = "You can't redeem this kit again for another $formattedCooldown${ChatColor.RED}!", linePrefix = ChatColor.RED.toString()))
                } else {
                    desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to redeem kit"))
                }
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder
                .copyOf(kit.icon)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (kit.requiresPermission) {
                    if (!kit.hasPermission(player)) {
                        player.sendMessage("${ChatColor.RED}You don't have permission to use that kit!")
                        return
                    }
                }

                if (kit.isCooldownSet() && kit.isOnCooldown(player)) {
                    val formattedCooldown = TimeUtil.formatIntoDetailedString((kit.getRemainingCooldown(player) / 1000.0).toInt())
                    player.sendMessage("${ChatColor.RED}You're on cooldown for another ${ChatColor.BOLD}$formattedCooldown${ChatColor.RED}!")
                    return
                }

                kit.giveItems(player)

                if (kit.isCooldownSet()) {
                    kit.applyCooldown(player)
                }
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