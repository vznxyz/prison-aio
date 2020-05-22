package net.evilblock.prisonaio.module.user.setting

import net.evilblock.cubed.serialize.AbstractTypeSerializable

interface UserSettingOption : AbstractTypeSerializable {

    fun getName(): String

    fun <T> getValue(): T

}