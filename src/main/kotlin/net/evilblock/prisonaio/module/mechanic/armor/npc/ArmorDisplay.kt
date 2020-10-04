/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor.npc

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.StaticItemStackButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorSet
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.lang.reflect.Type

class ArmorDisplay(location: Location, @JsonAdapter(ArmorSetSerializer::class) val armorSet: AbilityArmorSet) : NpcEntity(lines = listOf(""), location = location) {

    override fun initializeData() {
        super.initializeData()

        updateLines(listOf(armorSet.setName, "${ChatColor.GRAY}Armor Set"))

        updateHelmet(armorSet.getHelmet())
        updateChestplate(armorSet.getChestplate())
        updateLeggings(armorSet.getLeggings())
        updateBoots(armorSet.getBoots())
    }

    override fun onRightClick(player: Player) {
        PreviewMenu().openMenu(player)
    }

    companion object {
        private val RED_SLOTS = listOf(12, 14, 27, 29, 31, 33, 35)
    }

    private inner class PreviewMenu : Menu() {
        override fun getTitle(player: Player): String {
            return "${ChatColor.GRAY}Preview of ${armorSet.setName} Armor"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            for (i in RED_SLOTS) {
                buttons[i] = GlassButton(14)
            }

            buttons[13] = InfoButton()
            buttons[28] = StaticItemStackButton(armorSet.getHelmet())
            buttons[30] = StaticItemStackButton(armorSet.getChestplate())
            buttons[32] = StaticItemStackButton(armorSet.getLeggings())
            buttons[34] = StaticItemStackButton(armorSet.getBoots())

            for (i in 0 until 45) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(8)
                }
            }

            return buttons
        }

        override fun size(buttons: Map<Int, Button>): Int {
            return 45
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${armorSet.setName} Armor Set"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(text = armorSet.getSetDescription(), linePrefix = ChatColor.GRAY.toString())
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOOK
        }
    }

    object ArmorSetSerializer : JsonSerializer<AbilityArmorSet>, JsonDeserializer<AbilityArmorSet> {
        override fun serialize(armorSet: AbilityArmorSet, type: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(armorSet.setId)
        }

        override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): AbilityArmorSet? {
            return AbilityArmorHandler.getSetById(json.asString)
        }
    }

}