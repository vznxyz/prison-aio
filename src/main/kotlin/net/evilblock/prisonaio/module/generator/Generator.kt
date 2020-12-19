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
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.build.GeneratorBuild
import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel
import net.evilblock.prisonaio.module.generator.entity.GeneratorVillagerEntity
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifier
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifierType
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifierUtils
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * This object represents a physical instance of a generator in the world.
 * It encompasses all of a generator's information, including the construction process.
 */
abstract class Generator(
    val instanceId: UUID,
    val plotId: PlotId,
    val owner: UUID,
    val bounds: Cuboid,
    val rotation: Rotation
) : Region(id = "GEN-$instanceId"), AbstractTypeSerializable {

    var level: Int = 0
    var lastTick: Long = System.currentTimeMillis()

    var build: GeneratorBuild = GeneratorBuild()

    var modifierStorage: Array<ItemStack?> = arrayOfNulls(3)
    var modifiers: ConcurrentHashMap<GeneratorModifierType, GeneratorModifier> = ConcurrentHashMap()

    @Transient
    lateinit var villagerEntity: GeneratorVillagerEntity

    init {
        persistent = false
    }

    override fun getRegionName(): String {
        return "Generator #$instanceId"
    }

    override fun getPriority(): Int {
        return 50
    }

    override fun getCuboid(): Cuboid? {
        return bounds
    }

    open fun initializeData() {
        build.generator = this

        val schematic = getLevel().getSchematic(rotation)

        val villagerOffset = schematic.villager
        val villagerLocation = bounds.lowerNE.clone().add(villagerOffset)
        villagerLocation.yaw = schematic.villagerYaw

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

    open fun getLevel(): GeneratorBuildLevel {
        return getGeneratorType().getLevels()[level - 1]
    }

    open fun getNextLevel(): GeneratorBuildLevel? {
        return if (level >= getGeneratorType().getLevels().size) {
            null
        } else {
            getGeneratorType().getLevels()[level]
        }
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

        if (getMaxModifiers() > 0) {
            val expired = arrayListOf<GeneratorModifier>()
            for (modifier in modifiers.values) {
                if (modifier.isExpired()) {
                    expired.add(modifier)
                }
            }

            for (modifier in expired) {
                onRemoveModifier(modifier)
                modifiers.remove(modifier.type)
            }

            val activeModifiers = getActiveModifiers()
            if (activeModifiers.size < getMaxModifiers()) {
                for (item in modifierStorage) {
                    if (item != null) {
                        val modifier = GeneratorModifierUtils.extractModifierFromItemStack(item)
                        if (modifier != null) {
                            if (!hasActiveModifier(modifier.type)) {
                                modifiers[modifier.type] = modifier

                                if (modifier.type.durationBased) {
                                    removeModifierItem(item, 1)
                                }

                                onApplyModifier(modifier)

                                if (getActiveModifiers().size >= getMaxModifiers()) {
                                    break
                                }
                            }
                        }
                    }
                }
            }
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

    open fun hasItemStorage(): Boolean {
        return false
    }

    open fun getItemStorage(): MutableList<ItemStack> {
        return arrayListOf()
    }

    open fun removeItemFromStorage(player: Player, itemStack: ItemStack): Int {
        val originalAmount = itemStack.amount
        var remainingAmount = itemStack.amount

        val removed = arrayListOf<ItemStack>()
        for (stored in getItemStorage()) {
            if (stored.isSimilar(itemStack)) {
                if (stored.amount > remainingAmount) {
                    stored.amount -= remainingAmount
                    break
                } else {
                    removed.add(stored)
                    remainingAmount -= stored.amount
                }
            }
        }

        for (item in removed) {
            getItemStorage().remove(item)
        }

        if (originalAmount > remainingAmount) {
            player.inventory.addItem(ItemBuilder.copyOf(itemStack).amount(originalAmount - remainingAmount).build())
            player.updateInventory()
        }

        return remainingAmount
    }

    abstract fun getMaxModifiers(): Int

    open fun getActiveModifiers(): Array<GeneratorModifier?> {
        return modifiers.values.toTypedArray()
    }

    fun hasActiveModifier(type: GeneratorModifierType): Boolean {
        return modifiers.containsKey(type)
    }

    fun getActiveModifier(type: GeneratorModifierType): GeneratorModifier? {
        return modifiers[type]
    }

    open fun isModifierCompatible(type: GeneratorModifierType): Boolean {
        if (!build.finished) {
            return type == GeneratorModifierType.SPEED
        }

        return false
    }

    open fun onApplyModifier(modifier: GeneratorModifier) {
        if (!build.finished) {
            if (modifier.type == GeneratorModifierType.SPEED) {
                build.speed = modifier.value
            }
        }
    }

    open fun onRemoveModifier(modifier: GeneratorModifier) {
        if (!build.finished) {
            if (modifier.type == GeneratorModifierType.SPEED) {
                build.speed = 1.0
            }
        }
    }

    /**
     * Tries to remove the given [amount] of [itemStack] from the [modifierStorage].
     * Returns the amount of the [itemStack] that was removed.
     */
    fun removeModifierItem(itemStack: ItemStack, amount: Int): Int {
        val originalAmount = amount
        var remainingAmount = amount

        for (slot in modifierStorage.indices) {
            val itemInSlot = modifierStorage[slot] ?: continue

            if (!ItemUtils.isSimilar(itemInSlot, itemStack) || !ItemUtils.hasSameLore(itemInSlot, itemStack) || !ItemUtils.hasSameEnchantments(itemInSlot, itemStack)) {
                continue
            }

            if (remainingAmount >= itemInSlot.amount) {
                remainingAmount -= itemInSlot.amount
                modifierStorage[slot] = null
            } else {
                itemInSlot.amount = itemInSlot.amount - remainingAmount
                break
            }
        }

        return originalAmount - remainingAmount
    }

    open fun renderInformation(): MutableList<String> {
        return arrayListOf<String>().also { info ->
            if (!build.finished) {
                renderBuildInfo(info)
            }
        }
    }

    private fun renderBuildInfo(info: MutableList<String>) {
        info.add("${ChatColor.GRAY}Speed: ${ChatColor.AQUA}${NumberUtils.formatDecimal(build.speed)}")
        info.add("${ChatColor.GRAY}Progress: ${ChatColor.RED}${build.renderRemainingTime()}")
        info.add("${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}${build.renderProgressBar()}${ChatColor.GRAY}${Constants.THICK_VERTICAL_LINE}")
    }

    internal open fun giveItem(player: Player, item: ItemStack): Int {
        val remaining = player.inventory.addItem(item)
        if (remaining.isEmpty()) {
            return 0
        } else {
            for ((_, value) in remaining) {
                return value.amount
            }
            return 0
        }
    }

    override fun onEntityDamage(entity: Entity, cause: EntityDamageEvent.DamageCause, cancellable: Cancellable) {
        cancellable.isCancelled = true
        println(entity.name + " - " + cause.name)
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