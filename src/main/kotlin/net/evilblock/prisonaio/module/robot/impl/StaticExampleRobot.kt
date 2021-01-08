package net.evilblock.prisonaio.module.robot.impl

import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.robot.Robot
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.thread.Tickable
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import java.util.*
import kotlin.math.abs

class StaticExampleRobot(location: Location, private val tier: Int) : Robot(UUID.randomUUID(), location), Tickable {

    // animation values
    @Transient private var lastTick: Long = System.currentTimeMillis()

    // head animation values
    @Transient private var headMod: Byte = 1
    @Transient private var minHeadRotationRange = -25
    @Transient private var maxHeadRotationRange = 25
    @Transient private var headModPerTick = (abs(minHeadRotationRange) + abs(maxHeadRotationRange)) / 10.0

    // arm animation values
    @Transient private var armMod: Byte = 1
    @Transient private var minArmRotationRange = -150
    @Transient private var maxArmRotationRange = 0
    @Transient private var armModPerTick = (abs(minArmRotationRange) + abs(maxArmRotationRange)) / 10.0

    override fun initializeData() {
        super.initializeData()

        persistent = false
        lastTick = System.currentTimeMillis()

        headMod = 1.toByte()
        minHeadRotationRange = -25
        maxHeadRotationRange = 25
        headModPerTick = (abs(minHeadRotationRange) + abs(maxHeadRotationRange)) / 10.0

        armMod = 1.toByte()
        minArmRotationRange = -150
        maxArmRotationRange = 0
        armModPerTick = (abs(minArmRotationRange) + abs(maxArmRotationRange)) / 10.0

        if (RobotsModule.hasTierTexture(tier)) {
            val texture = RobotsModule.getTierTexture(tier)
            updateHelmet(ItemUtils.applySkullTexture(ItemBuilder(Material.SKULL_ITEM).data(3).build(), texture.first))
        }

        if (RobotsModule.hasTierArmorColor(tier)) {
            val armorColor = RobotsModule.getTierArmorColor(tier)
            updateChestplate(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_CHESTPLATE), Color.fromRGB(armorColor)))
            updateLeggings(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_LEGGINGS), Color.fromRGB(armorColor)))
            updateBoots(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_BOOTS), Color.fromRGB(armorColor)))
        }
    }

    override fun getAbstractType(): Type {
        return StaticExampleRobot::class.java
    }

    override fun getTickInterval(): Long {
        return 100L
    }

    override fun getLastTick(): Long {
        return lastTick
    }

    override fun updateLastTick() {
        lastTick = System.currentTimeMillis()
    }

    override fun tick() {
        if (Bukkit.isPrimaryThread()) {
            throw IllegalStateException("Cannot tick robot on main thread")
        }

        updateHeadPose(headPose.setZ(Math.toRadians(Math.toDegrees(headPose.z) + (headMod * headModPerTick))))
        updateRightArmPose(rightArmPose.setX(Math.toRadians(Math.toDegrees(rightArmPose.x) + (armMod * armModPerTick))))

        val headZDegrees = Math.toDegrees(headPose.z)
        if (headZDegrees >= maxHeadRotationRange) {
            headMod = -1
        } else if (headZDegrees <= minHeadRotationRange) {
            headMod = 1
        }

        val rightArmXDegrees = Math.toDegrees(rightArmPose.x)
        if (rightArmXDegrees >= maxArmRotationRange) {
            armMod = -1
        } else if (rightArmXDegrees <= minArmRotationRange) {
            armMod = 1
        }
    }

    override fun getHologramLines(): List<String> {
        return listOf(
            RobotsModule.getTierName(tier),
            "${ChatColor.GRAY}${ if (tier == 0) { "Regular" } else { "Tier $tier" } }"
        )
    }

    override fun onRightClick(player: Player) {
        if (RegionBypass.hasBypass(player) && player.gameMode == GameMode.CREATIVE) {
            ConfirmMenu("Delete Robot?") { confirmed ->
                if (confirmed) {
                    destroyForCurrentWatchers()
                    RobotHandler.forgetRobot(this)
                }
            }.openMenu(player)
        }
    }

}