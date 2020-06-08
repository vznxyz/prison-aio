package net.evilblock.prisonaio.module.crate.roll.task

import net.evilblock.prisonaio.module.crate.roll.CrateRollHandler

class CrateRollTicker : Thread("PrisonAIO Crate Roll Thread") {

    override fun run() {
        while (true) {
            CrateRollHandler.getActiveRolls().forEach { roll ->
                try {
                    if (roll.canTick()) {
                        roll.tick()

                        if (roll.isFinished()) {
                            CrateRollHandler.forgetRoll(roll)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            try {
                sleep(50L)
            } catch (ignore: Exception) {}
        }
    }

}