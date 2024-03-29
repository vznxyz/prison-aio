/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.impl.tutorial.progress

import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.module.quest.progress.QuestProgress

class TutorialProgress : QuestProgress(TutorialQuest) {

    var openedEnchantsMenu: Boolean = false

}