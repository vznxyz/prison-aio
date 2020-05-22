package net.evilblock.prisonaio.module.user.profile.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.prisonaio.module.user.User
import org.bukkit.entity.Player

class ProfileCommentsMenu(user: User) : ProfileMenuTemplate(user = user, tab = ProfileMenuTab.COMMENTS) {

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = super.getButtons(player) as MutableMap<Int, Button>

//        user.profileComments.forEach

        return buttons
    }

    companion object {
        private val LIST_SLOTS = arrayListOf(
            20, 21, 22, 23, 24, 25,
            29, 30, 31, 32, 33, 34,
            38, 39, 40, 41, 42, 43
        )
    }

}