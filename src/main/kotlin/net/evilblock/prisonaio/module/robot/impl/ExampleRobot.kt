package net.evilblock.prisonaio.module.robot.impl

import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.robot.Robot
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.tick.Tickable
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.lang.reflect.Type
import java.util.*
import kotlin.math.abs

class ExampleRobot(location: Location) : Robot(UUID.randomUUID(), location), Tickable {

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

    @Transient private var index: Int = 0
    @Transient private lateinit var task: BukkitTask

    override fun initializeData() {
        super.initializeData()

        task = startTask()

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
    }

    override fun getAbstractType(): Type {
        return ExampleRobot::class.java
    }

    override fun onDeletion() {
        if (!task.isCancelled) {
            task.cancel()
        }
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

    private fun startTask(): BukkitTask {
        return Tasks.asyncTimer(object : BukkitRunnable() {
            override fun run() {
                if (index >= RobotsModule.getTierKeys().size) {
                    index = 0
                }

                if (RobotsModule.getTierKeys().isEmpty()) {
                    return
                }

                val currentTier = RobotsModule.getTierKeys()[index++].toInt()

                if (!RobotsModule.hasTierTexture(currentTier) && !RobotsModule.hasTierArmorColor(currentTier)) {
                    restoreDefaultEquipment()
                    return
                }

                if (RobotsModule.hasTierTexture(currentTier)) {
                    val texture = RobotsModule.getTierTexture(currentTier)
                    updateHelmet(ItemUtils.applySkullTexture(ItemBuilder(Material.SKULL_ITEM).data(3).build(), texture.first))
                } else {
//                    updateHelmet(ItemUtils.getPlayerHeadItem(Cubed.instance.uuidCache.name(owner)))
                }

                if (RobotsModule.hasTierArmorColor(currentTier)) {
                    val armorColor = RobotsModule.getTierArmorColor(currentTier)
                    updateChestplate(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_CHESTPLATE), Color.fromRGB(armorColor)))
                    updateLeggings(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_LEGGINGS), Color.fromRGB(armorColor)))
                    updateBoots(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_BOOTS), Color.fromRGB(armorColor)))
                } else {
                    val color = Color.fromRGB(168, 169, 173)
                    updateChestplate(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_CHESTPLATE), color))
                    updateLeggings(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_LEGGINGS), color))
                    updateBoots(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_BOOTS), color))
                }

                val lines = arrayListOf<String>()

                lines.add(RobotsModule.getTierName(currentTier))

                if (currentTier == 0) {
                    lines.add("${ChatColor.GRAY}Regular")
                } else {
                    lines.add("${ChatColor.GRAY}Tier $currentTier")
                }

                hologram.updateLines(lines)
            }
        }, 40L, 40L)
    }

    override fun getHologramLines(): List<String> {
        return listOf("")
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