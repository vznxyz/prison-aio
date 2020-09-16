/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.bitmask

enum class RegionBitmask(
    val bitmaskValue: Int = 0,
    val displayName: String,
    val description: String
) {

    SAFE_ZONE(1, "SafeZone", "A region where players take no damage at all"),
    DANGER_ZONE(2, "Dangerous", "A region where natural damage and PvP is enabled")

}