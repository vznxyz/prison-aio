/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting

import net.evilblock.cubed.serialize.AbstractTypeSerializable

interface UserSettingOption : AbstractTypeSerializable {

    fun getName(): String

    fun <T> getValue(): T

}