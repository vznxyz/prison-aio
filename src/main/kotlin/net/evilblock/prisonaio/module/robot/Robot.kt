package net.evilblock.prisonaio.module.robot

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.serialize.AbstractTypeSerializable
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.nms.DataWatcherUtil
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.version.ObjectBasedDataWatcherUtil
import net.evilblock.cubed.util.nms.wrapper.Vector3FWrapper
import net.minecraft.server.v1_12_R1.*
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import java.util.*

abstract class Robot(internal var owner: UUID, location: Location) : Entity(location), AbstractTypeSerializable {

    @Transient internal lateinit var hologram: HologramEntity
    @Transient internal lateinit var dataWatcher: DataWatcher

    @Transient internal var itemInHand: ItemStack? = null
    @Transient internal var helmet: ItemStack? = null
    @Transient internal var chestplate: ItemStack? = null
    @Transient internal var leggings: ItemStack? = null
    @Transient internal var boots: ItemStack? = null

    internal var headPose: EulerAngle = EulerAngle(0.0, 0.0, 0.0)
    internal var bodyPose: EulerAngle = EulerAngle(0.0, 0.0, 0.0)
    internal var leftArmPose: EulerAngle = EulerAngle(0.0, 0.0, 0.0)
    internal var rightArmPose: EulerAngle = EulerAngle(0.0, 0.0, 0.0)
    internal var leftLegPose: EulerAngle = EulerAngle(0.0, 0.0, 0.0)
    internal var rightLegPose: EulerAngle = EulerAngle(0.0, 0.0, 0.0)

    override fun getTypeName(): String {
        return "Robot"
    }

    override fun initializeData() {
        hologram = RobotHologram(this)

        super.initializeData()

        dataWatcher = buildDataWatcher()

        refreshHologramLines()
        EntityManager.trackEntity(hologram)
    }

    override fun isMultiPartEntity(): Boolean {
        return true
    }

    override fun isRootOfMultiPartEntity(): Boolean {
        return true
    }

    override fun getDebugViewLocation(): Location {
        return hologram.getDebugViewLocation()
    }

    override fun getChildEntities(): Set<Entity> {
        return setOf(hologram)
    }

