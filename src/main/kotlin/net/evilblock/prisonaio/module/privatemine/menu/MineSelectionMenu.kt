package net.evilblock.prisonaio.module.privatemine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.prisonaio.module.privatemine.PrivateMine
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.util.function.Function

class MineSelectionMenu(private val title: String, val meta: Meta) : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "$title&r"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        val mines = meta.minesFunc.invoke()
        if (mines.isEmpty()) {
            for (i in 0..8) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 14, "&c&lYou have no mines to select!")
            }
        } else {
            mines.forEachIndexed { index, mine ->
                buttons[index] = SelectButton(mine, this)
            }
        }

        return buttons
    }

    class SelectButton(private val mine: PrivateMine,
                       private val parentMenu: MineSelectionMenu
    ) : Button() {

        override fun getName(player: Player): String {
            return parentMenu.meta.titleFunc.apply(mine)
        }

        override fun getDescription(player: Player): List<String> {
            return parentMenu.meta.loreFunc.apply(mine)
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_PICKAXE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            parentMenu.meta.clickLambda.invoke(mine, clickType)
        }

    }

    class Meta {

        lateinit var minesFunc: () -> Set<PrivateMine>
        lateinit var titleFunc: Function<PrivateMine, String>
        lateinit var loreFunc: Function<PrivateMine, List<String>>
        lateinit var clickLambda: (PrivateMine, ClickType) -> Unit

    }

}