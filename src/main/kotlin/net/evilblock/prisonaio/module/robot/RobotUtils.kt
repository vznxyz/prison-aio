package net.evilblock.prisonaio.module.robot

import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.nms.NBTUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

object RobotUtils {

    @JvmStatic
    fun isMechanicEggItem(itemStack: ItemStack): Boolean {
        return itemStack.type == Material.MONSTER_EGG
                && itemStack.hasItemMeta()
                && itemStack.itemMeta.hasDisplayName()
                && itemStack.itemMeta.hasLore()
                && itemStack.itemMeta.displayName == "${ChatColor.YELLOW}${ChatColor.BOLD}Robot Mechanic Egg"
                && ItemUtils.itemTagHasKey(itemStack, "RobotMechanicEgg")
    }

    @JvmStatic
    fun makeMechanicEggItem(amount: Int): ItemStack {
        val lore = TextSplitter.split(text = "Place this egg on any region that you own to spawn a Robot Mechanic, who is useful for combining and collecting from robots.", linePrefix = ChatColor.GRAY.toString())

        var item = ItemBuilder.of(Material.MONSTER_EGG)
                .amount(amount)
                .name("${ChatColor.YELLOW}${ChatColor.BOLD}Robot Mechanic Egg")
                .setLore(lore)
                .build()

        GlowEnchantment.addGlow(item)

        item = ItemUtils.setMonsterEggType(item, EntityType.SPIDER)

        return ItemUtils.addToItemTag(item, "RobotMechanicEgg", "true", true)
    }

    @JvmStatic
    fun isRobotItem(itemStack: ItemStack): Boolean {
        return itemStack.type == Material.ARMOR_STAND
                && itemStack.hasItemMeta()
                && itemStack.itemMeta.hasDisplayName()
                && itemStack.itemMeta.hasLore()
                && ItemUtils.itemTagHasKey(itemStack, "RobotItem")
    }

    @JvmStatic
    fun makeRobotItem(amount: Int, tier: Int): ItemStack {
        val lore = arrayListOf<String>()

        lore.addAll(TextSplitter.split(
                length = 40,
                text = "Place this robot anywhere in a region you own to start collecting money and tokens.",
                linePrefix = ChatColor.GRAY.toString()
        ))

        lore.add("")

        if (tier > 0) {
            lore.add("${ChatColor.RED}${ChatColor.BOLD}Tier $tier")

            if (tier >= 7) {
                lore.addAll(TextSplitter.split(
                        length = 40,
                        text = "This robot has reached the max tier possible through tier combination.",
                        linePrefix = ChatColor.RED.toString()
                ))
            } else {
                lore.addAll(TextSplitter.split(
                        length = 40,
                        text = "Combine this robot with another by dragging and dropping. The higher the tier, the more money and tokens the robot will collect without any upgrades.",
                        linePrefix = ChatColor.GRAY.toString()
                ))
            }
        } else {
            lore.add("${ChatColor.RED}${ChatColor.BOLD}Tier Combination")

            lore.addAll(TextSplitter.split(
                    length = 40,
                    text = "You can combine two robots of the same tier, up to tier 7, by dragging and dropping them onto each other. The higher the tier, the more money and tokens the robot will collect without any upgrades.",
                    linePrefix = ChatColor.GRAY.toString()
            ))
        }

        val item = ItemBuilder(Material.ARMOR_STAND)
                .amount(amount)
                .name("${ChatColor.RED}${ChatColor.BOLD}Robot")
                .setLore(lore)
                .build()

        GlowEnchantment.addGlow(item)

        return ItemUtils.addToItemTag(item, "RobotItem", tier.toString(), true)
    }

}