/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.statistic

import java.math.BigInteger
import kotlin.collections.ArrayList

class EarningsHistoryV1 {

    private var earnings: Long = 0

    var days: ArrayList<BigInteger> = arrayListOf()

    private var hoursAggregation: Int = 24
    var hours: ArrayList<BigInteger> = arrayListOf()

    private var minutesAggregation: Int = 60
    var minutes: ArrayList<BigInteger> = arrayListOf()

    fun aggregate() {
        minutes.add(BigInteger(earnings.toString()))

        if (minutes.size > 60) {
            minutes = ArrayList(minutes.drop(minutes.size - 60))
        }

        if (minutesAggregation-- <= 0) {
            hours.add(sum(minutes))

            if (hours.size > 24) {
                hours = ArrayList(hours.drop(hours.size - 24))
            }

            minutesAggregation = 60

            if (hoursAggregation-- <= 0) {
                days.add(sum(hours))

                if (days.size > 7) {
                    days = ArrayList(days.drop(days.size - 7))
                }

                hoursAggregation = 24
            }
        }

        earnings = 0L
    }

    fun isHourViewComplete(): Boolean {
        return minutes.size >= 60
    }

    fun isDayViewComplete(): Boolean {
        return hours.size >= 24
    }

    fun isWeekViewComplete(): Boolean {
        return days.size >= 7
    }

    fun getLastHourRealTime(): BigInteger {
        return sum(minutes)
    }

    fun getLastDayRealTime(): BigInteger {
        return sum(hours) + sum(minutes.drop(minutesAggregation))
    }

    fun getLastWeekRealTime(): BigInteger {
        return sum(days) + sum(hours.drop(24 - hoursAggregation)) + sum(minutes.drop(60 - minutesAggregation))
    }

    private fun sum(list: List<BigInteger>): BigInteger {
        var sum = BigInteger("0")

        val it = list.iterator()
        while (it.hasNext()) {
            sum += it.next()
        }

        return sum
    }

    fun getEarnings(): Long {
        return earnings
    }

    fun addEarnings(amount: Long) {
        earnings += amount
    }

}