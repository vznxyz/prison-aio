/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.statistic

import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.event.AsyncPlayTimeSyncEvent

class UserStatistics(@Transient internal var user: User) {

    /**
     * The amount of blocks the user has mined.
     */
    private var blocksMined: Int = 0

    /**
     * The amount of blocks the user has mined at each mine.
     */
    private val blocksMinedAtMines: MutableMap<String, Int> = hashMapOf()

    /**
     * The amount of time, in milliseconds, that the user has played on the server.
     */
    private var playTime: Long = 0L

    /**
     * This user's kills count.
     */
    private var kills: Int = 0

    /**
     * This user's deaths count.
     */
    private var deaths: Int = 0

    /**
     * The timestamp that the user logged into the server, if currently logged in.
     */
    @Transient
    internal var lastPlayTimeSync: Long = 0L

    fun getBlocksMined(): Int {
        return blocksMined
    }

    fun setBlocksMined(amount: Int) {
        blocksMined = amount
        user.requiresSave = true
    }

    fun addBlocksMined(amount: Int) {
        blocksMined += amount
        user.requiresSave = true
    }

    fun getBlocksMinedAtMine(mine: Mine): Int {
        return blocksMinedAtMines.getOrDefault(mine.id.toLowerCase(), 0)
    }

    fun setBlocksMinedAtMine(mine: Mine, amount: Int) {
        blocksMinedAtMines[mine.id.toLowerCase()] = amount
        user.requiresSave = true
    }

    fun addBlocksMinedAtMine(mine: Mine, amount: Int) {
        val previous = blocksMinedAtMines.getOrDefault(mine.id.toLowerCase(), 0)
        blocksMinedAtMines[mine.id.toLowerCase()] = previous + amount
        user.requiresSave = true
    }

    fun syncPlayTime() {
        val offset = getLivePlayTime() - playTime
        AsyncPlayTimeSyncEvent(user = user, offset = offset).call()

        playTime = getLivePlayTime()
        lastPlayTimeSync = System.currentTimeMillis()
        user.requiresSave = true
    }

    fun getPlayTime(): Long {
        return playTime
    }

    fun getLivePlayTime(): Long {
        return if (lastPlayTimeSync == 0L) {
            playTime
        } else {
            playTime + (System.currentTimeMillis() - lastPlayTimeSync)
        }
    }

    fun setPlayTime(time: Long) {
        playTime = time
        user.requiresSave = true
    }

    fun getKills(): Int {
        return kills
    }

    fun addKill() {
        kills++
        user.requiresSave = true
    }

    fun getDeaths(): Int {
        return deaths
    }

    fun addDeath() {
        deaths++
        user.requiresSave = true
    }

}