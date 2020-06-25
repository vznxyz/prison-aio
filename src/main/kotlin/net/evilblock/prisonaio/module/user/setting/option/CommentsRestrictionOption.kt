/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.setting.option

import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import java.lang.reflect.Type

class CommentsRestrictionOption(val restriction: RestrictionOptionValue) : UserSettingOption {

    override fun getName(): String {
        return restriction.getDisplayName()
    }

    override fun <T> getValue(): T {
        return restriction as T
    }

    override fun getAbstractType(): Type {
        return CommentsRestrictionOption::class.java
    }

    override fun equals(other: Any?): Boolean {
        return other is CommentsRestrictionOption && other.restriction == restriction
    }

    override fun hashCode(): Int {
        return restriction.hashCode()
    }

    enum class RestrictionOptionValue(private val displayName: String) {

        ALLOWED("Allow comments on my profile"),
        DISABLED("Don't allow comments on my profile");

        fun getDisplayName(): String {
            return displayName
        }

    }

}