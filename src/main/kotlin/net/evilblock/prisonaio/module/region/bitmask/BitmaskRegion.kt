/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.bitmask

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.EventUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffectType
import org.bukkit.projectiles.ProjectileSource
import java.lang.reflect.Type

open class BitmaskRegion(id: String, cuboid: Cuboid? = null) : Region(id, cuboid) {

    private var bitmask: Int = 0

    override fun getRegionName(): String {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Safe-Zone"
        }

        return "${ChatColor.RED}${ChatColor.BOLD}Dangerous"
    }

    override fun getPriority(): Int {
        return when {
            hasBitmask(RegionBitmask.SAFE_ZONE) -> {
                999
            }
            hasBitmask(RegionBitmask.DANGER_ZONE) -> {
                50
            }
            else -> {
                0
            }
        }
    }

    override fun getAbstractType(): Type {
        return BitmaskRegion::class.java
    }

    fun getRawBitmask(): Int {
        return bitmask
    }

    fun setBitmask(bitmaskValue: Int) {
        bitmask = bitmaskValue
    }

    fun addBitmask(bitmaskType: RegionBitmask) {
        if (!hasBitmask(bitmaskType)) {
            setBitmask(getRawBitmask() + bitmaskType.bitmaskValue)
        }
    }

    fun removeBitmask(bitmaskType: RegionBitmask) {
        if (hasBitmask(bitmaskType)) {
            setBitmask(getRawBitmask() - bitmaskType.bitmaskValue)
        }
    }

    fun hasBitmask(bitmaskType: RegionBitmask): Boolean {
        return (bitmask and bitmaskType.bitmaskValue) == bitmaskType.bitmaskValue
    }

    override fun supportsCosmetics(): Boolean {
        return !hasBitmask(RegionBitmask.DANGER_ZONE)
    }

    override fun onRightClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            if (Constants.CONTAINER_TYPES.contains(clickedBlock.type)) {
                cancellable.isCancelled = true
            }

            if (Constants.INTERACTIVE_TYPES.contains(clickedBlock.type)) {
                cancellable.isCancelled = true
            }
        }
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        if (!hasBitmask(RegionBitmask.ALLOW_BUILD)) {
            cancellable.isCancelled = true
        }
    }

    override fun onBlockPlace(player: Player, block: Block, cancellable: Cancellable) {
        if (!hasBitmask(RegionBitmask.ALLOW_BUILD)) {
            cancellable.isCancelled = true
        }
    }

    override fun onEntityDamage(entity: Entity, cause: EntityDamageEvent.DamageCause, cancellable: Cancellable) {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            cancellable.isCancelled = true
            return
        }
    }

    override fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        if (victim is ItemFrame || victim is Painting) {
            if (attacker is Player) {
                if (!RegionBypass.hasBypass(attacker)) {
                    cancellable.isCancelled = true
                    return
                }
            }
        }

        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            cancellable.isCancelled = true
            victim.fireTicks = 0
            return
        }

        if (hasBitmask(RegionBitmask.DANGER_ZONE)) {
            if (victim !is Player) {
                return
            }

            val attackingPlayer = EventUtils.getAttacker(attacker) ?: return

            // allow self damage
            if (attackingPlayer == victim) {
                cancellable.isCancelled = false
                return
            }

            if (getCuboid()?.contains(victim.location) == false) {
                cancellable.isCancelled = true
                return
            }

            // respect NO_FF bitmask
            if (hasBitmask(RegionBitmask.NO_FF)) {
                val victimGang = GangHandler.getGangByPlayer(victim)
                val attackerGang = GangHandler.getGangByPlayer(attackingPlayer)

                if (victimGang != null && attackerGang != null && victimGang == attackerGang) {
                    attackingPlayer.sendMessage("${ChatColor.YELLOW}You can't hurt ${ChatColor.DARK_GREEN}${victim.name}${ChatColor.YELLOW}!")
                    cancellable.isCancelled = true
                    return
                }
            }

            cancellable.isCancelled = false

            CombatTimerHandler.resetTimer(attackingPlayer)
            CombatTimerHandler.resetTimer(victim)
        }
    }

    override fun onProjectileLaunch(projectile: Projectile, source: ProjectileSource, cancellable: Cancellable) {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            if (source is Player) {
                cancellable.isCancelled = true
                source.sendMessage("${ChatColor.RED}You can't launch projectiles in a safe-zone!")
            }
        }
    }

    override fun onFoodLevelChange(cancellable: Cancellable) {
        cancellable.isCancelled = hasBitmask(RegionBitmask.SAFE_ZONE)
    }

    override fun onPlayerDeath(player: Player, event: PlayerDeathEvent) {
        val armorContents = player.inventory.armorContents
        val storageContents = player.inventory.storageContents

        if (hasBitmask(RegionBitmask.DANGER_ZONE)) {
            event.keepInventory = false
            event.drops.clear()

            // only drop items if the player is not in CREATIVE mode
            if (player.gameMode != GameMode.CREATIVE) {
                for (item in armorContents.filterNotNull()) {
                    player.location.world.dropItemNaturally(player.location, item)
                }

                for (item in storageContents.filterNotNull()) {
                    player.location.world.dropItemNaturally(player.location, item)
                }
            }

            player.inventory.clear()
            player.spigot().respawn()
        } else {
            player.spigot().respawn()

            Tasks.delayed(1L) {
                player.fireTicks = 0

                player.inventory.clear()
                player.inventory.armorContents = armorContents
                player.inventory.storageContents = storageContents

                player.updateInventory()
            }
        }
    }

    override fun onLeaveRegion(player: Player) {
        if (hasBitmask(RegionBitmask.DANGER_ZONE)) {
            if (CombatTimerHandler.isOnTimer(player)) {
                CombatTimerHandler.forgetTimer(CombatTimerHandler.getTimer(player)!!)
            }
        }

        if (hasBitmask(RegionBitmask.SPEED)) {
            player.removePotionEffect(PotionEffectType.SPEED)
        }
    }

    override fun onEnterRegion(player: Player) {
        if (hasBitmask(RegionBitmask.DENY_FLY)) {
            if (player.gameMode != GameMode.CREATIVE) {
                if (player.isFlying) {
                    player.isFlying = false
                }

                if (player.allowFlight) {
                    player.allowFlight = false
                }
            }
        }

        if (hasBitmask(RegionBitmask.DENY_SPEED)) {
            if (player.gameMode != GameMode.CREATIVE) {
                if (player.walkSpeed != 0.2F) {
                    player.walkSpeed = 0.2F
                }
            }

            for (enchant in EnchantHandler.getRegisteredEnchants()) {
                if (!EnchantHandler.config.isEnchantEnabled(enchant)) {
                    continue
                }

                val metadataKey = "JE-" + enchant.id

                if (player.hasMetadata(metadataKey)) {
                    enchant.onUnhold(player)
                    player.removeMetadata(metadataKey, PrisonAIO.instance)
                }
            }
        }
    }

    /*
            if (victim is Player) {
            cancellable.isCancelled = true
        }

        if (victim is ItemFrame) {
            if (attacker is Player) {
                if (RegionListeners.bypassCheck(attacker, cancellable)) {
                    return
                }

                cancellable.isCancelled = true
            }
        }
     */

}