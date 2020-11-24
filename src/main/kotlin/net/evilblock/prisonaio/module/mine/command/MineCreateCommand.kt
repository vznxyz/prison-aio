/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockMine
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyMine
import net.evilblock.prisonaio.module.mine.variant.normal.NormalMine
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MineCreateCommand {

    @Command(
        names = ["mine create"],
        description = "Create a new mine",
        permission = Permissions.MINES_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name= "type", defaultValue = "normal") type: String, @Param(name = "id") id: String) {
        if (MineHandler.getMineById(id).isPresent) {
            player.sendMessage("${ChatColor.RED}Mines must have a unique ID and `${ChatColor.WHITE}$id${ChatColor.RED}` is already taken.")
            return
        }

        try {
            val mine: Mine = when (type.toLowerCase()) {
                "normal" -> {
                    NormalMine(id)
                }
                "luckyblock" -> {
                    LuckyBlockMine(id)
                }
                "party" -> {
                    MinePartyMine(id)
                }
                else -> {
                    player.sendMessage("${ChatColor.RED}Error creating mine: Invalid mine type")
                    player.sendMessage("${ChatColor.RED}Try one of the following: normal, luckyblock, party")
                    return
                }
            }

            MineHandler.trackMine(mine)
            MineHandler.saveData()

            RegionHandler.trackRegion(mine)
            RegionHandler.updateBlockCache(mine)

            player.sendMessage("${ChatColor.GREEN}Successfully created new mine ${ChatColor.WHITE}$id${ChatColor.GREEN}.")
        } catch (e: IllegalStateException) {
            player.sendMessage("${ChatColor.RED}Failed to create mine: ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}