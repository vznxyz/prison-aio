/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player

class PrivateMineNpcEntity(location: Location) : NpcEntity(lines = listOf("${ChatColor.AQUA}${ChatColor.BOLD}Sell Your Inventory"), location = location) {

    override fun initializeData() {
        super.initializeData()

        updateTexture(
            "ewogICJ0aW1lc3RhbXAiIDogMTU5MTU0NzYzNjMwOCwKICAicHJvZmlsZUlkIiA6ICJlYTQ3NDYzYmYyMmE0M2UxOTk5NDg5MmFmMWQwMWViZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTZWxsQWxsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVjNDY3M2U0NTNiNGIzYmQxMmI3OTVmNTVmZmZlMWJmNmJjMThkZGI4YWNkMDA3Mjg0MzI5OTBlNjY5MzYwNiIKICAgIH0KICB9Cn0=",
            "bXlSJiBBP49ttU0pBsns8lknbRv4Zmd/O5nUEFOdf4DlTadSQsYvN3ZUN8VCnE/709NzSFUO3ORcTSG7xVgZTlD2HjKdqmQT9aQpcRYmkt+xYx4E4nzpmTfAftSsd/SZbRa7D1OgJSQ8+qoP0Youp8yAJ40aLvoHkFh4nGJQqH2rqHnhIXrGO9Rn5w5JBn7XIsew0nrWZDTuGKDDmRYtCB/HCRowWZZb7XhzyK2PYLFPrOf7LaF9Cgn48LI2yvNP8mGPfj2XTEiQfugWf5GT8bgUHHBTj9++dW21wwuyI4Dwjri9oPGO2ppC1S5BvdZ3O3upieHlVwd/MknA+gw7zH28qCqP954Q7WvF42IZaGWHldJ30Glj5la9CBmiCbcRTmLZ2VCr8GveigCpIvMMByWXbgaAEkUFA528yUrqBz0CN4YOkB9Ubppu4vlhjT1nQhc68zELO4xy+UXwHIEnV/G1tzlufSA+xUgmJcrkw9SNEK1h6uawb4zx5aYy+YRU7VPDuoM4KJYa428q+zxDULDgGuo5elYV169pFJSg1dynd4N3DzTBkGW9h8SArMednZL2PE4+t9M0ZXo03NdGiW+R7TPQiLh+zYqYKs1SUX62GfidNnmEFamDVlvZ8mGh8Bta8XOrIhQofKf9WX8Qcvo3RUsdEJn5KmBsDiIJGkM="
        )
    }

    override fun onRightClick(player: Player) {
        player.performCommand("sellall")
    }

}