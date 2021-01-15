/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.lite.LiteEdit
import net.evilblock.cubed.lite.LiteRegion
import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.mine.variant.personal.data.BlockType
import net.evilblock.prisonaio.module.mine.variant.personal.npc.PrivateMineNPC
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.minecraft.server.v1_12_R1.IBlockData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.util.*
import kotlin.collections.HashSet

class PrivateMine(
    val gridIndex: Int,
    val owner: UUID,
    val spawnPoint: Location,
    npcLocation: Location,
    cuboid: Cuboid,
    val innerCuboid: Cuboid
) : BitmaskRegion("Private-Mine-$gridIndex", cuboid) {

    var public: Boolean = false
    var salesTax: Double = PrivateMineConfig.salesTaxRange.maximumDouble
    val whitelistedPlayers: HashSet<UUID> = hashSetOf(owner)

    var moneyGained: Long = 0L

    internal var npc: PrivateMineNPC = PrivateMineNPC(npcLocation)

    @Transient
    internal var activePlayers: HashSet<Player> = hashSetOf()

    @Transient
    var lastResetCheck: Long = System.currentTimeMillis()

    override fun initializeData() {
        super.initializeData()

        activePlayers = hashSetOf()
        salesTax = salesTax.coerceAtLeast(1.0)

        npc.initializeData()
        npc.persistent = false
        npc.updateTexture(PrivateMineHandler.getPrivateMineNPCTextureValue(), PrivateMineHandler.getPrivateMineNPCTextureSignature())
        npc.updateLines(PrivateMineHandler.getPrivateMineNPCHologramLines())

        if (!EntityManager.isEntityTracked(npc)) {
            EntityManager.trackEntity(npc)
        }
    }

    override fun getRegionName(): String {
        return "${getOwnerName()}'s Private Mine"
    }

    override fun getPriority(): Int {
        return 100
    }

    override fun getCuboid(): Cuboid {
        return cuboid!!
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
        if (!innerCuboid.contains(block)) {
            cancellable.isCancelled = true
            return
        }

        val currentMine = PrivateMineHandler.getCurrentMine(player)
        if (currentMine != this) {
            cancellable.isCancelled = true

            if (currentMine == null) {
                player.sendMessage("${ChatColor.RED}You can't break blocks there!")
            } else {
                player.sendMessage("${ChatColor.RED}You can only break blocks inside of ${currentMine.getOwnerName()}'s Private Mine!")
            }

            return
        }
    }

    fun getOwnerName(): String {
        return Cubed.instance.uuidCache.name(owner)
    }

    fun translateVariables(string: String): String {
        return string
                .replace("{owner}", getOwnerName())
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
            "your"
        } else {
            "${getOwnerName()}'s"
        }

        player.sendMessage("${ChatColor.YELLOW}You've been teleported to ${ChatColor.BLUE}$ownerOrSelf ${ChatColor.YELLOW}private mine!")
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
                player.sendMessage("")
                player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Private Mine Access Granted")
                player.sendMessage(" ${ChatColor.GRAY}You've been granted access to ${ChatColor.YELLOW}${getOwnerName()}${ChatColor.GRAY}'s")
                player.sendMessage(" ${ChatColor.GRAY}private mine.")
                player.sendMessage("")
                player.sendMessage(" ${ChatColor.YELLOW}Type /pmine to get started!")
                player.sendMessage("")
            }

            return true
        }

        return false
    }

    fun removeFromWhitelistedPlayers(uuid: UUID): Boolean {
        if (whitelistedPlayers.remove(uuid)) {
            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                player.sendMessage("")
                player.sendMessage(" ${ChatColor.RED}${ChatColor.BOLD}Private Mine Access Revoked")
                player.sendMessage(" ${ChatColor.GRAY}Your access to ${ChatColor.YELLOW}${getOwnerName()}${ChatColor.GRAY}'s private mine")
                player.sendMessage(" ${ChatColor.GRAY}has been revoked!")
                player.sendMessage("")

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
        if (PrivateMineConfig.blocks.isEmpty()) {
            return
        }

        val blockList = arrayListOf<BlockType>()
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

    private fun pickRandomBlockType(): BlockType {
        if (PrivateMineConfig.blocks.isEmpty()) {
            throw IllegalStateException("Cannot pick random block if block list is empty")
        }

        val filteredBlockTypes = PrivateMineConfig.blocks.filter { blockType -> blockType.percentage > 0.0 }
        if (filteredBlockTypes.isEmpty()) {
            throw IllegalStateException("Cannot pick random block if block list has no blocks with percentages more than 0%")
        }

        if (filteredBlockTypes.size == 1) {
            return filteredBlockTypes[0]
        }

        while (true) {
            val randomBlockType = PrivateMineConfig.blocks.random()
            if (Chance.percent(randomBlockType.percentage)) {
                return randomBlockType
            }
        }
    }

}