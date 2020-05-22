package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PickaxeInHandButton(private val pickaxeInHand: ItemStack) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        return pickaxeInHand
    }

}