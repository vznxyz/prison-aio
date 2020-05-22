package net.evilblock.prisonaio.module.mine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.menus.ExitButton
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.block.BlockType
import net.evilblock.prisonaio.module.mine.prompt.MineEditBlockChancePrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class MineManageBlocksMenu(private val mine: Mine) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Manage Blocks - ${mine.id}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        // toolbar
        buttons[0] = BackButton { _player -> MineEditMenu(mine).openMenu(_player) }
        buttons[8] = ExitButton()

        // toolbar separator
        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        // list block types
        mine.blocksConfig.blockTypes.forEachIndexed { index, blockType ->
            buttons[18 + index] = BlockTypeButton(mine, blockType)
        }

        val startFrom = 18 + mine.blocksConfig.blockTypes.size
        for (index in startFrom until 54) {
            buttons[index] = EmptySlotButton()
        }

        return buttons
    }

    override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        if (itemStack.type != Material.AIR) {
            val cursorItemStack = itemStack.clone()
            if (!mine.blocksConfig.blockTypes.add(BlockType(cursorItemStack.type, cursorItemStack.durability.toByte(), 0.0))) {
                player.sendMessage("${ChatColor.RED}The mine already has that block listed!")
                return false
            }

            MineHandler.saveData()
            player.updateInventory()
            return true
        }

        return false
    }

    private inner class EmptySlotButton : Button() {
        override fun getName(player: Player): String {
            return " "
        }

        override fun getDescription(player: Player): List<String> {
            return emptyList()
        }

        override fun getMaterial(player: Player): Material {
            return Material.AIR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (view.cursor != null && view.cursor.type != Material.AIR) {
                val cursorItemStack = view.cursor.clone()

                if (!cursorItemStack.type.isBlock) {
                    player.sendMessage("${ChatColor.RED}That isn't a block!")
                    return
                }

                val blockType = BlockType(cursorItemStack.type, cursorItemStack.durability.toByte(), 0.0)

                if (mine.blocksConfig.blockTypes.add(blockType)) {
                    view.cursor = null
                    MineHandler.saveData()
                } else {
                    view.cursor = cursorItemStack
                    player.sendMessage("${ChatColor.RED}The mine already has that block listed!")
                }

                player.updateInventory()
            }
        }
    }

    private inner class BlockTypeButton(private val mine: Mine, private val blockType: BlockType) : Button() {
        init {
            preserveName = true
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}This block has a ${ChatColor.AQUA}${blockType.percentage}% ${ChatColor.GRAY}chance",
                "${ChatColor.GRAY}of spawning.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit chance",
                "${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to remove block"
            )
        }

        override fun getMaterial(player: Player): Material {
            return blockType.material
        }

        override fun getDamageValue(player: Player): Byte {
            return blockType.data
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                // close menu
                player.closeInventory()

                // start conversation
                ConversationUtil.startConversation(player, MineEditBlockChancePrompt(mine, blockType))
            } else if (clickType.isRightClick) {
                // remove block type
                mine.blocksConfig.blockTypes.remove(blockType)

                // save changes to file
                MineHandler.saveData()
            }
        }
    }

}