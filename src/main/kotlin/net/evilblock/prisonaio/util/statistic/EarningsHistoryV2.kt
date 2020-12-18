/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.statistic

import net.evilblock.cubed.util.NumberUtils
import java.math.BigDecimal

class EarningsHistoryV2 {

    private var earnings: BigDecimal = BigDecimal(0.0)

    private val minutes: ArrayList<BigDecimal> = arrayListOf()
    private val hours: ArrayList<BigDecimal> = arrayListOf()
    private val days: ArrayList<BigDecimal> = arrayListOf()

    private var aggregateMinutes: Int = 60
    private var aggregateHours: Int = 24

    var allTime: BigDecimal = BigDecimal(0.0)
    var lastHour: BigDecimal = BigDecimal(0.0)
    var lastDay: BigDecimal = BigDecimal(0.0)
    var lastWeek: BigDecimal = BigDecimal(0.0)

    var lastAggregation: Long = System.currentTimeMillis()

    fun addEarnings(add: Number) {
        val converted = NumberUtils.numberToBigDecimal(add)
        earnings += converted
        allTime += converted
        calculate()
    }

    fun aggregate() {
        lastAggregation = System.currentTimeMillis()

        minutes.add(earnings)

        if (minutes.size > 60) {
            while (minutes.size > 60) {
                minutes.removeAt(0)
            }
        }

        if (aggregateMinutes-- <= 0) {
            hours.add(sum(minutes))

            if (hours.size > 60) {
                while (hours.size > 60) {
                    hours.removeAt(0)
                }
            }

            aggregateMinutes = 60

            if (aggregateHours-- <= 0) {
                days.add(sum(hours))

                if (days.size > 7) {
                    while (days.size > 7) {
                        days.removeAt(0)
                    }
                }

                aggregateHours = 24
            }
        }

        earnings = BigDecimal(0.0)

        calculate()
    }

    fun calculate() {
        lastHour = sum(minutes) + earnings

        lastDay = if (hours.size >= 24) {
            sum(hours) + sum(minutes.drop(60 - aggregateMinutes))
        } else {
            sum(hours) + sum(minutes)
        } + earnings

        lastWeek = if (days.size >= 7) {
            sum(days) + sum(hours.drop(24 - aggregateHours)) + sum(minutes.drop(60 - aggregateMinutes))
        } else {
            sum(days) + sum(hours) + sum(minutes)
        } + earnings
    }

    private fun sum(list: List<BigDecimal>): BigDecimal {
        var sum = BigDecimal(0.0)

        val it = list.iterator()
        while (it.hasNext()) {
            sum += it.next()
        }

        return sum
    }

}