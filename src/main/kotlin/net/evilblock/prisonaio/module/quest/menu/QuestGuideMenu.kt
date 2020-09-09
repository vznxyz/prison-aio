/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.quest.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.quest.progress.QuestProgress
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class QuestGuideMenu : Menu() {

    override fun getTitle(player: Player): String {
        return "Quest Guide"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        val user = UserHandler.getUser(player.uniqueId)
        for (questProgress in user.getQuestProgressions().sortedBy(questOrderSort)) {
            buttons[buttons.size] = QuestButton(questProgress)
        }

        return buttons
    }

    private inner class QuestButton(private val progress: QuestProgress) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}${progress.quest.getName()}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            when {
                progress.isCompleted() -> description.add("${ChatColor.GREEN}${ChatColor.BOLD}COMPLETED")
                progress.hasStarted() -> description.add("${ChatColor.YELLOW}${ChatColor.BOLD}IN PROGRESS")
                else -> description.add("${ChatColor.RED}${ChatColor.BOLD}NOT STARTED")
            }

            description.add("")

            when {
                progress.isCompleted() -> {
                    description.add("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}REWARDS")

                    for (reward in progress.quest.getRewardsText()) {
                        description.add(" $reward")
                    }
                }
                progress.hasStarted() -> {
                    description.add("${ChatColor.YELLOW}${ChatColor.BOLD}${progress.getCurrentMission().getName()}")

                    TextSplitter.split(40, progress.getCurrentMission().getMissionText(player), "${ChatColor.GRAY}", " ").forEach { text ->
                        description.add(text)
                    }
                }
                else -> {
                    TextSplitter.split(40, progress.quest.getStartText(), "${ChatColor.GRAY}", " ").forEach { text ->
                        description.add(text)
                    }
                }
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENCHANTED_BOOK
        }
    }

    companion object {
        private val questOrderSort: (QuestProgress) -> Int? = { progress ->
            when {
                progress.isCompleted() -> 1
                progress.hasStarted() -> 2
                else -> 3
            }
        }
    }

}