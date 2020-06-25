/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.lite.LiteEdit
import net.evilblock.cubed.lite.LiteRegion
import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.privatemine.data.PrivateMineBlockData
import net.evilblock.prisonaio.module.privatemine.data.PrivateMineTier
import net.evilblock.prisonaio.module.privatemine.serializer.TierReferenceSerializer
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.minecraft.server.v1_12_R1.IBlockData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.util.*
import kotlin.collections.HashSet

class PrivateMine(
    val gridIndex: Int,
    val owner: UUID,
    @JsonAdapter(TierReferenceSerializer::class) val tier: PrivateMineTier,
    val spawnPoint: Location,
    private val cuboid: Cuboid,
    val innerCuboid: Cuboid
) : Region {

    /**
     * The sales tax of this mine, which is applied when visiting players sell to a shop.
     */
    var salesTax: Double = tier.salesTaxRange.maximumDouble

    /**
     * Players that have been whitelisted and can visit this mine at any time.
     */
    val whitelistedPlayers: HashSet<UUID> = hashSetOf(owner)

    /**
     * If this mine is currently public
     */
    var public: Boolean = false

    /**
     * The amount of money this mine has generated from sales tax.
     */
    @Transient var moneyGained: Long = 0L

    /**
     * The players that are currently active in this mine.
     */
    @Transient internal var activePlayers: HashSet<Player> = hashSetOf()

    @Transient
    var lastResetCheck: Long = System.currentTimeMillis()

    override fun getRegionName(): String {
        return "${getOwnerName()}'s Private Mine (Tier ${tier})"
    }

    override fun getCuboid(): Cuboid {
        return cuboid
    }

    override fun is3D(): Boolean {
        return false
    }

    override fun getBreakableCuboid(): Cuboid? {
        return innerCuboid
    }

    override fun resetBreakableCuboid() {
        resetRegion()
    }

    override fun supportsAbilityEnchants(): Boolean {
        return true
    }

    override fun supportsPassiveEnchants(): Boolean {
        return true
    }

    override fun supportsRewards(): Boolean {
        return true
    }

    override fun supportsAutoSell(): Boolean {
        return true
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        cancellable.isCancelled = !innerCuboid.contains(block)
    }

    fun getOwnerName(): String {
        return Cubed.instance.uuidCache.name(owner)
    }

    fun translateVariables(string: String): String {
        return string
                .replace("{owner}", getOwnerName())
                .replace("{tier}", tier.number.toString())
                .replace("{moneyGained}", NumberUtils.format(moneyGained))
                .replace("{salesTax}", salesTax.toString())
    }

    fun getActivePlayers(): Set<Player> {
        return HashSet(activePlayers)
    }

    fun isActivePlayer(player: Player): Boolean {
        return activePlayers.contains(player)
    }

    fun addToActivePlayers(player: Player) {
        this.activePlayers.add(player)

        player.teleport(spawnPoint)

        val ownerOrSelf = if (owner == player.uniqueId) {
            "Your"
        } else {
            "${getOwnerName()}'s"
        }

        PrivateMinesModule.getNotificationLines("teleported").forEach {
            player.sendMessage(translateVariables(it).replace("{ownerOrSelf}", ownerOrSelf))
        }
    }

    fun removeFromActivePlayers(player: Player) {
        this.activePlayers.remove(player)
    }

    fun getWhitelistedPlayers(): Set<UUID> {
        return HashSet(whitelistedPlayers)
    }

    fun isWhitelistedPlayer(uuid: UUID): Boolean {
        return whitelistedPlayers.contains(uuid)
    }

    fun addToWhitelistedPlayers(uuid: UUID): Boolean {
        if (whitelistedPlayers.add(uuid)) {
            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                // send player access granted notification
                PrivateMinesModule.getNotificationLines("access-granted").forEach { player.sendMessage(translateVariables(it)) }
            }

            return true
        }

        return false
    }

    fun removeFromWhitelistedPlayers(uuid: UUID): Boolean {
        if (whitelistedPlayers.remove(uuid)) {
            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                // send player access revoked notification
                PrivateMinesModule.getNotificationLines("access-revoked").forEach { player.sendMessage(translateVariables(it)) }

                // teleport player out of the mine
                if (isActivePlayer(player)) {
                    player.teleport(Bukkit.getWorlds()[0].spawnLocation)
                }
            }

            return true
        }

        return false
    }

    /**
     * Resets the mine area with new blocks.
     */
    fun resetRegion() {
        if (tier.blocks.isEmpty()) {
            return
        }

        val blockList = arrayListOf<PrivateMineBlockData>()
        for (i in 0 until (innerCuboid.sizeX * innerCuboid.sizeY * innerCuboid.sizeZ)) {
            blockList.add(pickRandomBlockType())
        }

        var index = 0

        val liteRegion = LiteRegion(innerCuboid)
        LiteEdit.fill(liteRegion, object : LiteEdit.FillHandler {
            override fun getBlock(x: Int, y: Int, z: Int): IBlockData? {
                if (RewardsModule.isEnabled()) {
                    if (MineCrateHandler.isAttached(Location(innerCuboid.world, x.toDouble(), y.toDouble(), z.toDouble()))) {
                        return null
                    }
                }

                val blockType = blockList[index++]
                return getData(blockType.material, blockType.data.toInt())
            }
        }, LiteEdit.VoidProgressCallBack)
    }

    private fun pickRandomBlockType(): PrivateMineBlockData {
        if (tier.blocks.isEmpty()) {
            throw IllegalStateException("Cannot pick random block if block list is empty")
        }

        val filteredBlockTypes = tier.blocks.filter { blockType -> blockType.percentage > 0.0 }
        if (filteredBlockTypes.isEmpty()) {
            throw IllegalStateException("Cannot pick random block if block list has no blocks with percentages more than 0%")
        }

        if (filteredBlockTypes.size == 1) {
            return filteredBlockTypes[0]
        }

        while (true) {
            val randomBlockType = tier.blocks.random()
            if (Chance.percent(randomBlockType.percentage)) {
                return randomBlockType
            }
        }
    }

}