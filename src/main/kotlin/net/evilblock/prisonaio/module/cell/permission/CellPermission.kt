package net.evilblock.prisonaio.module.cell.permission

import org.bukkit.Material

enum class CellPermission(
    val detailedName: String,
    val description: String,
    val error: String,
    val icon: Material,
    private val def: PermissionValue,
    private val incompatible: Set<PermissionValue>
) {

    ALLOW_VISITORS(
        "Allow Visitors",
        "This setting controls whether or not players can visit your cell.",
        "The owner of that cell isn't accepting visitors right now.",
        Material.WOOD_DOOR,
        PermissionValue.MEMBERS,
        setOf(PermissionValue.OWNER)
    ),
    BUILD_AND_BREAK(
        "Building & Breaking",
        "This setting controls who can build and break on your island.",
        "You don't have permission to build in this cell.",
        Material.DIAMOND_PICKAXE,
        PermissionValue.MEMBERS,
        emptySet()
    ),
    ACCESS_CONTAINERS(
        "Access Containers",
        "This setting controls who can access containers such as chests, furnaces, hoppers, and droppers.",
        "You don't have permission to access containers in this cell.",
        Material.CHEST,
        PermissionValue.MEMBERS,
        emptySet()
    ),
    INTERACT_WITH_BLOCKS(
        "Interact With Blocks",
        "This setting controls who can interact with blocks such as doors, trap doors, fence gates, buttons, levers, pressure plates, repeaters, and comparators.",
        "You don't have permission to interact with blocks in this cell.",
        Material.LEVER,
        PermissionValue.MEMBERS,
        emptySet()
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
        OWNER("Owner only"),
        MEMBERS("Members only"),
        VISITORS("Visitors and members")
    }

}