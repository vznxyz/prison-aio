package net.evilblock.prisonaio.module.user.statistic

import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.event.PlayTimeSyncEvent

class UserStatistics(@Transient internal var user: User) {

    /**
     * The amount of blocks the user has mined.
     */
    private var blocksMined: Long = 0L

    /**
     * The amount of time, in milliseconds, that the user has played on the server.
     */
    private var playTime: Long = 0L

    /**
     * The timestamp that the user logged into the server, if currently logged in.
     */
    @Transient
    internal var lastPlayTimeSync: Long = 0L

    fun addBlocksMined(amount: Int) {
        blocksMined += amount
        user.requiresSave = true
    }

    fun getBlocksMined(): Long {
        return blocksMined
    }

    fun syncPlayTime() {
        val offset = getLivePlayTime() - playTime
        PlayTimeSyncEvent(user = user, offset = offset).call()

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

}