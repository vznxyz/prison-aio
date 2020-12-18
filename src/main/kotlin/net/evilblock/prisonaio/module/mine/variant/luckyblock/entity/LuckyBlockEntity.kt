/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.entity

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.CubedConfig
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.nms.DataWatcherUtil
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.cubed.util.nms.version.ObjectBasedDataWatcherUtil
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlock
import net.evilblock.prisonaio.module.mine.variant.luckyblock.serialize.LuckyBlockReferenceSerializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.floor

class LuckyBlockEntity(
    @JsonAdapter(LuckyBlockReferenceSerializer::class)
    val luckyBlock: LuckyBlock,
    location: Location
) : Entity(location) {

    override fun getTypeName(): String {
        return "LB-Float-Head"
    }

    override fun getDebugViewLocation(): Location {
        return location.clone().add(0.5, 0.2, 0.5)
    }

    override fun renderDebugInformation(): List<String> {
        return arrayListOf<String>().also { info ->
            info.addAll(super.renderDebugInformation())
            info.add("Block Type: ${luckyBlock.name}")
        }
    }

    override fun sendSpawnPackets(player: Player) {
        if (luckyBlock.skinSource == null) {
            return
        }

        val armorStandDataWatcher = DataWatcherUtil.new()
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_Z, 0.toByte())
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_aB, "")
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_aC, false)
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 0.toByte())
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_b, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_br)
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_c, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bs)
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_d, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bt)
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_e, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bu)
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_f, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bv)
        DataWatcherUtil.register(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_g, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bw)
        DataWatcherUtil.setFlag(armorStandDataWatcher, 5, true)
        DataWatcherUtil.setTypeFlag(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 1, true)
        DataWatcherUtil.setTypeFlag(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 4, false)
        DataWatcherUtil.setTypeFlag(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 8, true)
        DataWatcherUtil.setTypeFlag(armorStandDataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 10, true)

        val location = location.clone()
        location.x = floor(location.x) + 0.5
        location.y = floor(location.y) - 0.45
        location.z = floor(location.z) + 0.5

        val spawnArmorStandPacket = MinecraftProtocol.buildSpawnEntityLivingPacket(id, uuid, 30, location)
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "m", armorStandDataWatcher)

        val texture = CubedConfig.getNpcTexture(luckyBlock.skinSource!!).textureValue
        val helmet = ItemUtils.applySkullTexture(ItemStack(Material.SKULL_ITEM, 1, 3), texture)
        val setEquipmentPacket = MinecraftProtocol.buildEntityEquipmentPacket(id, MinecraftReflection.getEnumItemSlot("HEAD")!!, helmet)

        MinecraftProtocol.send(player, spawnArmorStandPacket, setEquipmentPacket)
    }

    override fun sendUpdatePackets(player: Player) {
        val texture = CubedConfig.getNpcTexture(luckyBlock.skinSource!!).textureValue
        val helmet = ItemUtils.applySkullTexture(ItemStack(Material.SKULL_ITEM, 1, 3), texture)
        val setEquipmentPacket = MinecraftProtocol.buildEntityEquipmentPacket(id, MinecraftReflection.getEnumItemSlot("HEAD")!!, helmet)
        MinecraftProtocol.send(player, setEquipmentPacket)
    }

}