    fun restoreDefaultEquipment() {
        updateItemInHand(ItemStack(Material.DIAMOND_PICKAXE).also { GlowEnchantment.addGlow(it) })
//        updateHelmet(ItemUtils.getPlayerHeadItem(Cubed.instance.uuidCache.name(owner)))

        val color = Color.fromRGB(168, 169, 173)
        updateChestplate(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_CHESTPLATE), color))
        updateLeggings(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_LEGGINGS), color))
        updateBoots(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_BOOTS), color))
    }

    abstract fun getHologramLines(): List<String>

    fun refreshHologramLines() {
        hologram.updateLines(getHologramLines())
    }

    open fun getLocationAdjustmentForHologram(location: Location): Location {
        return location
    }

    private fun buildDataWatcher(): DataWatcher {
        val dataWatcher = DataWatcher(null as net.minecraft.server.v1_12_R1.Entity?)

        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_Z, 0.toByte())
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_aB, "")
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_aC, false)

        // register type flags
        // 0x01	 is Small
        // 0x04	 has Arms
        // 0x08	 no BasePlate
        // 0x10	 set Marker
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 0.toByte())
        DataWatcherUtil.setTypeFlag(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 1, true)
        DataWatcherUtil.setTypeFlag(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 4, true)
        DataWatcherUtil.setTypeFlag(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 8, true)
        DataWatcherUtil.setTypeFlag(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_a, 10, true)

        // register poses
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_b, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_br)
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_c, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bs)
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_d, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bt)
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_e, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bu)
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_f, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bv)
        DataWatcherUtil.register(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_g, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_bw)

        dataWatcher.e()

        return dataWatcher
    }

    override fun sendSpawnPackets(player: Player) {
        val spawnArmorStandPacket = PacketPlayOutSpawnEntityLiving()
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "a", id)
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "b", uuid)
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "c", 30) // entity type
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "d", location.x)
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "e", location.y)
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "f", location.z)
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "j", MathHelper.d(location.yaw * 256.0F / 360.0F).toByte())
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "k", MathHelper.d(location.pitch * 256.0F / 360.0F).toByte())
        Reflection.setDeclaredFieldValue(spawnArmorStandPacket, "m", dataWatcher)

        MinecraftProtocol.send(player, spawnArmorStandPacket)

        if (itemInHand != null) {
            MinecraftProtocol.send(player, makeEquipmentPacket(EnumItemSlot.MAINHAND, itemInHand!!))
        }

        if (helmet != null) {
            MinecraftProtocol.send(player, makeEquipmentPacket(EnumItemSlot.HEAD, helmet!!))
        }

        if (chestplate != null) {
            MinecraftProtocol.send(player, makeEquipmentPacket(EnumItemSlot.CHEST, chestplate!!))
        }

        if (leggings != null) {
            MinecraftProtocol.send(player, makeEquipmentPacket(EnumItemSlot.LEGS, leggings!!))
        }

        if (boots != null) {
            MinecraftProtocol.send(player, makeEquipmentPacket(EnumItemSlot.FEET, boots!!))
        }
    }

    override fun sendUpdatePackets(player: Player) {
        val entityMetadataPacket = PacketPlayOutEntityMetadata()
        Reflection.setDeclaredFieldValue(entityMetadataPacket, "a", id)
        Reflection.setDeclaredFieldValue(entityMetadataPacket, "b", dataWatcher.c() as Any)

        MinecraftProtocol.send(player, entityMetadataPacket)
    }

    override fun sendDestroyPackets(player: Player) {
        val entityDestroyPacket = PacketPlayOutEntityDestroy()
        Reflection.setDeclaredFieldValue(entityDestroyPacket, "a", intArrayOf(id))

        val playerConnection = (player as CraftPlayer).handle.playerConnection
        playerConnection.sendPacket(entityDestroyPacket)

        hologram.destroy(player)
    }

    fun updateItemInHand(itemStack: ItemStack) {
        itemInHand = itemStack.clone()

        val packet = makeEquipmentPacket(EnumItemSlot.MAINHAND, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updateHelmet(itemStack: ItemStack) {
        helmet = itemStack.clone()

        val packet = makeEquipmentPacket(EnumItemSlot.HEAD, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updateChestplate(itemStack: ItemStack) {
        chestplate = itemStack.clone()

        val packet = makeEquipmentPacket(EnumItemSlot.CHEST, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updateLeggings(itemStack: ItemStack) {
        leggings = itemStack.clone()

        val packet = makeEquipmentPacket(EnumItemSlot.LEGS, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updateBoots(itemStack: ItemStack) {
        boots = itemStack.clone()

        val packet = makeEquipmentPacket(EnumItemSlot.FEET, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    private fun makeEquipmentPacket(slot: EnumItemSlot, itemStack: ItemStack): Packet<*> {
        val packet = PacketPlayOutEntityEquipment()
        Reflection.setDeclaredFieldValue(packet, "a", id)
        Reflection.setDeclaredFieldValue(packet, "b", slot)
        Reflection.setDeclaredFieldValue(packet, "c", CraftItemStack.asNMSCopy(itemStack))
        return packet
    }

    fun updateHeadPose(angle: EulerAngle) {
        headPose = angle
        DataWatcherUtil.set(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_b, Vector3FWrapper(Math.toDegrees(headPose.x).toFloat(), Math.toDegrees(headPose.y).toFloat(), Math.toDegrees(headPose.z).toFloat()).vector)
        dataWatcher.e()
        updateForCurrentWatchers()
    }

    fun updateBodyPose(angle: EulerAngle) {
        bodyPose = angle
        DataWatcherUtil.set(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_c, Vector3FWrapper(Math.toDegrees(bodyPose.x).toFloat(), Math.toDegrees(bodyPose.y).toFloat(), Math.toDegrees(bodyPose.z).toFloat()).vector)
        dataWatcher.e()
        updateForCurrentWatchers()
    }

    fun updateLeftArmPose(angle: EulerAngle) {
        leftArmPose = angle
        DataWatcherUtil.set(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_d, Vector3FWrapper(Math.toDegrees(leftArmPose.x).toFloat(), Math.toDegrees(leftArmPose.y).toFloat(), Math.toDegrees(leftArmPose.z).toFloat()).vector)
        dataWatcher.e()
        updateForCurrentWatchers()
    }

    fun updateRightArmPose(angle: EulerAngle) {
        rightArmPose = angle
        DataWatcherUtil.set(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_e, Vector3FWrapper(Math.toDegrees(rightArmPose.x).toFloat(), Math.toDegrees(rightArmPose.y).toFloat(), Math.toDegrees(rightArmPose.z).toFloat()).vector)
        dataWatcher.e()
        updateForCurrentWatchers()
    }

    fun updateLeftLegPose(angle: EulerAngle) {
        leftLegPose = angle
        DataWatcherUtil.set(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_f, Vector3FWrapper(Math.toDegrees(leftLegPose.x).toFloat(), Math.toDegrees(leftLegPose.y).toFloat(), Math.toDegrees(leftLegPose.z).toFloat()).vector)
        dataWatcher.e()
        updateForCurrentWatchers()
    }

    fun updateRightLegPose(angle: EulerAngle) {
        rightLegPose = angle
        DataWatcherUtil.set(dataWatcher, ObjectBasedDataWatcherUtil.ENTITY_ARMOR_STAND_g, Vector3FWrapper(Math.toDegrees(rightLegPose.x).toFloat(), Math.toDegrees(rightLegPose.y).toFloat(), Math.toDegrees(rightLegPose.z).toFloat()).vector)
        dataWatcher.e()
        updateForCurrentWatchers()
    }

}