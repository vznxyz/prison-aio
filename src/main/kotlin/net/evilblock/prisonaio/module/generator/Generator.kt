/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator

import com.intellectualcrafters.plot.`object`.PlotId
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.serialize.AbstractTypeSerializable
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.build.GeneratorBuild
import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel
import net.evilblock.prisonaio.module.generator.entity.GeneratorVillagerEntity
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import java.util.*

/**
 * This object represents a physical instance of a generator in the world.
 * It encompasses all of a generator's information, including the construction process.
 */
abstract class Generator(
    val plotId: PlotId,
    val owner: UUID,
    val bounds: Cuboid,
    val rotation: Rotation
) : AbstractTypeSerializable {

    val instanceId: UUID = UUID.randomUUID()

    var level: Int = 0
    var lastTick: Long = System.currentTimeMillis()

    var build: GeneratorBuild = GeneratorBuild()

    @Transient
    lateinit var villagerEntity: GeneratorVillagerEntity

    fun initializeData() {
        build.generator = this

        val villagerOffset = getLevel().getSchematic(rotation)!!.villager
        val villagerLocation = bounds.lowerNE.clone().add(villagerOffset)

        villagerEntity = GeneratorVillagerEntity(villagerLocation)
        villagerEntity.generator = this
        villagerEntity.initializeData()
        villagerEntity.persistent = false

        EntityManager.trackEntity(villagerEntity)
    }

    fun getOwnerUsername(): String {
        return Cubed.instance.uuidCache.name(owner)
    }

    abstract fun getGeneratorType(): GeneratorType

    fun getLevel(): GeneratorBuildLevel {
        return getGeneratorType().getLevels()[level - 1]
    }

    fun getNextLevel(): GeneratorBuildLevel {
        return getGeneratorType().getLevels()[level]
    }

    open fun getTickInterval(): Long {
        return if (build.finished) {
            getLevel().tickInterval
        } else {
            build.getTickInterval()
        }
    }

    open fun tick() {
        if (!build.finished) {
            build.tick()
        }
    }

    fun startBuild() {
        build = GeneratorBuild()
        build.generator = this
        build.clearBlocks()
    }

    fun destroy() {
        EntityManager.forgetEntity(villagerEntity)
        EntityManager.forgetEntity(villagerEntity.hologram)

        villagerEntity.destroyForCurrentWatchers()
        villagerEntity.hologram.destroyForCurrentWatchers()

        build.finished = true
        build.clearBlocks()
    }

    override fun equals(other: Any?): Boolean {
        return other is Generator // check if an impl of Generator
                && other::class.java == this::class.java // check if same super type
                && other.plotId == this.plotId // check if same plot ID
                && other.instanceId == this.instanceId // check if same instance ID
    }

    override fun hashCode(): Int {
        var result = plotId.hashCode()
        result = 31 * result + instanceId.hashCode()
        return result
    }

}