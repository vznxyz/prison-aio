/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.rules

class GangRule(var title: String) {

    var description: MutableList<String> = arrayListOf()
    var order: Int = 0

}