package net.evilblock.prisonaio.util.economy

import net.evilblock.cubed.Cubed
import net.evilblock.prisonaio.module.user.UserHandler
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse
import java.lang.UnsupportedOperationException
import java.text.DecimalFormat

class EconomyProvider : AbstractEconomy() {

    private val decimalFormat = DecimalFormat("#.##")

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getName(): String {
        return "Cubed Economy"
    }

    override fun currencyNameSingular(): String {
        return "Dollar"
    }

    override fun currencyNamePlural(): String {
        return "Dollars"
    }

    override fun format(amount: Double): String {
        return decimalFormat.format(amount)
    }

    override fun fractionalDigits(): Int {
        return 2
    }

    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun getBanks(): MutableList<String> {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun createBank(name: String, player: String): EconomyResponse {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun deleteBank(name: String): EconomyResponse {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun isBankOwner(name: String, playerName: String): EconomyResponse {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun isBankMember(name: String, playerName: String): EconomyResponse {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun bankBalance(name: String): EconomyResponse {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun bankHas(name: String, amount: Double): EconomyResponse {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun bankDeposit(name: String, amount: Double): EconomyResponse {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun bankWithdraw(name: String, amount: Double): EconomyResponse {
        throw UnsupportedOperationException("Banks are not supported")
    }

    override fun hasAccount(playerName: String): Boolean {
        return true
    }

    override fun hasAccount(playerName: String, worldName: String): Boolean {
        return true
    }

    override fun createPlayerAccount(playerName: String): Boolean {
        return true
    }

    override fun createPlayerAccount(playerName: String, worldName: String): Boolean {
        return true
    }

    override fun getBalance(playerName: String): Double {
        val uuid = Cubed.instance.uuidCache.uuid(playerName)
        return if (uuid != null) {
            UserHandler.getOrLoadAndCacheUser(uuid, true).getMoneyBalance().toDouble()
        } else {
            -1.0
        }
    }

    override fun getBalance(playerName: String, world: String): Double {
        return getBalance(playerName)
    }

    override fun has(playerName: String, amount: Double): Boolean {
        return getBalance(playerName) >= amount
    }

    override fun has(playerName: String, worldName: String, amount: Double): Boolean {
        return has(playerName, amount)
    }

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val uuid = Cubed.instance.uuidCache.uuid(playerName)
        return if (uuid != null) {
            val user = UserHandler.getOrLoadAndCacheUser(uuid, true)

            try {
                user.addMoneyBalance(amount)

                EconomyResponse(amount, user.getMoneyBalance().toDouble(), EconomyResponse.ResponseType.SUCCESS, "")
            } catch (e: Exception) {
                EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, e.message)
            }
        } else {
            EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Couldn't find UUID of player")
        }
    }

    override fun depositPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse {
        return depositPlayer(playerName, amount)
    }

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val uuid = Cubed.instance.uuidCache.uuid(playerName)
        return if (uuid != null) {
            val user = UserHandler.getOrLoadAndCacheUser(uuid, true)

            try {
                user.subtractMoneyBalance(amount)

                EconomyResponse(amount, user.getMoneyBalance().toDouble(), EconomyResponse.ResponseType.SUCCESS, "")
            } catch (e: Exception) {
                EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, e.message)
            }
        } else {
            EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Couldn't find UUID of player")
        }
    }

    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse {
        return withdrawPlayer(playerName, amount)
    }

}