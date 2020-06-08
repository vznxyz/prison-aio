package net.evilblock.prisonaio.module.privatemine.data

import org.bukkit.Material

data class PrivateMineBlockData(val material: Material, val data: Byte, val percentage: Double) {

    companion object {
        @JvmStatic
        fun fromMap(map: Map<String, Any>): PrivateMineBlockData {
            return PrivateMineBlockData(
                    material = Material.valueOf(map["material"] as String),
                    data = (map["data"] as Int).toByte(),
                    percentage = map["percentage"] as Double
            )
        }
    }

}