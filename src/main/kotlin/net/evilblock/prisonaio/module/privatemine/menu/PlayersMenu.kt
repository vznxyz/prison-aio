package net.evilblock.prisonaio.module.privatemine.menu

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.privatemine.PrivateMinesModule
import net.evilblock.prisonaio.module.privatemine.PrivateMine
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class PlayersMenu(private val mine: PrivateMine) : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return mine.translateVariables(PrivateMinesModule.getMenuTitle("players-menu"))
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        var lastIndex = -1

        mine.whitelistedPlayers.filter { it != mine.owner }.forEachIndexed { index, minePlayer ->
            val slot = adjustOffsetSlot(index)
            buttons[slot] = PlayerInfoButton(minePlayer)
            buttons[slot + 9] = RemoveButton(mine, minePlayer)
            lastIndex = slot
        }

        val indexOffset = if (lastIndex == -1) 0 else lastIndex + 1
        val displayedInRow = indexOffset % 8

        if (lastIndex == -1 || displayedInRow > 0) {
            val endAt = indexOffset + (8 - displayedInRow)

            for (i in indexOffset .. endAt) {
                val slot = adjustOffsetSlot(i)
                buttons[slot] = Button.placeholder(Material.SKULL_ITEM, 3, "&7Empty Slot")
                buttons[slot + 9] = AddButton(mine)
            }
        }

        return buttons
    }

    private fun adjustOffsetSlot(index: Int): Int {
        return index + ((index / 9) * 9)
    }

    class PlayerInfoButton(private val player: UUID) : Button() {
        override fun getName(player: Player): String {
            return "&e&l${Cubed.instance.uuidCache.name(this.player)}"
        }

        override fun getDescription(player: Player): List<String> {
            return emptyList()
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta {
            (itemMeta as SkullMeta).owner = Cubed.instance.uuidCache.name(this.player)
            return itemMeta
        }
    }

    class RemoveButton(private val mine: PrivateMine,
                       private val player: UUID) : Button() {
        override fun getName(player: Player): String {
            return "&c&lRemove Player"
        }

        override fun getDescription(player: Player): List<String> {
            return emptyList()
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 14
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!mine.whitelistedPlayers.contains(this.player)) {
                player.sendMessage("${ChatColor.RED}That player is not apart of your Private Mine.")
                return
            }

            if (mine.removeFromWhitelistedPlayers(this.player)) {
                PrivateMineHandler.removeAccessToMine(this.player, mine)
                PrivateMineHandler.saveGrid()

                player.sendMessage("${ChatColor.GREEN}You removed ${ChatColor.GRAY}${Cubed.instance.uuidCache.name(this.player)} ${ChatColor.GREEN}from your Private Mine.")
            }
        }
    }

    class AddButton(private val mine: PrivateMine) : Button() {
        override fun getName(player: Player): String {
            return "&a&lAdd Player"
        }

        override fun getDescription(player: Player): List<String> {
            return emptyList()
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 5
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            EzPrompt.Builder()
                .promptText("${ChatColor.GREEN}Please specify the player you'd like to add to your private mine.")
                .acceptInput { player, input ->
                    Tasks.async {
                        val uuid = Cubed.instance.uuidCache.uuid(input)
                        if (uuid == null) {
                            player.sendMessage("${ChatColor.RED}A player by \"${ChatColor.GRAY}$input${ChatColor.RED}\" couldn't be found.")
                            return@async
                        }

                        if (mine.whitelistedPlayers.contains(uuid)) {
                            player.sendMessage("${ChatColor.RED}That player is already a member of your private mine.")
                            return@async
                        }

                        if (mine.whitelistedPlayers.size >= mine.tier.playerLimit) {
                            player.sendMessage("${ChatColor.RED}Your private mine has reached its reserved slot limit (${mine.tier.playerLimit} slots).")
                            player.sendMessage("${ChatColor.RED}Each tier gets more reserved slots than the previous.")
                            return@async
                        }

                        if (mine.addToWhitelistedPlayers(uuid)) {
                            PrivateMineHandler.addAccessToMine(uuid, mine)
                            PrivateMineHandler.saveGrid()

                            player.sendMessage("${ChatColor.GREEN}You added ${ChatColor.GRAY}$input${ChatColor.GREEN} to your private mine.")
                        }
                    }
                }
                .build()
                .start(player)
        }
    }

}