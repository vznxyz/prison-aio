package net.evilblock.prisonaio.module.user.event

import net.evilblock.prisonaio.module.PluginEvent
import net.evilblock.prisonaio.module.user.User

class PlayTimeSyncEvent(val user: User, val offset: Long) : PluginEvent()