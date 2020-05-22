package net.evilblock.prisonaio.module.crate.roll.task

import net.evilblock.prisonaio.module.crate.roll.CrateRollHandler

class CrateRollTicker : Runnable {

    override fun run() {
        CrateRollHandler.getActiveRolls().forEach { roll ->
            if (roll.canTick()) {
                roll.tick()

                if (roll.isFinished()) {
                    CrateRollHandler.forgetRoll(roll)
                }
            }
        }
    }

}