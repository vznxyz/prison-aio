package net.evilblock.prisonaio.module.robot.impl

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import net.evilblock.prisonaio.module.robot.menu.RobotMenu
import net.evilblock.prisonaio.module.robot.Robot
import net.evilblock.prisonaio.module.robot.RobotHandler
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.cosmetic.Cosmetic
import net.evilblock.prisonaio.module.robot.cosmetic.impl.SkinCosmetic
import net.evilblock.prisonaio.module.robot.impl.modifier.RobotModifier
import net.evilblock.prisonaio.module.robot.impl.modifier.RobotModifierType
import net.evilblock.prisonaio.module.robot.impl.modifier.RobotModifierUtils
import net.evilblock.prisonaio.util.statistic.EarningsHistoryV2
import net.evilblock.prisonaio.module.robot.impl.upgrade.Upgrade
import net.evilblock.prisonaio.module.robot.impl.upgrade.UpgradeManager
import net.evilblock.prisonaio.module.robot.impl.upgrade.impl.EfficiencyUpgrade
import net.evilblock.prisonaio.module.robot.impl.upgrade.impl.FortuneUpgrade
import net.evilblock.prisonaio.module.robot.serialize.AppliedCosmeticsSerializer
import net.evilblock.prisonaio.module.robot.serialize.AppliedUpgradesSerializer
import net.evilblock.prisonaio.module.robot.thread.Tickable
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class MinerRobot(owner: UUID, location: Location) : Robot(owner = owner, location = location), Tickable {

    companion object {
        private const val ROBOT_TICKS = 600
    }

    var tier: Int = 0

    internal var rewardTicks: Int = ROBOT_TICKS

    internal var moneyEarnings: EarningsHistoryV2 = EarningsHistoryV2()
    internal var tokenEarnings: EarningsHistoryV2 = EarningsHistoryV2()

    internal var moneyOwed: BigDecimal = BigDecimal(0.0)
    internal var tokensOwed: BigDecimal = BigDecimal(0.0)

    internal var lastCollect: Long = System.currentTimeMillis()

    @JsonAdapter(AppliedUpgradesSerializer::class)
    internal var appliedUpgrades: MutableMap<Upgrade, Int> = hashMapOf()

    @JsonAdapter(AppliedCosmeticsSerializer::class)
    internal var enabledCosmetics: MutableList<Cosmetic> = arrayListOf()

    private var lastTick: Long = System.currentTimeMillis()
    var uptime: Long = 0L

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

    var modifierStorage: Array<ItemStack?> = arrayOfNulls(3)
    var modifiers: ConcurrentHashMap<RobotModifierType, RobotModifier> = ConcurrentHashMap()

    override fun initializeData() {
        super.initializeData()

        persistent = false
        lastTick = System.currentTimeMillis()

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
            RobotMenu(this).openMenu(player)
        } catch (e: Exception) {
            player.sendMessage("${ChatColor.RED}Technical difficulties! (Server Error)")
            e.printStackTrace()
        }
    }

    override fun sendDestroyPackets(player: Player) {
        super.sendDestroyPackets(player)
    }

    override fun getHologramLines(): List<String> {
        val lines = arrayListOf<String>()
        val ownerName = Cubed.instance.uuidCache.name(owner)

        lines.add(getTierName())

        if (tier == 0) {
            lines.add("${ChatColor.GRAY}No Tier")
        } else {
            lines.add("${ChatColor.GRAY}Tier $tier")
        }

        lines.add("${ChatColor.GRAY}Owned by $ownerName")

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

    fun getTicksPerSecond(): Int {
        return (1000.0 / getTickInterval()).toInt()
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

        tickModifiers()

        val owner = Bukkit.getPlayer(owner)
        if (owner != null && owner.isOnline) {
            val timePassed = System.currentTimeMillis() - lastTick
            if (timePassed < 3_000L) {
                uptime += timePassed
            }

            tickRewards()

            if (RobotsModule.isAnimationsEnabled()) {
                tickArmorStandAnimation()
            }
        }
    }

    fun getMoneyPerTick(): Double {
        return if (hasUpgradeApplied(FortuneUpgrade)) {
            ((RobotsModule.getTierBaseMoney(tier) + RobotsModule.getFortuneBaseMoney()) * (1.0 + (getUpgradeLevel(FortuneUpgrade) * RobotsModule.getFortuneMoneyMultiplier())))
        } else {
            RobotsModule.getTierBaseMoney(tier)
        }
    }

    fun getTokensPerTick(): Double {
        return if (hasUpgradeApplied(FortuneUpgrade)) {
            ((RobotsModule.getTierBaseTokens(tier) + RobotsModule.getFortuneBaseTokens()) * (1.0 + (getUpgradeLevel(FortuneUpgrade) * RobotsModule.getFortuneTokensMultiplier())))
        } else {
            RobotsModule.getTierBaseTokens(tier)
        }
    }

    private fun tickRewards() {
        val money = getMoneyPerTick()
        val tokens = getTokensPerTick()

        moneyEarnings.addEarnings(money)
        tokenEarnings.addEarnings(tokens)

        if (rewardTicks-- <= 0) {
            moneyEarnings.aggregate()
            tokenEarnings.aggregate()

            rewardTicks = ROBOT_TICKS
        }

        moneyOwed += BigDecimal(money)
        tokensOwed += BigDecimal(tokens)

        if (hasActiveModifier(RobotModifierType.AUTO_COLLECT)) {
            val modifier = getActiveModifier(RobotModifierType.AUTO_COLLECT) ?: return

            val user = UserHandler.getOrLoadAndCacheUser(owner)
            if (user.cacheExpiry != null && modifier.duration != null) {
                if (modifier.duration.isPermanent()) {
                    user.cacheExpiry = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L)
                } else {
                    user.cacheExpiry = System.currentTimeMillis() + modifier.getRemainingTime()
                }
            }

            val moneyCollected = moneyOwed
            val tokensCollected = tokensOwed

            moneyOwed = BigDecimal(0.0)
            tokensOwed = BigDecimal(0.0)

            if (moneyCollected > BigDecimal.ZERO) {
                Currency.Type.MONEY.give(owner, moneyCollected)
            }

            if (tokensCollected > BigDecimal.ZERO) {
                Currency.Type.TOKENS.give(owner, tokensCollected)
            }
        }
    }

    fun getActiveModifiers(): Array<RobotModifier?> {
        return modifiers.values.toTypedArray()
    }

    fun hasActiveModifier(type: RobotModifierType): Boolean {
        return modifiers.containsKey(type)
    }

    fun getActiveModifier(type: RobotModifierType): RobotModifier? {
        return modifiers[type]
    }

    fun getMaxModifiers(): Int {
        return 1
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

    fun onApplyModifier(modifier: RobotModifier) {

    }

    fun onRemoveModifier(modifier: RobotModifier) {

    }

    private fun tickModifiers() {
        if (getMaxModifiers() > 0) {
            val expired = arrayListOf<RobotModifier>()
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
                        val modifier = RobotModifierUtils.extractModifierFromItemStack(item)
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

    fun collectEarnings(player: Player, sendMessages: Boolean = true): Boolean {
        if (System.currentTimeMillis() < lastCollect + 3_000L) {
            val cooldown = TimeUtil.formatIntoAbbreviatedString((((lastCollect + 3_000L) - System.currentTimeMillis()) / 1000.0).toInt())
            player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You must wait another $cooldown before collecting again!")
            return false
        }

        if (moneyOwed == BigDecimal.ZERO && tokensOwed == BigDecimal.ZERO) {
            if (sendMessages) {
                player.sendMessage("${RobotsModule.CHAT_PREFIX}${ChatColor.RED}You don't have anything to collect.")
            }

            return false
        }

        lastCollect = System.currentTimeMillis()

        val moneyCollected = moneyOwed
        val tokensCollected = tokensOwed.toBigInteger()

        moneyOwed = BigDecimal(0.0)
        tokensOwed = BigDecimal(0.0)

        val collectingMoney = moneyCollected > BigDecimal.ZERO
        if (collectingMoney) {
            Currency.Type.MONEY.give(player.uniqueId, moneyCollected)
        }

        val collectingTokens = tokensCollected > BigInteger.ZERO
        if (collectingTokens) {
            Currency.Type.TOKENS.give(player.uniqueId, tokensCollected)
        }

        if (sendMessages) {
            if (collectingMoney && collectingTokens) {
                player.sendMessage("${RobotsModule.CHAT_PREFIX}You collected ${Formats.formatMoney(moneyCollected)} ${ChatColor.GRAY}and ${Formats.formatTokens(tokensCollected)}${ChatColor.GRAY}.")
            } else if (collectingMoney) {
                player.sendMessage("${RobotsModule.CHAT_PREFIX}You collected ${Formats.formatMoney(moneyCollected)}${ChatColor.GRAY}.")
            } else if (collectingTokens) {
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