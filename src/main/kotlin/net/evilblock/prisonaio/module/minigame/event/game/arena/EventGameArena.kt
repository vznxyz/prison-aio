package net.evilblock.prisonaio.module.minigame.event.game.arena

import net.evilblock.prisonaio.module.minigame.event.game.EventGameType
import org.bukkit.Location

class EventGameArena(var name: String) {

    var pointA: Location? = null
    var pointB: Location? = null
    var spectatorLocation: Location? = null

    private val compatibleGameTypes: MutableSet<EventGameType> = hashSetOf()

    fun isSetup(): Boolean {
        return pointA != null && pointB != null && spectatorLocation != null && compatibleGameTypes.isNotEmpty()
    }

    fun isCompatible(gameType: EventGameType): Boolean {
        return compatibleGameTypes.contains(gameType)
    }

    fun makeCompatible(gameType: EventGameType) {
        compatibleGameTypes.add(gameType)
    }

    fun makeIncompatible(gameType: EventGameType) {
        compatibleGameTypes.remove(gameType)
    }

}