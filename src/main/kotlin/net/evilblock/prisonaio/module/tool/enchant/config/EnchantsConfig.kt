/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.config

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.enchant.config.formula.FixedPriceFormulaType
import net.evilblock.prisonaio.module.tool.enchant.config.formula.PriceFormulaType
import net.evilblock.prisonaio.module.tool.enchant.serialize.EnchantsSetReferenceSerializer
import java.lang.reflect.Type

class EnchantsConfig {

    @JsonAdapter(EnchantsSetReferenceSerializer::class)
    private val disabledEnchants: MutableSet<Enchant> = hashSetOf()

    @JsonAdapter(EnchantPriceMapSerializer::class)
    internal val enchantPriceFormulas: MutableMap<Enchant, PriceFormulaType.PriceFormula> = hashMapOf()

    fun isEnchantEnabled(enchant: Enchant): Boolean {
        return !disabledEnchants.contains(enchant)
    }

    fun enableEnchant(enchant: Enchant) {
        disabledEnchants.remove(enchant)
    }

    fun disableEnchant(enchant: Enchant) {
        disabledEnchants.add(enchant)
    }

    fun getEnchantPriceFormula(enchant: Enchant): PriceFormulaType.PriceFormula {
        if (!enchantPriceFormulas.containsKey(enchant)) {
            enchantPriceFormulas[enchant] = FixedPriceFormulaType.FixedPriceFormula()
        }
        return enchantPriceFormulas[enchant]!!
    }

    fun updateEnchantPriceFormula(enchant: Enchant, formula: PriceFormulaType.PriceFormula) {
        enchantPriceFormulas[enchant] = formula
    }

    private inner class EnchantPriceMapSerializer : JsonSerializer<MutableMap<Enchant, PriceFormulaType.PriceFormula>>, JsonDeserializer<MutableMap<Enchant, PriceFormulaType.PriceFormula>> {
        override fun serialize(map: MutableMap<Enchant, PriceFormulaType.PriceFormula>, type: Type, context: JsonSerializationContext): JsonElement {
            return JsonObject().also {
                for ((enchant, formula) in map) {
                    it.add(enchant.id, context.serialize(formula, PriceFormulaType.PriceFormula::class.java))
                }
            }
        }

        override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): MutableMap<Enchant, PriceFormulaType.PriceFormula> {
            return hashMapOf<Enchant, PriceFormulaType.PriceFormula>().also { map ->
                for ((key, value) in element.asJsonObject.entrySet()) {
                    val enchant = EnchantHandler.getEnchantById(key)
                    if (enchant != null) {
                        map[enchant] = context.deserialize(value, PriceFormulaType.PriceFormula::class.java) as PriceFormulaType.PriceFormula
                    }
                }
            }
        }
    }

}