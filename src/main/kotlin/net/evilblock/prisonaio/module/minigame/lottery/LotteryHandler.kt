/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.lottery

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.minigame.MinigamesModule
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger

object LotteryHandler : PluginHandler {



    override fun getModule(): PluginModule {
        return MinigamesModule
    }

    override fun getInternalDataFile(): File {
        return File(File(MinigamesModule.getPluginFramework().dataFolder, "internal"), "lottery-session.json")
    }

    override fun initialLoad() {
        super.initialLoad()


    }

    override fun saveData() {
        super.saveData()


    }

    fun getStartingPot(currency: Currency): BigInteger {
        return readBigInteger("lottery.starting-pot.${currency.toType().name}", 1000)
    }

    fun getTicketPrice(): BigInteger {
        return readBigInteger("lottery.ticket-price", 100000)
    }

    private fun readBigInteger(key: String, default: Int): BigInteger {
        val value = getModule().config.get(key, default)

        if (value is String) {
            return BigInteger(value)
        } else if (value is Number) {
            return when (value) {
                is BigInteger -> {
                    value
                }
                is Double -> {
                    BigDecimal(value).toBigInteger()
                }
                else -> {
                    BigInteger(value.toString())
                }
            }
        }

        return BigInteger.ZERO
    }

}