/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.bitmask

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class RegionBitmask(
    val bitmaskValue: Int = 0,
    val displayName: String,
    val description: String,
    val icon: ItemStack
) {

    SAFE_ZONE(1, "SafeZone", "A region where players take no damage at all", ItemStack(Material.WOOL, 1, 5)),
    DANGER_ZONE(2, "Dangerous", "A region where natural damage and PvP is enabled", ItemStack(Material.WOOL, 1, 14)),
    ALLOW_BUILD(4, "Allow Build", "A region where players can place/destroy blocks", ItemStack(Material.WOOD)),
    DENY_FLY(8, "Deny Fly", "A region where players cannot use fly", ItemStack(Material.ELYTRA)),
    DENY_SPEED(16, "Deny Speed", "A region where players cannot use modded speed", ItemStack(Material.POTION, 1, 8234)),
    SPEED(32, "Speed Effect", "A region where players have permanent speed 2 effect", ItemStack(Material.POTION, 1, 8226)),
    NO_FF(64, "No Friendly-Fire", "A region where gang members cannot attack each other", ItemStack(Material.BEACON)),

}