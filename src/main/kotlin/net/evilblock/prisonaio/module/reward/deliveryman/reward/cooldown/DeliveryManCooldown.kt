package net.evilblock.prisonaio.module.reward.deliveryman.reward.cooldown

import net.evilblock.cubed.util.TimeUtil
import java.util.concurrent.TimeUnit

enum class DeliveryManCooldown(
    val displayName: String,
    val timeDuration: Long
) {

    DAILY("Daily", TimeUnit.DAYS.toMillis(1)),
    BI_DAILY("Bi-Daily", TimeUnit.HOURS.toMillis(12)),
    TRI_DAILY("Tri-Daily", TimeUnit.HOURS.toMillis(8)),
    TRI_HOURLY("Tri-Hourly", TimeUnit.HOURS.toMillis(3)),
    BI_HOURLY("Bi-Hourly", TimeUnit.HOURS.toMillis(2)),
    HOURLY("Hourly", TimeUnit.HOURS.toMillis(1));

    fun getFormattedText(): String {
        return TimeUtil.formatIntoAbbreviatedString((timeDuration / 1000.0).toInt())
    }

}