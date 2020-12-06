package net.evilblock.prisonaio.module.robot

import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.nms.NBTUtil
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

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
        val lore = TextSplitter.split(text = "Place this egg on any region that you own to spawn a Robot Mechanic, who is useful for combining and collecting from robots.")

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
    fun getRobotItemTier(itemStack: ItemStack): Int {
        return NBTUtil.getString(NBTUtil.getOrCreateTag(ItemUtils.getNmsCopy(itemStack)), "RobotItem").toInt()
    }

    @JvmStatic
    fun makeRobotItem(amount: Int, tier: Int): ItemStack {
        val name = if (tier == 0) {
            "${ChatColor.RED}${ChatColor.BOLD}Robot ${ChatColor.GRAY}(No Tier)"
        } else {
            "${ChatColor.RED}${ChatColor.BOLD}Robot ${ChatColor.GRAY}(Tier ${tier})"
        }

        val lore = arrayListOf<String>().also { desc ->
            val moneyPerHour = (RobotsModule.getTierBaseMoney(tier) * 10.0) * TimeUnit.HOURS.toSeconds(1L)
            val tokensPerHour = ((RobotsModule.getTierBaseTokens(tier) * 10.0) * TimeUnit.HOURS.toSeconds(1L)).toLong()

            desc.addAll(TextSplitter.split(text = "Place this robot on your plot to start generating money and tokens!"))
            desc.add("")
            desc.add("${ChatColor.RED}${ChatColor.BOLD}Statistics")
            desc.add("${ChatColor.GRAY}Money/HR: ${Formats.formatMoney(moneyPerHour)}")
            desc.add("${ChatColor.GRAY}Tokens/HR: ${Formats.formatTokens(tokensPerHour)}")
        }

        val item = ItemBuilder(Material.ARMOR_STAND)
                .amount(amount)
                .name(name)
                .setLore(lore)
                .build()

        GlowEnchantment.addGlow(item)

        return ItemUtils.addToItemTag(item, "RobotItem", tier.toString(), true)
    }

}