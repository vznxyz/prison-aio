/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.event

import net.evilblock.cubed.plugin.PluginEvent
import net.evilblock.prisonaio.module.user.User

class AsyncPlayTimeSyncEvent(val user: User, val offset: Long) : PluginEvent()