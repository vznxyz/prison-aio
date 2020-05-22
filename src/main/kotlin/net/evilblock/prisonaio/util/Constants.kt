package net.evilblock.prisonaio.util

import org.apache.commons.lang3.StringUtils
import org.bukkit.ChatColor
import org.bukkit.Material

object Constants {

    @JvmStatic
    val CONTAINER_TYPES = listOf(
        Material.CHEST,
        Material.TRAPPED_CHEST,
        Material.ENDER_CHEST,
        Material.FURNACE,
        Material.BURNING_FURNACE,
        Material.DISPENSER,
        Material.HOPPER,
        Material.DROPPER,
        Material.OBSERVER,
        Material.BREWING_STAND
    )

    @JvmStatic
    val INTERACTIVE_TYPES = listOf(
        Material.REDSTONE_COMPARATOR,
        Material.REDSTONE_COMPARATOR_OFF,
        Material.REDSTONE_COMPARATOR_ON,
        Material.DIODE,
        Material.DIODE_BLOCK_OFF,
        Material.DIODE_BLOCK_ON,
        Material.LEVER,
        Material.WOOD_BUTTON,
        Material.STONE_BUTTON,
        Material.WOOD_PLATE,
        Material.STONE_PLATE,
        Material.IRON_PLATE,
        Material.GOLD_PLATE,
        Material.NOTE_BLOCK,
        Material.TRAP_DOOR,
        Material.IRON_TRAPDOOR,
        Material.IRON_DOOR,
        Material.WOODEN_DOOR,
        Material.ACACIA_DOOR,
        Material.BIRCH_DOOR,
        Material.DARK_OAK_DOOR,
        Material.JUNGLE_DOOR,
        Material.SPRUCE_DOOR,
        Material.FENCE_GATE,
        Material.BIRCH_FENCE_GATE,
        Material.DARK_OAK_FENCE_GATE,
        Material.JUNGLE_FENCE_GATE,
        Material.SPRUCE_FENCE_GATE
    )

    @JvmStatic
    val RANK_SYMBOL = "⚔"

    @JvmStatic
    val PRESTIGE_SYMBOL = "${ChatColor.BOLD}⭑"

    @JvmStatic
    val MONEY_SYMBOL = "${ChatColor.BOLD}$"

    @JvmStatic
    val TOKENS_SYMBOL = "${ChatColor.BOLD}⏣"

    @JvmStatic
    val ARROW_UP = "${ChatColor.BOLD}⬆"

    @JvmStatic
    val ARROW_DOWN = "${ChatColor.BOLD}⬇"

    @JvmStatic
    val DOUBLE_ARROW_LEFT = "«"

    @JvmStatic
    val DOUBLE_ARROW_RIGHT = "»"

    @JvmStatic
    val THICK_VERTICAL_LINE = "${ChatColor.BOLD}❙"

    /**
     * Example omitted - Solid line which almost entirely spans the
     * (default) Minecraft chat box. 53 is chosen for no reason other than its width
     * being almost equal to that of the chat box.
     */
    @JvmStatic
    val LONG_LINE = ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53)

    @JvmStatic
    val ADMIN_PREFIX = "${ChatColor.GRAY}[${ChatColor.DARK_RED}${ChatColor.BOLD}ADMIN${ChatColor.GRAY}] "

}