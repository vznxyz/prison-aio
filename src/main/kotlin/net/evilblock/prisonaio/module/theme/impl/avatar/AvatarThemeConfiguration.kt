/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme.impl.avatar

import net.evilblock.prisonaio.module.theme.ThemeConfiguration
import java.lang.reflect.Type

class AvatarThemeConfiguration : ThemeConfiguration {

    override fun getAbstractType(): Type {
        return AvatarThemeConfiguration::class.java
    }

}