package net.evilblock.prisonaio.module.privatemine.data

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.privatemine.PrivateMinesModule
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
        const val SHOP_NAME_PREFIX = "PrivateMinesShop-Tier"

        private val map = hashMapOf<Int, PrivateMineTier>()

        fun fromInt(tier: Int): PrivateMineTier? {
            return map[tier]
        }

        fun initialLoad() {
            for (tierMap in PrivateMinesModule.config.getList("tiers") as List<Map<String, Any>>) {
                val tier = fromMap(tierMap)

                if (!tier.schematicFile.exists()) {
                    PrisonAIO.instance.logger.severe("Couldn't find schematic file for tier ${tier.number}")
                }

                map[tier.number] = tier
            }
        }

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
                    salesTaxRange = DoubleRange(map["sales-tax"] as Double),
                    playerLimit = map["player-limit"] as Int
            )
        }
    }

}