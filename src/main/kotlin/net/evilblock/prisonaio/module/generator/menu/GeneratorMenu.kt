/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.InventoryUtils
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.generator.GeneratorType
import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifierUtils
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class GeneratorMenu(private val generator: Generator) : Menu() {

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return "${generator.getGeneratorType().getProperName()} (Level ${NumberUtils.format(generator.level)})"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in BORDER_SLOTS) {
                buttons[i] = GlassButton(7)
            }

            buttons[4] = GeneratorButton()

            if (generator.modifierStorage.any { it != null }) {
                for ((index, itemStack) in generator.modifierStorage.withIndex()) {
                    if (itemStack != null) {
                        buttons[MODIFIER_SLOTS[index]] = ModifierItemButton(itemStack)
                    }
                }
            }

            if (generator.hasItemStorage()) {
                val itemsStorage = generator.getItemStorage()
                for ((index, itemStack) in itemsStorage.withIndex()) {
                    buttons[STORAGE_SLOTS[index]] = ItemStorageButton(itemStack)
                }
            }

            val nextLevel = generator.getNextLevel()
            if (nextLevel != null) {
                buttons[16] = UpgradeButton(nextLevel)
            } else {
                buttons[16] = MaxLevelButton()
            }

            buttons[43] = DestroyButton()
        }
    }

    override fun getAutoUpdateTicks(): Long {
        return 1000L
    }

    override fun acceptsInsertedItem(player: Player, itemStack: ItemStack, slot: Int): Boolean {
        if (!MODIFIER_SLOTS.contains(slot)) {
            return false
        }

        val modifier = GeneratorModifierUtils.extractModifierFromItemStack(itemStack) ?: return false
        if (!generator.isModifierCompatible(modifier.type)) {
            player.sendMessage("${ChatColor.RED}That modifier item is not compatible with this Generator!")
            return false
        }

        val notInserted = InventoryUtils.addAmountToInventory(generator.modifierStorage, itemStack, itemStack.amount)
        if (notInserted != null) {
            player.inventory.addItem(notInserted)
            player.updateInventory()
        }

        return true
    }

    inner class UpgradeButton(private val nextLevel: GeneratorBuildLevel) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Upgrade ${generator.getGeneratorType().getProperName()} ${ChatColor.GRAY}(Lvl ${generator.level} -> ${nextLevel.number})"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->

                desc.add("${ChatColor.GRAY}Price: ${Formats.formatTokens(nextLevel.cost)}")
                desc.add("${ChatColor.GRAY}Build Time: ${ChatColor.RED}${ChatColor.BOLD}${TimeUtil.formatIntoAbbreviatedString(nextLevel.buildTime)}")
                desc.add("")

                if (generator.getGeneratorType() == GeneratorType.CORE) {
                    desc.addAll(TextSplitter.split(text = "You won't be able to upgrade other generators while your Core is building."))
                    desc.add("")
                }

                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to purchase upgrade")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 5
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                val core = GeneratorHandler.getCoreByPlot(generator.plotId)
                if (core == null) {
                    player.sendMessage("${ChatColor.RED}You need to place a Core on your plot first!")
                    return
                }

                if (!core.build.finished) {
                    player.sendMessage("${ChatColor.RED}You must wait for your Core to finish building!")
                    return
                }

                ConfirmMenu { confirmed ->
                    if (!generator.build.finished) {
                        player.sendMessage("${ChatColor.RED}You can't upgrade your ${generator.getGeneratorType().getProperName()} until it's finished building!")
                        return@ConfirmMenu
                    }

                    if (!core.build.finished) {
                        player.sendMessage("${ChatColor.RED}You must wait for your Core to finish building!")
                        return@ConfirmMenu
                    }

                    if (confirmed) {
                        val user = UserHandler.getUser(player)
                        if (!user.hasTokenBalance(nextLevel.cost)) {
                            player.sendMessage("${ChatColor.RED}You can't afford to upgrade your ${generator.getGeneratorType().getProperName()}!")
                            return@ConfirmMenu
                        }

                        user.subtractTokensBalance(nextLevel.cost)

                        generator.level++
                        generator.startBuild()
                    }
                }.openMenu(player)
            }
        }
    }

    inner class DestroyButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Destroy ${generator.getGeneratorType().getProperName()}"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Remove this ${generator.getGeneratorType().getProperName()} from your plot. It and all of its contents will be lost forever."))
                desc.add("")

                if (generator.getGeneratorType() == GeneratorType.CORE) {
                    desc.add("${ChatColor.RED}${ChatColor.BOLD}Warning")
                    desc.addAll(TextSplitter.split(text = "Destroying your Core will destroy ALL of the generators on your plot!"))
                    desc.add("")
                }

                desc.add("${ChatColor.RED}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.RED}to destroy generator")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 14
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        generator.destroy()
                        GeneratorHandler.forgetGenerator(generator)
                    }
                }.openMenu(player)
            }
        }
    }

    inner class MaxLevelButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Max Level Reached"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "This ${generator.getGeneratorType().getProperName()} has reached it's max level!"))
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 2
        }
    }

    inner class GeneratorButton : Button() {
        override fun getName(player: Player): String {
            return "${generator.getGeneratorType().getColoredName()} ${ChatColor.GRAY}(Level ${NumberUtils.format(generator.level)})"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = generator.getGeneratorType().description))
                desc.add("")
                desc.addAll(generator.renderInformation())
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(generator.getGeneratorType().icon)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
                .also {
                    GlowEnchantment.addGlow(it)
                }
        }
    }

    inner class ModifierItemButton(private val itemStack: ItemStack) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return itemStack.clone()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (player.inventory.firstEmpty() == -1) {
                player.sendMessage("${ChatColor.RED}You need a free inventory space to do that!")
                return
            }

            when (clickType) {
                ClickType.LEFT -> {
                    val removed = generator.removeModifierItem(itemStack, itemStack.amount)
                    if (removed > 0) {
                        player.inventory.addItem(ItemBuilder.copyOf(itemStack).amount(removed).build())
                        player.updateInventory()
                    }
                }
                ClickType.RIGHT -> {
                    var removed = 0
                    if (itemStack.amount > 1) {
                        removed = generator.removeModifierItem(itemStack, itemStack.amount / 2)
                    } else if (itemStack.amount == 1) {
                        removed = generator.removeModifierItem(itemStack, 1)
                    }

                    if (removed > 0) {
                        player.inventory.addItem(ItemBuilder.copyOf(itemStack).amount(removed).build())
                        player.updateInventory()
                    }
                }
                else -> {

                }
            }
        }
    }

    inner class ItemStorageButton(private val itemStack: ItemStack) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return itemStack.clone()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                generator.removeItemFromStorage(player, itemStack)
            }
        }
    }

    companion object {
        private val BORDER_SLOTS = arrayListOf(9, 11, 15, 17, 18, 24, 25, 26, 27, 33, 34, 35, 36, 37, 38, 42, 44, 46).also {
            it.addAll(0..8)
            it.addAll(45..53)

            it.add(20)
            it.add(29)
        }

        val MODIFIER_SLOTS = listOf(10, 19, 28)

        val STORAGE_SLOTS = listOf(
            12, 13, 14,
            21, 22, 23,
            30, 31, 32,
            39, 40, 41
        )
    }

}