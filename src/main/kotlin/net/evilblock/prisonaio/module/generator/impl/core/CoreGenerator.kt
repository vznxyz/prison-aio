/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.core

import com.intellectualcrafters.plot.`object`.Plot
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.generator.GeneratorType
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import org.bukkit.ChatColor
import java.lang.reflect.Type
import java.util.*

class CoreGenerator(plot: Plot, owner: UUID, bounds: Cuboid, rotation: Rotation) : Generator(UUID.randomUUID(), plot.id, owner, bounds, rotation) {

    override fun getAbstractType(): Type {
        return CoreGenerator::class.java
    }

    override fun getGeneratorType(): GeneratorType {
        return GeneratorType.CORE
    }

    override fun getLevel(): CoreBuildLevel {
        return super.getLevel() as CoreBuildLevel
    }

    override fun getNextLevel(): CoreBuildLevel? {
        return super.getNextLevel() as CoreBuildLevel?
    }

    override fun getMaxModifiers(): Int {
        return 1
    }

    override fun renderInformation(): MutableList<String> {
        return super.renderInformation().also { desc ->
            if (build.finished) {
                val coreLevel = getLevel()
                val generators = GeneratorHandler.getGeneratorsByPlot(plotId).filter { it !is CoreGenerator }

                desc.add("${ChatColor.GRAY}Builds: ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.format(generators.size)}${ChatColor.GRAY}/${NumberUtils.format(coreLevel.maxBuilds)}")

                if (generators.isNotEmpty()) {
                    desc.add("")

                    for (generator in generators) {
                        val level = NumberUtils.format(generator.level)

                        if (generator.build.finished) {
                            desc.add("${generator.getGeneratorType().getColoredName()} ${ChatColor.GRAY}(Lvl $level)")
                        } else {
                            val countdown = TimeUtil.formatIntoAbbreviatedString((generator.build.getRemainingTime() / 1000.0).toInt())
                            desc.add("${generator.getGeneratorType().getColoredName()} ${ChatColor.GRAY}(Lvl $level)")
                            desc.add("${ChatColor.GRAY}(Building... ${ChatColor.RED}$countdown${ChatColor.GRAY})")
                        }
                    }
                }

                desc.add("")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}Core Limits")
                desc.add("${ChatColor.GRAY}Money Generator: ${ChatColor.RED}${ChatColor.BOLD}${NumberUtils.format(coreLevel.maxMoneyLevel)}")
                desc.add("${ChatColor.GRAY}Token Generator: ${ChatColor.RED}${ChatColor.BOLD}${NumberUtils.format(coreLevel.maxTokenLevel)}")
                desc.add("${ChatColor.GRAY}Key Generator: ${ChatColor.RED}${ChatColor.BOLD}${NumberUtils.format(coreLevel.maxKeyLevel)}")
            }
        }
    }

}