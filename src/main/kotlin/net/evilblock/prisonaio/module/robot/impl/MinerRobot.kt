package net.evilblock.prisonaio.module.robot.impl

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.util.economy.Currency
import net.evilblock.prisonaio.module.robot.menu.ManageRobotMenu
import net.evilblock.prisonaio.module.robot.Robot
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.cosmetic.Cosmetic
import net.evilblock.prisonaio.module.robot.cosmetic.impl.SkinCosmetic
import net.evilblock.prisonaio.module.robot.impl.statistic.MinerRobotTimedEarnings
import net.evilblock.prisonaio.module.robot.impl.upgrade.Upgrade
import net.evilblock.prisonaio.module.robot.impl.upgrade.UpgradeManager
import net.evilblock.prisonaio.module.robot.impl.upgrade.impl.EfficiencyUpgrade
import net.evilblock.prisonaio.module.robot.impl.upgrade.impl.FortuneUpgrade
import net.evilblock.prisonaio.module.robot.serialize.AppliedCosmeticsSerializer
import net.evilblock.prisonaio.module.robot.serialize.AppliedUpgradesSerializer
import net.evilblock.prisonaio.module.robot.tick.Tickable
import net.evilblock.prisonaio.util.plot.PlotUtil
import net.minecraft.server.v1_12_R1.BlockPosition
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockBreakAnimation
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class MinerRobot(owner: UUID, location: Location) : Robot(owner = owner, location = location), Tickable {

    companion object {
        private const val ROBOT_TICKS = 600
    }

    internal var createdAt: Long = System.currentTimeMillis()

    var tier: Int = 0

    internal var rewardTicks: Int = ROBOT_TICKS

    internal var moneyTotalEarnings: BigDecimal = BigDecimal("0")
    internal var moneyEarnings: MinerRobotTimedEarnings = MinerRobotTimedEarnings()

    internal var tokensTotalEarnings: BigInteger = BigInteger("0")
    internal var tokenEarnings: MinerRobotTimedEarnings = MinerRobotTimedEarnings()

    internal var moneyOwed: Double = 0.0
    internal var tokensOwed: Long = 0

    internal var lastCollect: Long = System.currentTimeMillis()

    @JsonAdapter(AppliedUpgradesSerializer::class)
    internal var appliedUpgrades: MutableMap<Upgrade, Int> = hashMapOf()

    @JsonAdapter(AppliedCosmeticsSerializer::class)
    internal var enabledCosmetics: MutableList<Cosmetic> = arrayListOf()

    // animation values
    @Transient private var lastTick: Long = System.currentTimeMillis()

    // block animation values
    @Transient private var blockPhase: Int = 0

    // head animation values
    @Transient private var headMod: Byte = 1
    @Transient private var minHeadRotationRange = -25
    @Transient private var maxHeadRotationRange = 25
    @Transient private var headModPerTick = (50) / 10.0

    // arm animation values
    @Transient private var armMod: Byte = 1
    @Transient private var minArmRotationRange = -150
    @Transient private var maxArmRotationRange = 0
    @Transient private var armModPerTick = (150) / 10.0

    override fun initializeData() {
        super.initializeData()

        persistent = false
        lastTick = System.currentTimeMillis()

        blockPhase = 0

        headMod = 1.toByte()
        minHeadRotationRange = -25
        maxHeadRotationRange = 25
        headModPerTick = (50) / 10.0

        armMod = 1.toByte()
        minArmRotationRange = -150
        maxArmRotationRange = 0
        armModPerTick = (150) / 10.0

        for (cosmetic in enabledCosmetics) {
            cosmetic.onEnable(this)
        }

        updateItemInHand(ItemStack(Material.DIAMOND_PICKAXE).also { GlowEnchantment.addGlow(it) })

        if (RobotsModule.hasTierTexture(tier)) {
            val texture = RobotsModule.getTierTexture(tier)
            updateHelmet(ItemUtils.applySkullTexture(ItemBuilder(Material.SKULL_ITEM).data(3).build(), texture.first))
        }
//        updateHelmet(ItemUtils.getPlayerHeadItem(Cubed.instance.uuidCache.name(owner)))

        if (RobotsModule.hasTierArmorColor(tier)) {
            val armorColor = Color.fromRGB(RobotsModule.getTierArmorColor(tier))
            updateChestplate(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_CHESTPLATE), armorColor))
            updateLeggings(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_LEGGINGS), armorColor))
            updateBoots(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_BOOTS), armorColor))
        } else {
            val color = Color.fromRGB(168, 169, 173)
            updateChestplate(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_CHESTPLATE), color))
            updateLeggings(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_LEGGINGS), color))
            updateBoots(ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_BOOTS), color))
        }
    }

    override fun getAbstractType(): Type {
        return MinerRobot::class.java
    }

    fun getTierName(): String {
        return RobotsModule.getTierName(tier)
    }

    fun getOwnerName(): String {
        return Cubed.instance.uuidCache.name(owner)
    }

    override fun onRightClick(player: Player) {
        if (owner != player.uniqueId && !RobotHandler.isPrivileged(player, location)) {
            return
        }

        try {
            ManageRobotMenu(this).openMenu(player)
        } catch (e: Exception) {
            player.sendMessage("${ChatColor.RED}Technical difficulties! (Server Error)")
            e.printStackTrace()
        }
    }

    override fun sendDestroyPackets(player: Player) {
        super.sendDestroyPackets(player)

        val fakeBlock = getFakeBlockLocation()
        val blockPos = BlockPosition(fakeBlock.blockX, fakeBlock.blockY, fakeBlock.blockZ)

        Tasks.sync {
            val blockChangePacket = PacketPlayOutBlockChange()
            Reflection.setDeclaredFieldValue(blockChangePacket, "a", blockPos)
            Reflection.setDeclaredFieldValue(blockChangePacket, "block", (fakeBlock.world as CraftWorld).handle.getType(blockPos))

            MinecraftProtocol.send(player, blockChangePacket)
        }

        val blockBreakEntityId = (fakeBlock.x.toInt() and 0xFFF) shl 20 or (fakeBlock.z.toInt() and 0xFFF) shl 8 or fakeBlock.y.toInt() and 0xFF
        val blockBreakAnimationPacket = PacketPlayOutBlockBreakAnimation()
        Reflection.setDeclaredFieldValue(blockBreakAnimationPacket, "a", blockBreakEntityId)
        Reflection.setDeclaredFieldValue(blockBreakAnimationPacket, "b", BlockPosition(fakeBlock.blockX, fakeBlock.blockY, fakeBlock.blockZ))
        Reflection.setDeclaredFieldValue(blockBreakAnimationPacket, "c", 0)

        MinecraftProtocol.send(player, blockBreakAnimationPacket)
    }

    override fun getHologramLines(): List<String> {
        val lines = arrayListOf<String>()
        val ownerName = Cubed.instance.uuidCache.name(owner)

        if (tier == 0) {
            lines.add("${ChatColor.RED}${ChatColor.BOLD}$ownerName's Robot")
        } else {
            lines.add(ChatColor.translateAlternateColorCodes('&', RobotsModule.config.getString("tiers.${tier}.name", "&cDefault Tier $tier Name")))
            lines.add("${ChatColor.GRAY}Owned by $ownerName")
        }

        if (enabledCosmetics.isNotEmpty()) {
            for (cosmetic in enabledCosmetics) {
                if (cosmetic is SkinCosmetic) {
                    lines.add("${ChatColor.GRAY}(${cosmetic.getName()}${ChatColor.GRAY})")
                    break
                }
            }
        }

        return lines
    }

    override fun getTickInterval(): Long {
        return if (hasUpgradeApplied(EfficiencyUpgrade)) {
            val level = getUpgradeLevel(EfficiencyUpgrade)
            return 100L - (level * 3)
        } else {
            100L
        }
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

        tickRewards()

        if (RobotsModule.isAnimationsEnabled()) {
            tickArmorStandAnimation()
            tickBlockAnimation()
        }
    }

    private fun tickRewards() {
        var rewardedMoney = RobotsModule.getTierBaseMoney(tier)
        var rewardedTokens = RobotsModule.getTierBaseTokens(tier)

        if (hasUpgradeApplied(FortuneUpgrade)) {
            val fortuneLevel = getUpgradeLevel(FortuneUpgrade)
            val fortuneBaseMoney = RobotsModule.getFortuneBaseMoney()
            val fortuneMoneyMultiplier = RobotsModule.getFortuneMoneyMultiplier()

            rewardedMoney = (rewardedMoney + (fortuneBaseMoney * fortuneLevel)) * fortuneMoneyMultiplier

            val fortuneBaseTokens = RobotsModule.getFortuneBaseTokens()
            val fortuneTokensMultiplier = RobotsModule.getFortuneTokensMultiplier()

            rewardedTokens = ((rewardedTokens + fortuneBaseTokens) * (1.0 + (fortuneLevel * fortuneTokensMultiplier)).toLong())
        }

        moneyTotalEarnings += BigDecimal(rewardedMoney.toString())
        moneyEarnings.addEarnings(rewardedMoney.toLong())

        tokensTotalEarnings += BigInteger(rewardedTokens.toString())
        tokenEarnings.addEarnings(rewardedTokens)

        if (rewardTicks-- <= 0) {
            moneyEarnings.aggregate()
            tokenEarnings.aggregate()

            rewardTicks = ROBOT_TICKS
        }

        moneyOwed += rewardedMoney
        tokensOwed += rewardedTokens
    }

    private fun tickArmorStandAnimation() {
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

    private fun tickBlockAnimation() {
        val fakeBlockLocation = getFakeBlockLocation()
        val blockBreakAnimationPacket = PacketPlayOutBlockBreakAnimation()

        // there will be some overlap here, but these effects are very localized so it should be ok
        val blockBreakEntityId = (fakeBlockLocation.blockX and 0xFFF shl 20 or (fakeBlockLocation.blockZ and 0xFFF shl 8) or (fakeBlockLocation.blockY and 0xFF))
        Reflection.setDeclaredFieldValue(blockBreakAnimationPacket, "a", blockBreakEntityId)

        Reflection.setDeclaredFieldValue(blockBreakAnimationPacket, "b", BlockPosition(fakeBlockLocation.blockX, fakeBlockLocation.blockY, fakeBlockLocation.blockZ))
        Reflection.setDeclaredFieldValue(blockBreakAnimationPacket, "c", blockPhase)

        for (currentWatcher in getCurrentWatcherPlayers()) {
            if (currentWatcher.location.world != currentWatcher.world) {
                continue
            }

            (currentWatcher as CraftPlayer).handle.playerConnection.sendPacket(blockBreakAnimationPacket)
        }

        blockPhase++

        if (blockPhase > 9) {
            blockPhase = 0
        }
    }

    fun getCreatedAt(): Long {
        return createdAt
    }

    fun collectEarnings(player: Player, sendMessages: Boolean = true): Boolean {
        if (moneyOwed == 0.0 && tokensOwed == 0L) {
            if (sendMessages) {
                player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You don't have anything to collect.")
            }

            return false
        }

        lastCollect = System.currentTimeMillis()

        val moneyCollected = moneyOwed
        val tokensCollected = tokensOwed

        moneyOwed = 0.0
        tokensOwed = 0

        if (moneyCollected > 0) {
            Currency.Type.MONEY.give(player.uniqueId, moneyCollected)
        }

        if (tokensCollected > 0) {
            Currency.Type.TOKENS.give(player.uniqueId, tokensCollected)
        }

        if (sendMessages) {
            if (moneyCollected > 0 && tokensCollected > 0) {
                player.sendMessage("${RobotsModule.CHAT_PREFIX}You collected ${Formats.formatMoney(moneyCollected)} ${ChatColor.GRAY}and ${Formats.formatTokens(tokensCollected)}${ChatColor.GRAY}.")
            } else if (moneyCollected > 0) {
                player.sendMessage("${RobotsModule.CHAT_PREFIX}You collected ${Formats.formatMoney(moneyCollected)}${ChatColor.GRAY}.")
            } else if (tokensCollected > 0) {
                player.sendMessage("${RobotsModule.CHAT_PREFIX}You collected ${Formats.formatTokens(tokensCollected)}${ChatColor.GRAY}.")
            }
        }

        return true
    }

    /**
     * If this robot is fully upgraded.
     */
    fun isFullyUpgraded(): Boolean {
        for (upgrade in UpgradeManager.getRegisteredUpgrades()) {
            if (appliedUpgrades.getOrDefault(upgrade, 0) != upgrade.getMaxLevel()) {
                return false
            }
        }
        return true
    }

    /**
     * If this robot has a given [Upgrade] applied.
     */
    fun hasUpgradeApplied(upgrade: Upgrade): Boolean {
        for (appliedUpgrade in getAppliedUpgrades()) {
            if (appliedUpgrade == upgrade) {
                return true
            }
        }
        return false
    }

    fun applyUpgrade(upgrade: Upgrade) {
        if (hasUpgradeApplied(upgrade)) {
            throw IllegalStateException("Upgrade is already applied")
        }

        appliedUpgrades[upgrade] = 1
    }

    /**
     * Gets a copy of this robot's applied upgrades.
     */
    fun getAppliedUpgrades(): Set<Upgrade> {
        return appliedUpgrades.keys.toSet()
    }

    fun getUpgradeLevel(upgrade: Upgrade): Int {
        return appliedUpgrades.getOrDefault(upgrade, 0)
    }

    fun setUpgradeLevel(upgrade: Upgrade, level: Int) {
        appliedUpgrades[upgrade] = level
    }

    /**
     * If the given [cosmetic] has been applied to this robot.
     */
    fun hasCosmeticApplied(cosmetic: Cosmetic): Boolean {
        return enabledCosmetics.contains(cosmetic)
    }

    /**
     * Enables the given [cosmetic].
     */
    fun enableCosmetic(cosmetic: Cosmetic) {
        if (hasCosmeticApplied(cosmetic)) {
            throw IllegalStateException("Cosmetic is already applied")
        }

        for (appliedCosmetic in enabledCosmetics.toList()) {
            if (!cosmetic.isCompatible(appliedCosmetic)) {
                disableCosmetic(appliedCosmetic)
            }
        }

        enabledCosmetics.add(cosmetic)
        cosmetic.onEnable(this)
    }

    fun disableCosmetic(cosmetic: Cosmetic) {
        if (!hasCosmeticApplied(cosmetic)) {
            throw IllegalStateException("Cosmetic is not applied")
        }

        enabledCosmetics.remove(cosmetic)
        cosmetic.onDisable(this)
    }

    fun getFakeBlockLocation(): Location {
        val direction = getDirection()
        return location.clone().add(direction.modX.toDouble(), direction.modY.toDouble(), direction.modZ.toDouble())
    }

    fun getFakeBlock(): Block {
        return getFakeBlockLocation().block
    }

    fun setupFakeBlock(ensureSync: Boolean) {
        if (ensureSync) {
            if (!Bukkit.isPrimaryThread()) {
                Tasks.sync {
                    setupFakeBlock(false)
                }
            } else {
                setupFakeBlock(false)
            }
        } else {
            if (getFakeBlockLocation().world == null) {
                return
            }

            val fakeBlock = getFakeBlock()
            if (fakeBlock.type != Material.OBSIDIAN) {
                fakeBlock.type = Material.OBSIDIAN
                fakeBlock.state.update()
            }

            if (!fakeBlock.hasMetadata("RobotBlock")) {
                fakeBlock.setMetadata("RobotBlock", FixedMetadataValue(RobotsModule.getPluginFramework(), true))
            }
        }
    }

    fun clearFakeBlock() {
        if (getFakeBlockLocation().world == null) {
            return
        }

        Tasks.sync {
            val chunk = location.chunk
            if (!chunk.isLoaded) {
                if (!chunk.load(true)) {
                    throw IllegalStateException("Couldn't load chunk where robot is located")
                }
            }

            val fakeBlock = getFakeBlock()
            fakeBlock.type = Material.AIR
            fakeBlock.state.update()
            fakeBlock.removeMetadata("RobotBlock", RobotsModule.getPluginFramework())
        }
    }

    private fun getDirection(): BlockFace {
        var yaw = location.yaw
        if (yaw < 0.0f) {
            yaw += 360.0f
        }

        if (yaw >= 315.0f || yaw < 45.0f) {
            return BlockFace.SOUTH
        }

        if (yaw < 135.0f) {
            return BlockFace.WEST
        }

        return if (yaw < 225.0f) {
            BlockFace.NORTH
        } else {
            BlockFace.EAST
        }
    }

}