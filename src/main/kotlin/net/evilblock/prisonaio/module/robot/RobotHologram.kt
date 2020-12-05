package net.evilblock.prisonaio.module.robot

import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.entity.Player

class RobotHologram(private val robot: Robot) : HologramEntity(text = "", location = robot.getLocationAdjustmentForHologram(robot.location)) {

    override fun isMultiPartEntity(): Boolean {
        return true
    }

    override fun onRightClick(player: Player) {
        robot.onRightClick(player)
    }

    override fun isVisibleToPlayer(player: Player): Boolean {
        if (!UserHandler.isUserLoaded(player.uniqueId)) {
            return false
        }

        return UserHandler.getUser(player.uniqueId).settings.getSettingOption(UserSetting.ROBOT_HOLOGRAMS).getValue() && super.isVisibleToPlayer(player)
    }

}