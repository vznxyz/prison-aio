package net.evilblock.prisonaio.module.user.profile

import java.util.*

data class ProfileComment(
    val creator: UUID,
    val message: String,
    val amountPaid: Double = 0.0
) {

    val createdAt: Long = System.currentTimeMillis()

}