/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.news

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class News(
    var title: String,
    val createdBy: UUID
) {

    val id: UUID = UUID.randomUUID()
    val createdAt: Long = System.currentTimeMillis()

    var lines = arrayListOf<String>()

    var icon: ItemStack = ItemStack(Material.PAPER)
    var hidden: Boolean = true

    var reads: Int = 0

    override fun equals(other: Any?): Boolean {
        return other is News && id.toString() == other.id.toString()
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}