/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.perk

import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangsModule
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.UsersModule
import org.bukkit.entity.Player
import java.util.*

class UserPerks(@Transient internal var user: User) {

    /**
     * The user's granted perks.
     */
    private var grantedPerks: MutableList<PerkGrant> = arrayListOf()

    /**
     * The user's perk toggle states.
     */
    private var perkStates: MutableMap<Perk, Boolean> = EnumMap(Perk::class.java)

    /**
     * Get the user's granted perks.
     */
    fun getPerkGrants(): List<PerkGrant> {
        return grantedPerks.toList()
    }

    /**
     * Gets the active grant for the given [perk].
     */
    fun getActivePerkGrant(perk: Perk): PerkGrant? {
        return grantedPerks.firstOrNull() { it.perk == perk && !it.isPaused() && !it.isExpired() }
    }

    /**
     * Creates a new [PerkGrant] with the given data and inserts it into the user's [grantedPerks].
     */
    fun trackPerkGrant(grant: PerkGrant): PerkGrant {
        grantedPerks.add(grant)
        user.requiresSave = true
        return grant
    }

    /**
     * Removes the given [grant] from the user's [grantedPerks].
     */
    fun forgetPerkGrant(grant: PerkGrant) {
        grantedPerks.remove(grant)
        user.requiresSave = true
    }

    /**
     * If the player has access to the given [perk].
     */
    fun hasPerk(player: Player, perk: Perk): Boolean {
        if (perk == Perk.AUTO_SMELT) {
            if (UsersModule.isAutoSmeltPerkEnabledByDefault()) {
                return true
            }
        }

        if (perk.permission != null) {
            if (player.hasPermission(perk.permission)) {
                return true
            }
        }

        return grantedPerks.any { it.perk == perk && !it.isExpired() }
    }

    /**
     * Toggles the given [perk].
     */
    fun togglePerk(perk: Perk) {
        val newState = !perkStates.getOrDefault(perk, false)
        perkStates[perk] = newState

        if (newState) {
            grantedPerks.firstOrNull { it.perk == perk && !it.isExpired() }?.start()
        } else {
            getActivePerkGrant(perk)?.pause()
        }

        user.requiresSave = true
    }

    /**
     * If the player has the given [perk] enabled.
     */
    fun isPerkEnabled(perk: Perk): Boolean {
        return perkStates.getOrDefault(perk, false)
    }

    /**
     * Get the user's sales-boost perk multiplier.
     */
    fun getSalesMultiplier(player: Player): Double {
        var stackedMultiplier = 0.0

        for ((permission, multiplier) in UsersModule.permissionSalesMultipliers) {
            if (player.hasPermission(permission)) {
                stackedMultiplier += multiplier
                break
            }
        }

        stackedMultiplier += getActivePerkGrant(Perk.SALES_BOOST)?.metadata?.get("multiplier")?.asDouble ?: 0.0

        val globalMultiplier = GlobalMultiplierHandler.getActiveMultiplier()
        if (globalMultiplier != null) {
            stackedMultiplier += globalMultiplier.multiplier
        }

        val equippedAbilityArmor = AbilityArmorHandler.getEquippedSet(player)
        if (equippedAbilityArmor != null) {
            stackedMultiplier += 4.0
        }

        val assumedGang = GangHandler.getGangByPlayer(player.uniqueId)
        if (assumedGang != null) {
            if (assumedGang.hasBooster(GangBooster.BoosterType.SALES_MULTIPLIER)) {
                stackedMultiplier += GangsModule.readSalesMultiplierMod()
            }
        }

        return stackedMultiplier.coerceAtLeast(1.0)
    }

    fun isAutoSellEnabled(player: Player): Boolean {
        return UsersModule.isAutoSellPerkEnabledByDefault() || (isPerkEnabled(Perk.AUTO_SELL) && hasPerk(player, Perk.AUTO_SELL))
    }

}