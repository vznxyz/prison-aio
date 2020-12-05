/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.schematic.rotate

enum class Rotation(private val helper: RotationHelper) {

    NORTH(NorthRotator()),
    EAST(EastRotator()),
    SOUTH(SouthRotator()),
    WEST(WestRotator());

    open fun <T> rotate(matrix: Array<Array<Array<T>>>, clazz: Class<Array<Array<Array<T>>>>): Array<Array<Array<T>>> {
        return helper.apply(matrix, clazz)
    }

    open fun getOpposite(): Rotation {
        return when (this) {
            EAST -> WEST
            WEST -> EAST
            NORTH -> SOUTH
            SOUTH -> NORTH
        }
    }

    private interface RotationHelper {
        fun <T> apply(matrix: Array<Array<Array<T>>>, clazz: Class<Array<Array<Array<T>>>>): Array<Array<Array<T>>>
    }

    private class NorthRotator : RotationHelper {
        override fun <T> apply(matrix: Array<Array<Array<T>>>, clazz: Class<Array<Array<Array<T>>>>): Array<Array<Array<T>>> {
            val ret = clazz.cast(java.lang.reflect.Array.newInstance(clazz.componentType.componentType.componentType, matrix.size, matrix[0].size, matrix[0][0].size))
            for (x in matrix.indices) {
                for (y in matrix[x].indices) {
                    System.arraycopy(matrix[x][y], 0, ret[x][y], 0, matrix[x][y].size)
                }
            }
            return ret
        }
    }

    private class EastRotator : RotationHelper {
        override fun <T> apply(matrix: Array<Array<Array<T>>>, clazz: Class<Array<Array<Array<T>>>>): Array<Array<Array<T>>> {
            val ret = clazz.cast(java.lang.reflect.Array.newInstance(clazz.componentType.componentType.componentType, matrix[0][0].size, matrix[0].size, matrix.size))
            for (x in matrix.indices) {
                for (y in matrix[x].indices) {
                    for (z in matrix[x][y].indices) {
                        ret[matrix[x][y].size - 1 - z][y][x] = matrix[x][y][z]
                    }
                }
            }
            return ret
        }
    }

    private class SouthRotator : RotationHelper {
        override fun <T> apply(matrix: Array<Array<Array<T>>>, clazz: Class<Array<Array<Array<T>>>>): Array<Array<Array<T>>> {
            val ret = clazz.cast(java.lang.reflect.Array.newInstance(clazz.componentType.componentType.componentType, matrix.size, matrix[0].size, matrix[0][0].size))
            for (x in matrix.indices) {
                for (y in matrix[x].indices) {
                    for (z in matrix[x][y].indices) {
                        ret[matrix.size - 1 - x][y][matrix[x][y].size - 1 - z] = matrix[x][y][z]
                    }
                }
            }
            return ret
        }
    }

    private class WestRotator : RotationHelper {
        override fun <T> apply(matrix: Array<Array<Array<T>>>, clazz: Class<Array<Array<Array<T>>>>): Array<Array<Array<T>>> {
            val ret = clazz.cast(java.lang.reflect.Array.newInstance(clazz.componentType.componentType.componentType, matrix[0][0].size, matrix[0].size, matrix.size))
            for (x in matrix.indices) {
                for (y in matrix[x].indices) {
                    for (z in matrix[x][y].indices) {
                        ret[z][y][matrix.size - 1 - x] = matrix[x][y][z]
                    }
                }
            }
            return ret
        }
    }

}