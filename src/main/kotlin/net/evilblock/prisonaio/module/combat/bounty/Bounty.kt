/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.bounty

import java.util.*

class Bounty(val target: UUID, val createdBy: UUID, val amount: Long) {

    val createdAt: Long = System.currentTimeMillis()

}