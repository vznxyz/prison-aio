/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.core

import com.intellectualcrafters.plot.`object`.Plot
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorType
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import java.lang.reflect.Type
import java.util.*

class CoreGenerator(plot: Plot, owner: UUID, bounds: Cuboid, rotation: Rotation) : Generator(UUID.randomUUID(), plot.id, owner, bounds, rotation) {

    override fun getAbstractType(): Type {
        return CoreGenerator::class.java
    }

    override fun getGeneratorType(): GeneratorType {
        return GeneratorType.CORE
    }

    override fun tick() {
        super.tick()
    }

}