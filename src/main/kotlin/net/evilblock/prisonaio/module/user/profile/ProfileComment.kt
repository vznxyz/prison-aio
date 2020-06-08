package net.evilblock.prisonaio.module.user.profile

import java.util.*

data class ProfileComment(
    val creator: UUID,
    val message: String
) {

    val createdAt: Long = System.currentTimeMillis()

}