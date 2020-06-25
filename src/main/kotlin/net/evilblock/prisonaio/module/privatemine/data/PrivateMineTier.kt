/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.data

import org.apache.commons.lang.math.DoubleRange
import org.bukkit.Bukkit
import java.io.File

class PrivateMineTier(
    val number: Int,
    val blocks: List<PrivateMineBlockData>,
    val resetInterval: Long,
    val salesTaxRange: DoubleRange,
    val playerLimit: Int
) {

    var schematicFile: File = File(File(Bukkit.getPluginManager().getPlugin("WorldEdit").dataFolder, "schematics"), "PrivateMines-Tier${number}.schematic")

    companion object {
        @JvmStatic
        fun fromMap(map: Map<String, Any>): PrivateMineTier {
            val resetInterval = if (map["reset-interval"] is Int) {
                (map["reset-interval"] as Int).toLong()
            } else{
                map["reset-interval"] as Long
            }

            return PrivateMineTier(
                    number = map["tier"] as Int,
                    blocks = (map["blocks"] as List<Map<String, Any>>).map { PrivateMineBlockData.fromMap(it) },
                    resetInterval = resetInterval,
                    salesTaxRange = DoubleRange(0.0, map["sales-tax"] as Double),
                    playerLimit = map["player-limit"] as Int
            )
        }
    }

}