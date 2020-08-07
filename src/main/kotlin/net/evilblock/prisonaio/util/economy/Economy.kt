/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.economy

import net.evilblock.cubed.util.hook.VaultHook
import org.bukkit.Bukkit
import java.util.*

object Economy {

    @JvmStatic
    fun getBalance(uuid: UUID): Long {
        return VaultHook.useEconomy { economy ->
            economy.getBalance(Bukkit.getOfflinePlayer(uuid)).toLong()
        }
    }

    @JvmStatic
    fun takeBalance(uuid: UUID, amount: Double) {
        return VaultHook.useEconomy { economy ->
            economy.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount)
        }
    }

}