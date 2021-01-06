/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme.impl.avatar

import com.google.common.base.Charsets
import com.google.common.io.Files
import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.module.theme.Theme
import net.evilblock.prisonaio.module.theme.ThemesModule
import net.evilblock.prisonaio.module.theme.impl.avatar.command.AvatarPickaxeCommand
import net.evilblock.prisonaio.module.theme.impl.avatar.npc.command.SpawnMasterCommand
import net.evilblock.prisonaio.module.theme.impl.avatar.path.listener.AvatarPathListeners
import net.evilblock.prisonaio.module.theme.impl.avatar.user.AvatarThemeUserData
import net.evilblock.prisonaio.module.theme.user.ThemeUserData
import net.evilblock.prisonaio.module.user.User
import org.bukkit.event.Listener

object AvatarTheme : Theme {

    lateinit var config: AvatarThemeConfiguration

    override fun getId(): String {
        return "avatar"
    }

    override fun getName(): String {
        return "Avatar"
    }

    override fun initialLoad() {
        val dataFile = getDataFile()

        config = if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                Cubed.gson.fromJson(reader, AvatarThemeConfiguration::class.java) as AvatarThemeConfiguration
            }
        } else {
            AvatarThemeConfiguration()
        }
    }

    override fun getConfiguration(): AvatarThemeConfiguration {
        return config
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            AvatarPathListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            AvatarPickaxeCommand::class.java,
            SpawnMasterCommand::class.java
        )
    }

    fun isThemeEnabled(): Boolean {
        return ThemesModule.isThemeEnabled() && ThemesModule.getTheme() == this
    }

    override fun hasUserDataImplementation(): Boolean {
        return true
    }

    override fun createUserData(user: User): ThemeUserData {
        return AvatarThemeUserData(user)
    }

}