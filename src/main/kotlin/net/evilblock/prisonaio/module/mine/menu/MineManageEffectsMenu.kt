package net.evilblock.prisonaio.module.mine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.menus.ExitButton
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.prompt.MineEditEffectPotencyPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.potion.PotionEffectType

class MineManageEffectsMenu(private val mine: Mine) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Manage Effects - ${mine.id}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        // toolbar
        buttons[0] = BackButton { _player -> MineEditMenu(mine).openMenu(_player) }
        buttons[8] = ExitButton()

        // toolbar separator
        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
        }

        PotionEffectType.values().filterNotNull().forEachIndexed { index, potionEffectType ->
            buttons[18 + index] = EffectButton(mine, potionEffectType)
        }

        return buttons
    }

    private inner class EffectButton(private val mine: Mine, private val potionEffectType: PotionEffectType) : Button() {
        override fun getName(player: Player): String {
            val potionName = potionEffectType.name.toLowerCase().replace("_", " ").capitalize()
            val stringBuilder = StringBuilder("${ChatColor.AQUA}${ChatColor.BOLD}$potionName: ")

            if (mine.effectsConfig.enabledEffects.contains(potionEffectType)) {
                stringBuilder.append("${ChatColor.GREEN}Enabled ${ChatColor.GRAY}(${mine.effectsConfig.effectPotency.getOrDefault(potionEffectType, 1)})")
            } else {
                stringBuilder.append("${ChatColor.RED}Disabled")
            }

            return stringBuilder.toString()
        }

        override fun getDescription(player: Player): List<String> {
            return if (mine.effectsConfig.enabledEffects.contains(potionEffectType)) {
                listOf(
                    "",
                    "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit potency",
                    "${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK to disable"
                )
            } else {
                listOf(
                    "",
                    "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to enable effect"
                )
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.GLOWSTONE_DUST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            var update = false

            if (clickType.isLeftClick) {
                if (mine.effectsConfig.enabledEffects.contains(potionEffectType)) {
                    update = true
                    player.closeInventory()
                    ConversationUtil.startConversation(player, MineEditEffectPotencyPrompt(mine, potionEffectType))
                } else {
                    update = true
                    mine.effectsConfig.enabledEffects.add(potionEffectType)
                }
            } else if (clickType.isRightClick) {
                if (mine.effectsConfig.enabledEffects.contains(potionEffectType)) {
                    update = true
                    mine.effectsConfig.enabledEffects.remove(potionEffectType)
                }
            }

            // save changes to file if updated
            if (update) {
                MineHandler.saveData()
            }
        }
    }

}