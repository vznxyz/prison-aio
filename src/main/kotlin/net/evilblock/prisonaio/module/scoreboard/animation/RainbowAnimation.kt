/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.scoreboard.animation

import org.bukkit.ChatColor

object RainbowAnimation : Runnable {

    private var index: Int = 0
    private var lastStage: Long = System.currentTimeMillis()

    private var colors: List<ChatColor> = listOf(
        ChatColor.DARK_RED,
        ChatColor.RED,
        ChatColor.GOLD,
        ChatColor.YELLOW,
        ChatColor.GREEN,
        ChatColor.DARK_GREEN,
        ChatColor.AQUA,
        ChatColor.BLUE,
        ChatColor.DARK_BLUE,
        ChatColor.LIGHT_PURPLE,
        ChatColor.DARK_PURPLE
        //&44&cc&66&ee&aa&22&bb&99&11&dd&55
    )

    private val offsets: Map<ChatColor, ChatColor> = mapOf(
        ChatColor.DARK_RED to ChatColor.RED,
        ChatColor.RED to ChatColor.DARK_RED,
        ChatColor.GOLD to ChatColor.YELLOW,
        ChatColor.YELLOW to ChatColor.GOLD,
        ChatColor.GREEN to ChatColor.DARK_GREEN,
        ChatColor.DARK_GREEN to ChatColor.GREEN,
        ChatColor.AQUA to ChatColor.BLUE,
        ChatColor.BLUE to ChatColor.DARK_BLUE,
        ChatColor.DARK_BLUE to ChatColor.BLUE,
        ChatColor.LIGHT_PURPLE to ChatColor.DARK_PURPLE,
        ChatColor.DARK_PURPLE to ChatColor.LIGHT_PURPLE
    )

    override fun run() {
        if (System.currentTimeMillis() - lastStage >= 100L) {
            if (index + 1 >= colors.size) {
                index = 0
            } else {
                index++
            }

            lastStage = System.currentTimeMillis()
        }
    }

    @JvmStatic
    fun getCurrentDisplay(): ChatColor {
        return colors[index]
    }

    @JvmStatic
    fun getOffset(color: ChatColor): ChatColor? {
        return offsets[color]
    }

}