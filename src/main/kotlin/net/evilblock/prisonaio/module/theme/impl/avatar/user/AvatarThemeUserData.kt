/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.theme.impl.avatar.user

import net.evilblock.prisonaio.module.theme.impl.avatar.structure.AvatarElement
import net.evilblock.prisonaio.module.theme.user.ThemeUserData
import net.evilblock.prisonaio.module.user.User
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

class AvatarThemeUserData(user: User) : ThemeUserData(user) {

    private var baseElement: AvatarElement? = null
    private var elementProgress: MutableMap<AvatarElement, Int> = ConcurrentHashMap()

    override fun getAbstractType(): Type {
        return AvatarThemeUserData::class.java
    }

    fun hasBaseElement(): Boolean {
        return baseElement != null
    }

    fun updateBaseElement(element: AvatarElement) {
        baseElement = element
        user.requiresSave()
    }

}