package net.evilblock.prisonaio.module.reward.deliveryman.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.prisonaio.module.reward.deliveryman.reward.cooldown.DeliveryManCooldown
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class EditDeliveryManRewardCooldownMenu(private val reward: DeliveryManReward) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Cooldown - ${reward.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (cooldown in DeliveryManCooldown.values()) {
            buttons[buttons.size] = CooldownButton(cooldown)
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                EditDeliveryManRewardMenu(reward).openMenu(player)
            }
        }
    }

    private inner class CooldownButton(private val cooldown: DeliveryManCooldown) : Button() {
        override fun getName(player: Player): String {
            return if (reward.cooldown == cooldown) {
                "${ChatColor.GREEN}${ChatColor.BOLD}${cooldown.displayName}"
            } else {
                "${ChatColor.YELLOW}${ChatColor.BOLD}${cooldown.displayName}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}${cooldown.displayName} allows the player to")
            description.add("${ChatColor.GRAY}claim the reward every ${ChatColor.AQUA}${cooldown.getFormattedText()}${ChatColor.GRAY}.")

            if (reward.cooldown != cooldown) {
                description.add("")
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to select ${cooldown.displayName}")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                reward.cooldown = cooldown
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)

            if (reward.cooldown == cooldown) {
                GlowEnchantment.addGlow(item)
            }

            return item
        }
    }

}