/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.enchant.serialize.EnchantsSetReferenceSerializer

class EnchantsConfig {

    @JsonAdapter(EnchantsSetReferenceSerializer::class)
    private val disabledEnchants: MutableSet<AbstractEnchant> = hashSetOf()

    fun isEnchantEnabled(enchant: AbstractEnchant): Boolean {
        return !disabledEnchants.contains(enchant)
    }

    fun enableEnchant(enchant: AbstractEnchant) {
        disabledEnchants.remove(enchant)
    }

    fun disableEnchant(enchant: AbstractEnchant) {
        disabledEnchants.add(enchant)
    }

}