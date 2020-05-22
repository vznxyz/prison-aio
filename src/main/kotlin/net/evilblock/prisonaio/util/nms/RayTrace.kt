package net.evilblock.prisonaio.util.nms

import org.bukkit.Effect
import org.bukkit.World
import org.bukkit.util.Vector
import java.util.*

class RayTrace(
    /**
     * The start position
     */
    var origin: Vector,
    /**
     * The direction in which this ray-trace flows.
     */
    var direction: Vector
) {

    /**
     * Gets a point on this ray-trace at [blocksAway] blocks aways.
     */
    fun getPosition(blocksAway: Double): Vector {
        return origin.clone().add(direction.clone().multiply(blocksAway))
    }

    /**
     * Checks if a position is within this ray-trace.
     */
    fun isOnLine(position: Vector): Boolean {
        val t = (position.x - origin.x) / direction.x
        return position.blockY.toDouble() == origin.y + t * direction.y && position.blockZ.toDouble() == origin.z + t * direction.z
    }

    /**
     * Gets all positions of this ray-trace.
     */
    fun traverse(blocksAway: Double, accuracy: Double): ArrayList<Vector> {
        val positions = ArrayList<Vector>()
        var d = 0.0
        while (d <= blocksAway) {
            positions.add(getPosition(d))
            d += accuracy
        }
        return positions
    }

    /**
     * Gets the position of an intersection on this ray-trace.
     */
    fun positionOfIntersection(min: Vector, max: Vector, blocksAway: Double, accuracy: Double): Vector? {
        val positions = traverse(blocksAway, accuracy)
        for (position in positions) {
            if (intersects(position, min, max)) {
                return position
            }
        }
        return null
    }

    /**
     * Gets if a position intersects with this ray-trace.
     */
    fun intersects(min: Vector, max: Vector, blocksAway: Double, accuracy: Double): Boolean {
        val positions = traverse(blocksAway, accuracy)
        for (position in positions) {
            if (intersects(position, min, max)) {
                return true
            }
        }
        return false
    }

    /**
     * Gets the position of an intersection on this ray-trace.
     */
    fun positionOfIntersection(boundingBox: BoundingBox, blocksAway: Double, accuracy: Double): Vector? {
        val positions = traverse(blocksAway, accuracy)
        for (position in positions) {
            if (intersects(position, boundingBox.min, boundingBox.max)) {
                return position
            }
        }
        return null
    }

    /**
     * Gets if a position intersects with this ray-trace.
     */
    fun intersects(boundingBox: BoundingBox, blocksAway: Double, accuracy: Double): Boolean {
        val positions = traverse(blocksAway, accuracy)
        for (position in positions) {
            if (intersects(position, boundingBox.min, boundingBox.max)) {
                return true
            }
        }
        return false
    }

    /**
     * Debug effects
     */
    fun highlight(world: World, blocksAway: Double, accuracy: Double) {
        for (position in traverse(blocksAway, accuracy)) {
            world.playEffect(position.toLocation(world), Effect.COLOURED_DUST, 0)
        }
    }

    companion object {
        @JvmStatic
        fun intersects(position: Vector, min: Vector, max: Vector): Boolean {
            if (position.x < min.x || position.x > max.x) {
                return false
            } else if (position.y < min.y || position.y > max.y) {
                return false
            } else if (position.z < min.z || position.z > max.z) {
                return false
            }
            return true
        }
    }

}