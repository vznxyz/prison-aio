/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.theme.user.ThemeUserData
import net.evilblock.prisonaio.module.user.User
import org.bukkit.event.Listener
import java.io.File

interface Theme {

    fun getId(): String

    fun getName(): String

    fun initialLoad()

    fun getConfiguration(): ThemeConfiguration

    fun getListeners(): List<Listener> {
        return emptyList()
    }

    fun getCommands(): List<Class<*>> {
        return emptyList()
    }

    fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return emptyMap()
    }

    fun getDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "themes"), "${getId()}.json")
    }

    fun hasUserDataImplementation(): Boolean

    fun createUserData(user: User): ThemeUserData {
        throw IllegalStateException("Theme has no user data implementation")
    }

}