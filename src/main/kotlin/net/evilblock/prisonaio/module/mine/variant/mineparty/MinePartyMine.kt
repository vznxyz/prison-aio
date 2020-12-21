/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.mineparty

import net.evilblock.cubed.util.Duration
import net.evilblock.prisonaio.module.mine.variant.normal.NormalMine
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.lang.reflect.Type

class MinePartyMine(id: String) : NormalMine(id) {

    var active: Boolean = false

    var goal: Int = 0
    var progress: Int = 0

    var startedAt: Long = System.currentTimeMillis()
    var duration: Duration = Duration(0)

    override fun getAbstractType(): Type {
        return MinePartyMine::class.java
    }

    override fun supportsAutomaticReset(): Boolean {
        return false
    }

    override fun supportsAbilityEnchants(): Boolean {
        return false
    }

    override fun supportsAutoSell(): Boolean {
        return false
    }

    override fun supportsPassiveEnchants(): Boolean {
        return false
    }

    override fun supportsRewards(): Boolean {
        return false
    }

    override fun supportsCosmetics(): Boolean {
        return true
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        if (!active || isExpired()) {
            cancellable.isCancelled = true
            return
        }

        progress++

        if (progress >= goal) {
            MinePartyHandler.finishEvent()
        }
    }

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= startedAt + duration.get()
    }

    fun getRemainingTime(): Long {
        return (startedAt + duration.get()) - System.currentTimeMillis()
    }

    fun getRemainingSeconds(): Int {
        return (getRemainingTime() / 1000.0).toInt()
    }

}