/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.key

import com.intellectualcrafters.plot.`object`.Plot
import net.evilblock.crates.CrateHandler
import net.evilblock.crates.key.CrateKeyHandler
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorType
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifier
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifierType
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import java.util.*

class KeyGenerator(plot: Plot, owner: UUID, bounds: Cuboid, rotation: Rotation) : Generator(UUID.randomUUID(), plot.id, owner, bounds, rotation) {

    private var multiplier = 1.0
    private val autoCollected: ArrayList<ItemStack> = arrayListOf()
    private val keyStorage: ArrayList<ItemStack> = arrayListOf()

    override fun getAbstractType(): Type {
        return KeyGenerator::class.java
    }

    override fun getGeneratorType(): GeneratorType {
        return GeneratorType.KEY
    }

    override fun getLevel(): KeyBuildLevel {
        return super.getLevel() as KeyBuildLevel
    }

    override fun getNextLevel(): KeyBuildLevel? {
        return super.getNextLevel() as KeyBuildLevel?
    }

    override fun hasItemStorage(): Boolean {
        return true
    }

    override fun getItemStorage(): MutableList<ItemStack> {
        return keyStorage
    }

    override fun getMaxModifiers(): Int {
        return 3
    }

    override fun isModifierCompatible(type: GeneratorModifierType): Boolean {
        return if (build.finished) {
            type == GeneratorModifierType.MULTIPLIER || type == GeneratorModifierType.AUTO_COLLECT
        } else {
            super.isModifierCompatible(type)
        }
    }

    override fun onApplyModifier(modifier: GeneratorModifier) {
        if (build.finished) {
            if (modifier.type == GeneratorModifierType.MULTIPLIER) {
                multiplier = modifier.value
            }
        } else {
            super.onApplyModifier(modifier)
        }
    }

    override fun onRemoveModifier(modifier: GeneratorModifier) {
        if (modifier.type == GeneratorModifierType.MULTIPLIER) {
            multiplier = 1.0
        }
    }

    override fun tick() {
        super.tick()

        if (!build.finished) {
            return
        }

        val storedKeys = getStoredKeys()

        val level = getLevel()
        if (storedKeys < level.maxKeys) {
            val pickedKey = level.pickRandomKey()
            val crate = CrateHandler.getCrateById(pickedKey.key)
            if (crate != null) {
                keyStorage.add(CrateKeyHandler.makeKeyItemStack(amount = 1, crate = crate, issuedBy = null, reason = "Key Generator"))
            }
        }

        val player = Bukkit.getPlayer(owner)

        if (hasActiveModifier(GeneratorModifierType.AUTO_COLLECT) && keyStorage.isNotEmpty()) {
            val iterator = keyStorage.iterator()
            while (iterator.hasNext()) {
                val keyItemStack = iterator.next()

                if (player != null) {
                    val remaining = giveItem(player, keyItemStack)
                    if (remaining != 0) {
                        keyItemStack.amount = remaining
                        autoCollected.add(keyItemStack)
                    }

                    iterator.remove()
                } else {
                    autoCollected.add(keyItemStack)
                    iterator.remove()
                }
            }
        }

        if (player != null && autoCollected.isNotEmpty()) {
            val items: MutableIterator<ItemStack> = autoCollected.iterator()
            while (items.hasNext()) {
                val item = items.next()
                val remaining = giveItem(player, item)
                if (remaining != 0) {
                    item.amount = remaining
                } else {
                    items.remove()
                }
            }
        }
    }

    override fun renderInformation(): MutableList<String> {
        return super.renderInformation().also { info ->
            if (build.finished) {
                renderGeneratingInfo(info)
            }
        }
    }

    private fun renderGeneratingInfo(info: MutableList<String>) {
        val level = getLevel()
        val storage = getStoredKeys()

        info.add("${ChatColor.GRAY}Multiplier: ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.formatDecimal(multiplier)}x")

        info.add(buildString {
            append("${ChatColor.GRAY}Storage: ")
            append("${ChatColor.YELLOW}${ChatColor.BOLD}")
            append(NumberUtils.format(storage))
            append("${ChatColor.GRAY}/")
            append(NumberUtils.format(level.maxKeys))

            if (storage >= level.maxKeys) {
                append(" ${ChatColor.RED}${ChatColor.BOLD}FULL")
            }
        })
    }

    private fun getStoredKeys(): Int {
        var storedKeys = 0
        for (item in keyStorage) {
            storedKeys += item.amount
        }
        return storedKeys
    }

}