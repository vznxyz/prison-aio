package net.evilblock.prisonaio.module.rank

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Rank(val id: String) {

    var displayName: String = id
    internal var price: Long = 1L
    var sortOrder: Int = 1

    fun executeCommands(player: Player) {
        RanksModule.readCommands(id).forEach { command ->
            val processedCommand = command.trim()
                .replace("{playerName}", player.name)
                .replace("{playerDisplayName}", player.displayName)
                .replace("{rankId}", id)
                .replace("{rankDisplayName}", displayName)
                .replace("{rankOrder}", sortOrder.toString())

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand)
        }
    }

    fun getCommands(): Set<String> {
        return RanksModule.readCommands(id).toSet()
    }

    fun getPermissions(): Set<String> {
        val permissions = RanksModule.readPermissions(id).toMutableSet()

        for (permission in RanksModule.getDefaultPermissions()) {
            permissions.add(permission.replace("{rankId}", id))
        }

        return permissions.toSet()
    }

    fun getCompoundedPermissions(): Set<String> {
        val set = hashSetOf<String>()
        set.addAll(getPermissions())

        for (rank in RankHandler.getSortedRanks().filter { it.sortOrder < this.sortOrder }) {
            set.addAll(rank.getPermissions())
        }

        return set
    }

    fun getPrice(prestige: Int): Double {
        val priceMultiplier = if (prestige == 0) {
            1.0
        } else {
            prestige * RankHandler.priceScaleMultiplier
        }

        return price * priceMultiplier
    }

}