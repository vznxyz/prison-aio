/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme.impl.avatar.npc

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.theme.impl.avatar.AvatarTheme
import net.evilblock.prisonaio.module.theme.impl.avatar.path.menu.SelectPathMenu
import net.evilblock.prisonaio.module.theme.impl.avatar.user.AvatarThemeUserData
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.Location
import org.bukkit.entity.Player

class MasterNPC(location: Location) : NpcEntity(lines = listOf("master npc"), location = location) {

    override fun initializeData() {
        super.initializeData()
    }

    override fun onRightClick(player: Player) {
        if (!AvatarTheme.isThemeEnabled()) {
            return
        }

        val data = UserHandler.getUser(player.uniqueId).themeUserData as AvatarThemeUserData
        if (!data.hasBaseElement()) {
            SelectPathMenu(data).openMenu(player)
            return
        }
    }

}