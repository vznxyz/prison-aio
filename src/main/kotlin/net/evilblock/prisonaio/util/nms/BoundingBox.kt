package net.evilblock.prisonaio.util.nms

import net.minecraft.server.v1_12_R1.AxisAlignedBB
import org.bukkit.util.Vector

class BoundingBox {
    //min and max points of hit box
    var max: Vector
    var min: Vector

    internal constructor(min: Vector, max: Vector) {
        this.max = max
        this.min = min
    }

    internal constructor(bb: AxisAlignedBB) {
        min = Vector(bb.a, bb.b, bb.c)
        max = Vector(bb.d, bb.e, bb.f)
    }

    fun midPoint(): Vector {
        return max.clone().add(min).multiply(0.5)
    }
}