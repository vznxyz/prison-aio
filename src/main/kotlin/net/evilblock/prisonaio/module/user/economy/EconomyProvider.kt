/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.economy

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import java.util.*

class EconomyProvider : AbstractEconomy() {

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getName(): String {
        return "PrisonAIO"
    }

    override fun currencyNamePlural(): String {
        return "Dollars"
    }

    override fun currencyNameSingular(): String {
        return "Dollar"
    }

    override fun fractionalDigits(): Int {
        return 2
    }

    override fun format(amount: Double): String {
        return Formats.formatMoney(amount)
    }

    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun getBanks(): MutableList<String> {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun createBank(name: String?, player: OfflinePlayer?): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun createBank(name: String?, player: String?): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun deleteBank(name: String?): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun isBankOwner(name: String, player: OfflinePlayer): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun isBankOwner(name: String?, playerName: String?): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun bankBalance(name: String?): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun bankHas(name: String?, amount: Double): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun bankDeposit(name: String?, amount: Double): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun bankWithdraw(name: String?, amount: Double): EconomyResponse {
        throw UnsupportedOperationException("PrisonAIO does not support banks")
    }

    override fun getBalance(playerName: String): Double {
        val uuid = Cubed.instance.uuidCache.uuid(playerName) ?: throw IllegalStateException("UUID doesn't belong to an existing player")
        ensureUserLoaded(uuid)

        return UserHandler.getUser(uuid).getMoneyBalance().toDouble()
    }

    override fun getBalance(playerName: String, world: String): Double {
        return getBalance(playerName)
    }

    override fun getBalance(player: OfflinePlayer?): Double {
        if (player == null) {
            throw IllegalStateException("UUID doesn't belong to an existing player")
        }

        ensureUserLoaded(player.uniqueId)

        return UserHandler.getUser(player.uniqueId).getMoneyBalance().toDouble()
    }

    override fun getBalance(player: OfflinePlayer?, world: String?): Double {
        return getBalance(player)
    }

    override fun has(playerName: String?, amount: Double): Boolean {
        if (playerName == null) {
            return false
        }

        val uuid = Cubed.instance.uuidCache.uuid(playerName) ?: throw IllegalStateException("UUID doesn't belong to an existing player")
        ensureUserLoaded(uuid)

        return UserHandler.getUser(uuid).hasMoneyBalance(amount)
    }

    override fun has(playerName: String, worldName: String, amount: Double): Boolean {
        return has(playerName, amount)
    }

    override fun has(player: OfflinePlayer?, amount: Double): Boolean {
        if (player == null) {
            throw IllegalStateException("UUID doesn't belong to an existing player")
        }

        ensureUserLoaded(player.uniqueId)

        return UserHandler.getUser(player.uniqueId).hasMoneyBalance(amount)
    }

    override fun has(player: OfflinePlayer?, worldName: String?, amount: Double): Boolean {
        return has(player, amount)
    }

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val uuid = Cubed.instance.uuidCache.uuid(playerName) ?: throw IllegalStateException("UUID doesn't belong to an existing player")
        ensureUserLoaded(uuid)

        val user = UserHandler.getUser(uuid)
        user.addMoneyBalance(amount)

        return EconomyResponse(amount, user.getMoneyBalance().toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun depositPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse {
        return depositPlayer(playerName, amount)
    }

    override fun depositPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        if (player == null) {
            throw IllegalStateException("UUID doesn't belong to an existing player")
        }

        ensureUserLoaded(player.uniqueId)

        val user = UserHandler.getUser(player.uniqueId)
        user.addMoneyBalance(amount)

        return EconomyResponse(amount, user.getMoneyBalance().toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun depositPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse {
        return depositPlayer(player, amount)
    }

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val uuid = Cubed.instance.uuidCache.uuid(playerName) ?: throw IllegalStateException("UUID doesn't belong to an existing player")
        ensureUserLoaded(uuid)

        val user = UserHandler.getUser(uuid)
        user.subtractMoneyBalance(amount)

        return EconomyResponse(amount, user.getMoneyBalance().toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse {
        return withdrawPlayer(playerName, amount)
    }

    override fun withdrawPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        if (player == null) {
            throw IllegalStateException("UUID doesn't belong to an existing player")
        }

        ensureUserLoaded(player.uniqueId)

        val user = UserHandler.getUser(player.uniqueId)
        user.subtractMoneyBalance(amount)

        return EconomyResponse(amount, user.getMoneyBalance().toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun withdrawPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse {
        return withdrawPlayer(player, amount)
    }

    override fun hasAccount(playerName: String?): Boolean {
        return true
    }

    override fun hasAccount(playerName: String?, worldName: String): Boolean {
        return true
    }

    override fun createPlayerAccount(playerName: String): Boolean {
        return true
    }

    override fun createPlayerAccount(playerName: String, worldName: String): Boolean {
        return true
    }

    private fun ensureUserLoaded(uuid: UUID) {
        if (!UserHandler.isUserLoaded(uuid)) {
            UserHandler.getOrLoadAndCacheUser(uuid, lookup = false, throws = true)
        }
    }

}