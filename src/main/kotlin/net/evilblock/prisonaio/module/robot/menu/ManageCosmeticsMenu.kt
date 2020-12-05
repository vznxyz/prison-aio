package net.evilblock.prisonaio.module.robot.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.robot.cosmetic.CosmeticHandler
import net.evilblock.prisonaio.module.robot.cosmetic.impl.SkinCosmetic
import net.evilblock.prisonaio.module.robot.impl.MinerRobot
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class ManageCosmeticsMenu(private val robot: MinerRobot) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Robot Cosmetics"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        CosmeticHandler.getRegisteredCosmetics().forEachIndexed { index, cosmetic ->
            val granted = CosmeticHandler.hasBeenGrantedCosmetic(player.uniqueId, cosmetic)

            if (cosmetic is SkinCosmetic) {
                buttons[BUTTON_SLOTS[index]] = SkinCosmeticButton(cosmetic, granted)
            }
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 27
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                ManageRobotMenu(robot).openMenu(player)
            }
        }
    }

    private inner class SkinCosmeticButton(private val cosmetic: SkinCosmetic, private val granted: Boolean) : Button() {
        override fun getName(player: Player): String {
            return cosmetic.getName()
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (granted) {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}UNLOCKED")
            } else {
                description.add("${ChatColor.RED}${ChatColor.BOLD}LOCKED")
            }

            description.add("")
            description.addAll(cosmetic.getDescription())
            description.add("")

            if (robot.hasCosmeticApplied(cosmetic)) {
                description.add("${ChatColor.YELLOW}This cosmetic is currently applied.")
                description.add("")
            }

            description.add("${ChatColor.YELLOW}Click to toggle this cosmetic.")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemUtils.applySkullTexture(super.getButtonItem(player), cosmetic.getTextureValue())
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!granted) {
                player.sendMessage("${ChatColor.RED}You don't own that cosmetic. Purchase it on our store at store.minejunkie.com.")
                return
            }

            if (robot.hasCosmeticApplied(cosmetic)) {
                robot.disableCosmetic(cosmetic)
                playNeutral(player)
            } else {
                robot.enableCosmetic(cosmetic)
                playSuccess(player)
            }

            robot.refreshHologramLines()

            player.closeInventory()
        }
    }

    companion object {
        private val BUTTON_SLOTS = arrayListOf<Int>().also {
            it.addAll(10..16)
            it.addAll(19..25)
            it.addAll(28..34)
            it.addAll(37..43)
        }
    }

}