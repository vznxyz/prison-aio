/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.permission

import org.bukkit.Material

enum class GangPermission(
    val detailedName: String,
    val description: String,
    val error: String,
    val icon: Material,
    private val def: PermissionValue,
    private val incompatible: Set<PermissionValue>
) {

    BUILD_AND_BREAK(
        "Building & Breaking",
        "This setting controls who can build and break in your gang's HQ.",
        "You don't have permission to build here!",
        Material.GRASS,
        PermissionValue.MEMBERS,
        emptySet()
    ),
    ACCESS_CONTAINERS(
        "Access Containers",
        "This setting controls who can access containers such as chests, furnaces, hoppers, and droppers.",
        "You don't have permission to access containers here!",
        Material.CHEST,
        PermissionValue.MEMBERS,
        emptySet()
    ),
    INTERACT_WITH_BLOCKS(
        "Interact With Blocks",
        "This setting controls who can interact with blocks such as doors, trap doors, fence gates, buttons, levers, pressure plates, repeaters, and comparators.",
        "You don't have permission to interact with blocks here!",
        Material.LEVER,
        PermissionValue.MEMBERS,
        emptySet()
    ),
    SPEND_TROPHIES(
        "Spend Trophies",
        "This setting controls who can spend trophies in the Gang Shop.",
        "You don't have permission to spend the gang's trophies!",
        Material.GOLD_INGOT,
        PermissionValue.CO_LEADERS,
        setOf(PermissionValue.MEMBERS, PermissionValue.VISITORS)
    ),
    ALLOW_VISITORS(
        "Allow Visitors",
        "This setting controls whether or not players can visit your gang's HQ.",
        "The owner of that gang isn't accepting visitors right now.",
        Material.WOOD_DOOR,
        PermissionValue.MEMBERS,
        setOf(PermissionValue.CO_LEADERS)
    ),
    KICK_VISITORS(
        "Kick Visitors",
        "This setting controls who can kick visitors in your gang's HQ.",
        "You don't have permission to kick visitors!",
        Material.IRON_DOOR,
        PermissionValue.CAPTAINS,
        setOf(PermissionValue.VISITORS)
    ),
    INVITE_MEMBERS(
        "Invite Members",
        "This setting controls who can invite members to your gang.",
        "You don't have permission to invite members to the gang!",
        Material.PAPER,
        PermissionValue.CO_LEADERS,
        setOf(PermissionValue.VISITORS, PermissionValue.MEMBERS)
    ),
    KICK_MEMBERS(
        "Kick Members",
        "This setting controls who can kick members of your gang.",
        "You don't have permission to kick gang members!",
        Material.TNT,
        PermissionValue.CAPTAINS,
        setOf(PermissionValue.VISITORS, PermissionValue.MEMBERS)
    );

    fun getDefaultValue(): PermissionValue {
        return def
    }

    fun getNextValue(current: PermissionValue): PermissionValue {
        var nextOrdinal = current.ordinal + 1
        if (nextOrdinal >= PermissionValue.values().size) {
            nextOrdinal = 0
        }

        val nextValue = PermissionValue.values()[nextOrdinal]
        if (incompatible.contains(nextValue)) {
            return getNextValue(nextValue)
        }

        return nextValue
    }

    fun isCompatibleWith(value: PermissionValue): Boolean {
        return !incompatible.contains(value)
    }

    enum class PermissionValue(val detailedName: String) {
        OWNER("Leader only"),
        CO_LEADERS("Co-leaders only"),
        CAPTAINS("Captains only"),
        MEMBERS("Members only"),
        VISITORS("Visitors and members")
    }

}