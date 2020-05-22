package net.evilblock.prisonaio.module.quest.impl.narcotic

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

enum class Narcotic(
    val displayName: String,
    val textColor: ChatColor,
    val itemStack: ItemStack,
    val buyPrice: Double = 0.0,
    val sellPrice: Double = 0.0
) {

    MARIJUANA(
        "${ChatColor.GREEN}${ChatColor.BOLD}Marijuana",
        ChatColor.GREEN,
        ItemBuilder.of(Material.LONG_GRASS).data(1).build()
    ),
    MAGIC_MUSHROOMS(
        "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}Magic Mushrooms",
        ChatColor.DARK_PURPLE,
        ItemBuilder.of(Material.RED_MUSHROOM).build()
    ),
    LSD(
        "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}LSD",
        ChatColor.LIGHT_PURPLE,
        ItemBuilder.of(Material.PAPER).build()
    ),
    PERCOCET(
        "${ChatColor.YELLOW}${ChatColor.BOLD}Percocet",
        ChatColor.YELLOW,
        ItemBuilder.of(Material.GOLD_NUGGET).build()
    ),
    XANAX(
        "${ChatColor.RED}${ChatColor.BOLD}Xanax",
        ChatColor.RED,
        ItemBuilder.of(Material.DOUBLE_PLANT).build()
    ),
    COCAINE(
        "${ChatColor.WHITE}${ChatColor.BOLD}Cocaine",
        ChatColor.WHITE,
        ItemBuilder.of(Material.SUGAR).build()
    ),
    METHAMPHETAMINE(
        "${ChatColor.DARK_AQUA}${ChatColor.BOLD}Methamphetamine",
        ChatColor.DARK_AQUA,
        ItemBuilder.of(Material.SULPHUR).build()
    );

    fun toItemStack(amount: Int): ItemStack {
        return ItemBuilder
            .copyOf(itemStack.clone())
            .name(displayName)
            .amount(amount).build()
    }

    fun findInInventory(player: Player): Map<Int, ItemStack> {
        val map = hashMapOf<Int, ItemStack>()

        for (i in player.inventory.contents.indices) {
            val itemAtSlot = player.inventory.contents[i] ?: continue

            if (isSimilar(itemAtSlot)) {
                map[i] = itemAtSlot
            }
        }

        return map
    }

    private fun isSimilar(check: ItemStack): Boolean {
        if (this.itemStack.type != check.type) {
            return false
        }

        if (!check.hasItemMeta()) {
            return false
        }

        if (!check.itemMeta.hasDisplayName()) {
            return false
        }

        if (!check.itemMeta.displayName.startsWith(this.displayName)) {
            return false
        }

        if (this.itemStack.itemMeta.hasLore() != check.itemMeta.hasLore()) {
            return false
        }

        return true
    }

    object CommandParameterType : ParameterType<Narcotic> {
        override fun transform(sender: CommandSender, source: String): Narcotic? {
            return try {
                valueOf(source)
            } catch (e: Exception) {
                sender.sendMessage("${ChatColor.RED}Could not find narcotic by that name.")
                null
            }
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            val completions = arrayListOf<String>()

            for (narcotic in values()) {
                if (narcotic.name.startsWith(source, ignoreCase = true)) {
                    completions.add(narcotic.name)
                }
            }

            return completions
        }
    }

}