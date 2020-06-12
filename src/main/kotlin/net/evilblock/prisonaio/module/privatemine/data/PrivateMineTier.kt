package net.evilblock.prisonaio.module.privatemine.data

import org.apache.commons.lang.math.DoubleRange
import org.bukkit.Bukkit
import org.bukkit.Material
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
                    blocks = listOf(PrivateMineBlockData(Material.REDSTONE_BLOCK, 0, 100.0)),
//                    blocks = (map["blocks"] as List<Map<String, Any>>).map { PrivateMineBlockData.fromMap(it) },
                    resetInterval = resetInterval,
                    salesTaxRange = DoubleRange(map["sales-tax"] as Double),
                    playerLimit = map["player-limit"] as Int
            )
        }
    }

}