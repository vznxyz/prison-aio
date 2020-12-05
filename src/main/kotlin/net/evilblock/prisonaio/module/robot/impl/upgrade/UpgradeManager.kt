package net.evilblock.prisonaio.module.robot.impl.upgrade

import net.evilblock.prisonaio.module.robot.impl.upgrade.impl.EfficiencyUpgrade
import net.evilblock.prisonaio.module.robot.impl.upgrade.impl.FortuneUpgrade

object UpgradeManager {

    private val registeredUpgrades: List<Upgrade> = listOf(EfficiencyUpgrade, FortuneUpgrade)

    /**
     * Gets a copy of the registered upgrades.
     */
    fun getRegisteredUpgrades(): List<Upgrade> {
        return registeredUpgrades.toList()
    }

}