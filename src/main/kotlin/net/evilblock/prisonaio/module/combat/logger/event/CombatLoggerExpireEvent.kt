/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.logger.event

import net.evilblock.cubed.plugin.PluginEvent
import net.evilblock.prisonaio.module.combat.logger.CombatLogger

class CombatLoggerExpireEvent(val logger: CombatLogger) : PluginEvent()