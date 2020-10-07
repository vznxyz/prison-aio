/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.nms.NBTUtil
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class AbilityArmorSet(
    val setId: String,
    val setName: String,
    private val helmet: ItemStack,
    private val chestplate: ItemStack,
    private val leggings: ItemStack,
    private val boots: ItemStack
) {

    abstract fun getSetDescription(): String

    open fun getShortSetDescription(): String? {
        return null
    }

    open fun getInheritedArmorSets(): List<AbilityArmorSet> {
        return emptyList()
    }

    fun getHelmet(): ItemStack {
        val item = ItemBuilder.copyOf(helmet)
            .name("$setName Helmet")
            .setLore(getArmorPieceLore())
            .build()

        GlowEnchantment.addGlow(item)

        return attachNbt(item)
    }

    fun getChestplate(): ItemStack {
        val item = ItemBuilder.copyOf(chestplate)
            .name("$setName Chestplate")
            .setLore(getArmorPieceLore())
            .build()

        GlowEnchantment.addGlow(item)

        return attachNbt(item)
    }

    fun getLeggings(): ItemStack {
        val item = ItemBuilder.copyOf(leggings)
            .name("$setName Leggings")
            .setLore(getArmorPieceLore())
            .build()

        GlowEnchantment.addGlow(item)

        return attachNbt(item)
    }

    fun getBoots(): ItemStack {
        val item = ItemBuilder.copyOf(boots)
            .name("$setName Boots")
            .setLore(getArmorPieceLore())
            .build()

        GlowEnchantment.addGlow(item)

        return attachNbt(item)
    }

    fun hasSetEquipped(player: Player): Boolean {
        return player.inventory.helmet != null && isArmorPiece(player.inventory.helmet, true)
                && player.inventory.chestplate != null && isArmorPiece(player.inventory.chestplate, true)
                && player.inventory.leggings != null && isArmorPiece(player.inventory.leggings, true)
                && player.inventory.boots != null && isArmorPiece(player.inventory.boots, true)
    }

    fun getArmorPieceLore(): List<String> {
        val lore = arrayListOf<String>()

        lore.add("${ChatColor.GRAY}Part of the ${ChatColor.stripColor(setName)} Armor Set")
        lore.add("")
        lore.addAll(TextSplitter.split(text = getSetDescription(), linePrefix = ChatColor.GRAY.toString()))

        val inheritedAbilities = getInheritedArmorSets()
        if (inheritedAbilities.isNotEmpty()) {
            lore.add("")
            lore.add("${ChatColor.getLastColors(setName)}Inherited Abilities")

            for (inherited in inheritedAbilities) {
                lore.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${inherited.setName}${ChatColor.GRAY}: ${inherited.getShortSetDescription()}")
            }
        }

        return lore
    }

    fun isArmorPiece(itemStack: ItemStack, checkType: Boolean): Boolean {
        if (checkType) {
            when {
                itemStack.type.name.endsWith("_HELMET") || itemStack.type.name.equals("SKULL_ITEM", ignoreCase = true) -> {
                    if (itemStack.type != helmet.type) {
                        return false
                    }
//                    if (!itemStack.isSimilar(getHelmet())) {
//                        return false
//                    }
                }
                itemStack.type.name.endsWith("_CHESTPLATE") -> {
                    if (itemStack.type != chestplate.type) {
                        return false
                    }
//                    if (!itemStack.isSimilar(getChestplate())) {
//                        return false
//                    }
                }
                itemStack.type.name.endsWith("_LEGGINGS") -> {
                    if (itemStack.type != leggings.type) {
                        return false
                    }
//                    if (!itemStack.isSimilar(getLeggings())) {
//                        return false
//                    }
                }
                itemStack.type.name.endsWith("_BOOTS") -> {
                    if (itemStack.type != boots.type) {
                        return false
                    }
//                    if (!itemStack.isSimilar(getBoots())) {
//                        return false
//                    }
                }
                else -> return false
            }
        }

        val nmsCopy = ItemUtils.getNmsCopy(itemStack)
        val tag = NBTUtil.getOrCreateTag(nmsCopy)
        return NBTUtil.hasKey(tag, "ArmorSetID") && NBTUtil.getString(tag, "ArmorSetID") == setId
    }

    private fun attachNbt(itemStack: ItemStack): ItemStack {
        val nmsCopy = ItemUtils.getNmsCopy(itemStack)

        val tag = NBTUtil.getOrCreateTag(nmsCopy)
        NBTUtil.setString(tag, "ArmorSetID", setId)
        NBTUtil.setTag(nmsCopy, tag)

        return ItemUtils.getBukkitCopy(nmsCopy)
    }

    fun onEquipped(player: Player) {
        player.sendMessage("${ChatColor.GRAY}[${setName}${ChatColor.GRAY}] Your armor's ability is now active!")
    }

    fun onUnequipped(player: Player) {
        player.sendMessage("${ChatColor.GRAY}[${setName}${ChatColor.GRAY}] Your armor's ability is no longer active!")
    }

    object ArmorParameterType : ParameterType<AbilityArmorSet> {
        override fun transform(sender: CommandSender, source: String): AbilityArmorSet? {
            return AbilityArmorHandler.getSetById(source).also {
                if (it == null) {
                    sender.sendMessage("${ChatColor.RED}Couldn't find an armor set from the given input: ${ChatColor.WHITE}$source")
                }
            }
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            return arrayListOf<String>().also { completions ->
                for (set in AbilityArmorHandler.registeredSets) {
                    if (set.setId.startsWith(source, ignoreCase = true)) {
                        completions.add(set.setId)
                    }
                }
            }
        }
    }

}