package net.evilblock.prisonaio.util.particle

import net.minecraft.server.v1_12_R1.EnumParticle
import org.bukkit.Location

data class ParticleMeta(val location: Location, val particle: EnumParticle) {

    constructor(location: Location,
                particle: EnumParticle,
                deltaX: Float,
                deltaY: Float,
                deltaZ: Float,
                speed: Float,
                amount: Int) : this(location, particle) {
        this.offsetX = deltaX
        this.offsetY = deltaY
        this.offsetZ = deltaZ
        this.speed = speed
        this.amount = amount
    }

    var offsetX = 0.0F
    var offsetY = 0.0F
    var offsetZ = 0.0F
    var speed = 1.0F
    var amount = 1

}