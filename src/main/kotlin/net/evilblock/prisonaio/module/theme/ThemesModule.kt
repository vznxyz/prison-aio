/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme

import net.evilblock.cubed.command.CommandHandler
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.theme.impl.avatar.AvatarTheme
import java.io.File
import java.util.*

object ThemesModule : PluginModule() {

    private val themes: EnumMap<ThemeType, Theme> = EnumMap(mapOf(
        ThemeType.AVATAR to AvatarTheme
    ))

    private var theme: Theme? = null

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun getName(): String {
        return "Themes"
    }

    override fun getConfigFileName(): String {
        return "themes"
    }

    override fun onEnable() {
        super.onEnable()

        val themeId = config.getString("theme")
        if (themeId.isNotEmpty()) {
            try {
                theme = themes[ThemeType.valueOf(themeId.toUpperCase())]

                theme?.let { theme ->
                    for (listener in theme.getListeners()) {
                        getPluginFramework().server.pluginManager.registerEvents(listener, getPluginFramework())
                    }

                    for (command in theme.getCommands()) {
                        CommandHandler.registerClass(command)
                    }

                    for ((type, parameterType) in theme.getCommandParameterTypes()) {
                        CommandHandler.registerParameterType(type, parameterType)
                    }
                }
            } catch (e: IllegalArgumentException) {
                getPluginFramework().logger.info("Failed to enable theme \"$themeId\"")
                e.printStackTrace()
            }
        }

        if (isThemeEnabled()) {
            getPluginFramework().logger.info("Loaded and enabled theme ${theme!!.getName()}")
        } else {
            getPluginFramework().logger.info("No theme loaded!")
        }
    }

    override fun onReload() {
        super.onReload()


    }

    override fun onAutoSave() {
        super.onAutoSave()
    }

    fun isThemeEnabled(): Boolean {
        return theme != null
    }

    fun getTheme(): Theme {
        return theme!!
    }

    fun getThemesDirectory(): File {
        return File(PrisonAIO.instance.dataFolder, "themes")
    }

    fun getThemeFile(name: String): File {
        return File(getThemesDirectory(), name)
    }

}