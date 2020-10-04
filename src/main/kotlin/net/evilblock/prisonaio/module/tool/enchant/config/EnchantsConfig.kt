/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.config

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.enchant.config.formula.FixedPriceFormulaType
import net.evilblock.prisonaio.module.tool.enchant.config.formula.PriceFormulaType
import net.evilblock.prisonaio.module.tool.enchant.serialize.EnchantsSetReferenceSerializer
import java.lang.reflect.Type

class EnchantsConfig {

    @JsonAdapter(EnchantsSetReferenceSerializer::class)
    private val disabledEnchants: MutableSet<AbstractEnchant> = hashSetOf()

    @JsonAdapter(EnchantPriceMapSerializer::class)
    internal val enchantPriceFormulas: MutableMap<AbstractEnchant, PriceFormulaType.PriceFormula> = hashMapOf()

    fun isEnchantEnabled(enchant: AbstractEnchant): Boolean {
        return !disabledEnchants.contains(enchant)
    }

    fun enableEnchant(enchant: AbstractEnchant) {
        disabledEnchants.remove(enchant)
    }

    fun disableEnchant(enchant: AbstractEnchant) {
        disabledEnchants.add(enchant)
    }

    fun getEnchantPriceFormula(enchant: AbstractEnchant): PriceFormulaType.PriceFormula {
        if (!enchantPriceFormulas.containsKey(enchant)) {
            enchantPriceFormulas[enchant] = FixedPriceFormulaType.FixedPriceFormula()
        }
        return enchantPriceFormulas[enchant]!!
    }

    fun updateEnchantPriceFormula(enchant: AbstractEnchant, formula: PriceFormulaType.PriceFormula) {
        enchantPriceFormulas[enchant] = formula
    }

    private inner class EnchantPriceMapSerializer : JsonSerializer<MutableMap<AbstractEnchant, PriceFormulaType.PriceFormula>>, JsonDeserializer<MutableMap<AbstractEnchant, PriceFormulaType.PriceFormula>> {
        override fun serialize(map: MutableMap<AbstractEnchant, PriceFormulaType.PriceFormula>, type: Type, context: JsonSerializationContext): JsonElement {
            return JsonObject().also {
                for ((enchant, formula) in map) {
                    it.add(enchant.id, context.serialize(formula, PriceFormulaType.PriceFormula::class.java))
                }
            }
        }

        override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): MutableMap<AbstractEnchant, PriceFormulaType.PriceFormula> {
            return hashMapOf<AbstractEnchant, PriceFormulaType.PriceFormula>().also { map ->
                for ((key, value) in element.asJsonObject.entrySet()) {
                    val enchant = EnchantsManager.getEnchantById(key)
                    if (enchant != null) {
                        map[enchant] = context.deserialize(value, PriceFormulaType.PriceFormula::class.java) as PriceFormulaType.PriceFormula
                    }
                }
            }
        }
    }

}