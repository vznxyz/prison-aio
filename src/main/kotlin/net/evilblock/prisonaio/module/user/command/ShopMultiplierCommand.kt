/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangsModule
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.mechanic.armor.impl.MinerArmorSet
import net.evilblock.prisonaio.module.mechanic.armor.impl.WardenArmorSet
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.perk.Perk
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.text.DecimalFormat

object ShopMultiplierCommand {

    private val DECIMAL_FORMAT = DecimalFormat("#.##")

    @Command(
        names = ["shopmulti", "shopmultiplier", "salesmultiplier", "salesmulti", "salesboost", "multi", "multiplier"],
        description = "Shows your active shop multiplier"
    )
    @JvmStatic
    fun execute(player: Player) {
        val user = UserHandler.getUser(player.uniqueId)
        var hasMultiplier = false

        for ((permission, multiplier) in UsersModule.getPermissionSalesMultipliers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage("${ChatColor.GRAY}You have a permission-based shop multiplier of ${ChatColor.RED}${ChatColor.BOLD}${DECIMAL_FORMAT.format(multiplier)}x${ChatColor.GRAY}.")
                hasMultiplier = true
                break
            }
        }

        if (user.perks.hasPerk(player, Perk.SALES_BOOST)) {
            hasMultiplier = true

            val activePerk = user.perks.getActivePerkGrant(Perk.SALES_BOOST)!!
            val perkMultiplier = DECIMAL_FORMAT.format(activePerk.metadata.get("multiplier")?.asDouble ?: 1.0)

            if (activePerk.isPermanent()) {
                player.sendMessage("${ChatColor.GRAY}You have a granted shop multiplier of ${ChatColor.RED}${ChatColor.BOLD}${perkMultiplier}x ${ChatColor.GRAY}for a period of ${ChatColor.RED}forever${ChatColor.GRAY}.")
            } else {
                val formattedDuration = TimeUtil.formatIntoDetailedString((activePerk.getRemainingTime() / 1000.0).toInt())
                player.sendMessage("${ChatColor.GRAY}You have a granted shop multiplier of ${ChatColor.RED}${ChatColor.BOLD}${perkMultiplier}x ${ChatColor.GRAY}for a period of ${ChatColor.RED}$formattedDuration${ChatColor.GRAY}.")
            }
        }

        val assumedGang = GangHandler.getGangByPlayer(player.uniqueId)
        if (assumedGang != null && assumedGang.hasBooster(GangBooster.BoosterType.SALES_MULTIPLIER)) {
            hasMultiplier = true

            val booster = assumedGang.getBooster(GangBooster.BoosterType.SALES_MULTIPLIER)!!
            val formattedDuration = TimeUtil.formatIntoDetailedString((booster.getRemainingTime() / 1000.0).toInt())
            player.sendMessage("${ChatColor.GRAY}You have a Gang Booster shop multiplier of ${ChatColor.RED}${ChatColor.BOLD}${DECIMAL_FORMAT.format(GangsModule.readSalesMultiplierMod())}x ${ChatColor.GRAY}for a period of ${ChatColor.RED}$formattedDuration${ChatColor.GRAY}.")
        }

        val globalMultiplier = GlobalMultiplierHandler.getActiveMultiplier()
        if (globalMultiplier != null) {
            hasMultiplier = true

            player.sendMessage("${ChatColor.GRAY}You have a global shop multiplier of ${ChatColor.RED}${ChatColor.BOLD}${DECIMAL_FORMAT.format(globalMultiplier.multiplier)}x ${ChatColor.GRAY}for the next ${ChatColor.RED}${globalMultiplier.getRemainingTime()}${ChatColor.GRAY}.")
        }

        val abilityArmor = AbilityArmorHandler.getEquippedSet(player)
        if (abilityArmor is MinerArmorSet || abilityArmor is WardenArmorSet) {
            hasMultiplier = true

            player.sendMessage("${ChatColor.GRAY}You have a miner armor multiplier of ${ChatColor.RED}${ChatColor.BOLD}${DECIMAL_FORMAT.format(4)}x${ChatColor.GRAY}.")
        }

        if (!hasMultiplier) {
            player.sendMessage("${ChatColor.RED}You don't have any active multipliers!")
        } else {
            player.sendMessage("${ChatColor.GRAY}Your stacked multiplier is ${ChatColor.RED}${ChatColor.BOLD}${user.perks.getSalesMultiplier(player)}x${ChatColor.GRAY}.")
        }
    }

}