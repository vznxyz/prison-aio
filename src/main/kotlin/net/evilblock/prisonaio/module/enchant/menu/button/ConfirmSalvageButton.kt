package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter.split
import net.evilblock.cubed.util.bukkit.ColorUtil.toChatColor
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.EnchantsManager.getEnchants
import net.evilblock.prisonaio.module.enchant.menu.SalvagePickaxeMenu
import net.evilblock.prisonaio.module.enchant.salvage.SalvagePreventionHandler
import net.evilblock.prisonaio.module.enchant.type.Cubed
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.text.NumberFormat
import java.util.*

class ConfirmSalvageButton(private val parent: SalvagePickaxeMenu) : Button() {

    companion object {
        private val enchantColorOrder = mapOf<Color, Int>(
            Color.GREEN to 1,
            Color.AQUA to 2,
            Color.ORANGE to 3
        )

        private val ENCHANT_COMPARATOR = Comparator<Map.Entry<AbstractEnchant, Int>> { o1, o2 ->
            val o1Order = enchantColorOrder.getOrDefault(o1.key.iconColor, 4)
            val o2Order = enchantColorOrder.getOrDefault(o2.key.iconColor, 4)
            when {
                o1Order == o2Order -> {
                    return@Comparator 0
                }
                o1Order > o2Order -> {
                    return@Comparator 1
                }
                else -> {
                    return@Comparator -1
                }
            }
        }
    }

    override fun getName(player: Player): String {
        return "${ChatColor.GREEN}${ChatColor.BOLD}Salvage Pickaxe"
    }

    override fun getDescription(player: Player): List<String> {
        val description: MutableList<String> = ArrayList()
        val enchants = getEnchants(parent.pickaxeInHand)

        enchants.entries.stream()
            .filter { entry -> entry.key !is Cubed }
            .sorted(ENCHANT_COMPARATOR)
            .forEach { entry ->
                val formattedReturns = NumberFormat.getInstance().format(entry.key.getSalvageReturns(entry.value))
                description.add("${toChatColor(entry.key.iconColor)}${ChatColor.BOLD}❙ ${ChatColor.GRAY}${entry.key.strippedEnchant} (${ChatColor.GOLD}$formattedReturns${ChatColor.GRAY})")
            }

        description.add("")

        val totalReturns = enchants.entries.stream()
            .filter { entry -> entry.key !is Cubed }
            .mapToLong { entry -> entry.key.getSalvageReturns(entry.value) }
            .sum()

        val formattedTotalReturns = NumberFormat.getInstance().format(totalReturns)
        val returnsText = "You will receive ${ChatColor.GOLD}${ChatColor.BOLD}$formattedTotalReturns ${ChatColor.GRAY}tokens from salvaging your pickaxe."
        description.addAll(split(34, returnsText, ChatColor.GRAY.toString(), " "))

        description.add("")
        description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Click to Salvage your Pickaxe")

        return description
    }

    override fun getMaterial(player: Player): Material {
        return Material.INK_SACK
    }

    override fun getDamageValue(player: Player): Byte {
        return 10
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType == ClickType.LEFT) { // item has no enchants on it, so it cannot be salvaged
            val enchants = SalvagePreventionHandler.getSalvageableLevels(parent.pickaxeInHand)
            if (enchants.isEmpty()) {
                player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe doesn't have any enchantments, therefore it cannot be salvaged.")
                return
            }

            if (enchants.containsKey(Cubed)) {
                player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe has the Cubed enchantment, which makes the pickaxe un-salvagable.")
                return
            }

            parent.setPendingConfirmation(true)
            player.closeInventory()

            // open the confirmation menu
            ConfirmMenu("&c&lAre you sure you want to salvage?") { confirmed: Boolean ->
                if (confirmed) {
                    val totalReturns = enchants.entries.stream()
                        .filter { entry -> entry.key !is Cubed }
                        .mapToLong { entry -> entry.key.getSalvageReturns(entry.value) }
                        .sum()

                    val formattedTotalReturns = NumberFormat.getInstance().format(totalReturns)
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.GRAY}You have salvaged your pickaxe for ${ChatColor.GOLD}$formattedTotalReturns ${ChatColor.GRAY}tokens. It is now gone forever.")

                    val user = UserHandler.getUser(player.uniqueId)
                    user.addTokensBalance(totalReturns)
                } else {
                    parent.setPendingConfirmation(false)
                    parent.openMenu(player)
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You have canceled salvaging your pickaxe.")
                }
            }.openMenu(player)
        }
    }

}