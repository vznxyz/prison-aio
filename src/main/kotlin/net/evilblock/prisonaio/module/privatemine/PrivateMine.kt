package net.evilblock.prisonaio.module.privatemine

import com.boydti.fawe.bukkit.wrapper.AsyncWorld
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.module.privatemine.data.PrivateMineTier
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.util.*
import kotlin.collections.HashSet
import kotlin.random.Random

class PrivateMine(
    val gridIndex: Int,
    val owner: UUID,
    val tier: PrivateMineTier,
    val spawnPoint: Location,
    val cuboid: Cuboid,
    val innerCuboid: Cuboid
) : Region {

    /**
     * The sales tax of this mine, which is applied when visiting players sell to a shop.
     */
    var salesTax: Double = tier.salesTaxRange.minimumDouble

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
     * The last time in milliseconds that this mine was reset.
     */
    @Transient var lastReset: Long = -1

    /**
     * The players that are currently active in this mine.
     */
    @Transient internal var activePlayers: HashSet<Player> = hashSetOf()

    override fun getRegionName(): String {
        return "${getOwnerName()}'s Private Mine (Tier ${tier})"
    }

    override fun getBreakableRegion(): Cuboid? {
        return innerCuboid
    }

    override fun resetBreakableRegion() {
        resetMineArea()
    }

    override fun supportsEnchants(): Boolean {
        return true
    }

    override fun supportsRewards(): Boolean {
        return true
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        if (innerCuboid.contains(block)) {
            cancellable.isCancelled = false
        }
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

        // reset mine if it's been a while
        if (System.currentTimeMillis() - lastReset > tier.resetInterval) {
            resetMineArea()
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
    fun resetMineArea() {
        // prevent never-ending while loop
        if (tier.blocks.isEmpty()) {
            return
        }

        lastReset = System.currentTimeMillis()

        Tasks.sync {
            val blocks = tier.blocks.toMutableList()
            val asyncWorld = AsyncWorld.wrap(PrivateMineHandler.getGridWorld())

            innerCuboid.blocks.forEach { block ->
                var changed = false
                while (!changed) {
                    for (possibleBlockData in blocks.shuffled()) {
                        if (Random.nextInt(0, 100) >= 100 - possibleBlockData.percentage) {
                            asyncWorld.setBlock(block.x, block.y, block.z, possibleBlockData.material.id, possibleBlockData.data.toInt())
                            changed = true
                            break
                        }
                    }
                }
            }

            asyncWorld.commit()
        }
    }

}