/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.hook

import net.evilblock.cosmetics.hook.Hook
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.MainMenu
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.entity.Player

object CosmeticsHook : Hook {

    override fun openMainMenu(player: Player) {
        MainMenu(UserHandler.getUser(player)).openMenu(player)
    }

    override fun inSupportedRegion(player: Player): Boolean {
        return RegionHandler.findRegion(player).supportsCosmetics()
    }

    override fun canRenderEffects(player: Player): Boolean {
        return UserHandler.getUser(player).settings.getSettingOption(UserSetting.COSMETIC_EFFECTS).getValue()
    }

    override fun canRenderTracks(player: Player): Boolean {
        return UserHandler.getUser(player).settings.getSettingOption(UserSetting.COSMETIC_TRACKS).getValue()
    }

}