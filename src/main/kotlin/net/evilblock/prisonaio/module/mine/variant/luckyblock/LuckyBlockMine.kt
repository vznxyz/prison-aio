/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock

import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MinesModule
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import net.evilblock.prisonaio.module.mine.menu.button.ToggleSpawnSelectionButton
import net.evilblock.prisonaio.module.mine.variant.luckyblock.entity.LuckyBlockEntity
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.metadata.FixedMetadataValue
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashSet

class LuckyBlockMine(id: String) : Mine(id) {

    /**
     * The locations where LuckyBlocks can spawn.
     */
    var spawnLocations: HashSet<Location> = hashSetOf()

    /**
     * A list of timestamps, that are inserted when a LuckyBlock is broken within the bounds of this mine.
     */
    @Transient
    var spawnQueue: LinkedList<Long> = LinkedList()

    /**
     * The currently spawned LuckyBlocks of this mine.
     */
    var spawnedEntities: MutableSet<LuckyBlockEntity> = ConcurrentHashMap.newKeySet<LuckyBlockEntity>()

    override fun initializeData() {
        super.initializeData()

        spawnQueue = LinkedList()

        for (entity in spawnedEntities) {
            entity.initializeData()
            entity.persistent = false
            entity.location.block.setMetadata("LuckyBlock", FixedMetadataValue(PrisonAIO.instance, entity))

            EntityManager.trackEntity(entity)
        }
    }

    override fun getAbstractType(): Type {
        return LuckyBlockMine::class.java
    }

    override fun getEditorButtons(): List<Button> {
        return super.getEditorButtons().toMutableList().also {
            it.add(ToggleSpawnSelectionButton(this))
        }
    }

    override fun supportsAutomaticReset(): Boolean {
        return false
    }

    override fun supportsAbilityEnchants(): Boolean {
        return false
    }

    override fun supportsAutoSell(): Boolean {
        return false
    }

    override fun supportsPassiveEnchants(): Boolean {
        return false
    }

    override fun supportsRewards(): Boolean {
        return false
    }

    override fun supportsCosmetics(): Boolean {
        return false
    }

    override fun resetRegion() {
        if (region == null) {
            throw IllegalStateException("Cannot reset mine if its region is not set")
        }

        if (spawnLocations.isEmpty()) {
            return
        }

        Tasks.sync {
            for (entity in spawnedEntities) {
                val block = entity.location.block
                block.type = Material.STONE
                block.state.update()
                block.removeMetadata("LuckyBlock", PrisonAIO.instance)

                entity.destroyForCurrentWatchers()
                EntityManager.forgetEntity(entity)
            }

            spawnedEntities.clear()

            for (i in 0 until MinesModule.getLuckyBlockMineMaxSpawns()) {
                spawnBlock()
            }
        }
    }

    fun spawnBlock() {
        if (spawnLocations.size <= spawnedEntities.size) {
            return
        }

        val unusedLocations = (spawnLocations.clone() as HashSet<Location>)
        unusedLocations.removeAll(spawnedEntities.map { it.location })

        val randomBlockType = LuckyBlockHandler.pickRandomBlockType() ?: return
        val randomLocation = unusedLocations.random()

        val entity = LuckyBlockEntity(randomBlockType, randomLocation)
        entity.initializeData()
        entity.persistent = false

        EntityManager.trackEntity(entity)

        val block = randomLocation.block
        block.type = randomBlockType.blockType.type
        block.data = randomBlockType.blockType.data.data
        block.state.update()
        block.setMetadata("LuckyBlock", FixedMetadataValue(PrisonAIO.instance, entity))

        spawnedEntities.add(entity)
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        // allow block break if the player is breaking a mine
        if (permission != null && !player.hasPermission(permission)) {
            player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}No access! ${ChatColor.RED}You lack the permission ${ChatColor.WHITE}${permission} ${ChatColor.RED}to mine here.")
            cancellable.isCancelled = true
            return
        }

        if (!block.hasMetadata("LuckyBlock")) {
            cancellable.isCancelled = true
            return
        }

        val entity = block.getMetadata("LuckyBlock").first().value() as LuckyBlockEntity
        entity.destroyForCurrentWatchers()
        EntityManager.forgetEntity(entity)

        block.removeMetadata("LuckyBlock", PrisonAIO.instance)
        block.type = Material.STONE
        block.state.update()

        spawnQueue.add(System.currentTimeMillis())
        spawnedEntities.remove(entity)

        MineBlockBreakEvent(player, block, this).call()
    }

    override fun onPlayerDeath(player: Player, event: PlayerDeathEvent) {
        event.keepInventory = false
        event.drops.clear()

        for (item in player.inventory.armorContents) {
            player.location.world.dropItemNaturally(player.location, item)
        }

        for (item in player.inventory.storageContents) {
            player.location.world.dropItemNaturally(player.location, item)
        }

        player.inventory.clear()

        event.entity.spigot().respawn()
    }

    override fun onEnterRegion(player: Player) {
        player.sendMessage("")
        player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}LuckyBlock Mine")
        player.sendMessage(" ${ChatColor.GRAY}Mine the rare block types to receive rare rewards.")
        player.sendMessage(" ${ChatColor.GRAY}Make sure to watch your back and be ready to fight!")
        player.sendMessage("")
        player.sendMessage(" ${ChatColor.RED}${ChatColor.BOLD}Warning")
        player.sendMessage(" ${ChatColor.RED}This area is dangerous! If you die, you will lose your items!")
        player.sendMessage("")
    }

}