/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.mineparty

import net.evilblock.prisonaio.module.mine.Mine
import java.lang.reflect.Type

class MinePartyMine(id: String) : Mine(id) {

    private var startingAmount: Int = 0
    private var remainingAmount: Int = 0

    override fun getAbstractType(): Type {
        return MinePartyMine::class.java
    }

    override fun supportsAutomaticReset(): Boolean {
        return false
    }

}