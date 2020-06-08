package net.evilblock.prisonaio.module.reward.deliveryman.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.Constants
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class DeliveryManMenu(private val user: User) : Menu() {

    init {
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return DeliveryManHandler.getDeliveryManMenuTitle()
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in BORDER_SLOTS) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 14, " ")
        }

        DeliveryManHandler.getRewards()
            .sortedBy { it.order }
            .forEachIndexed { index, reward ->
                buttons[REWARD_SLOTS[index]] = RewardButton(reward)
            }

        for (i in 0 until 54) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
            }
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class RewardButton(private val reward: DeliveryManReward) : Button() {
        override fun getName(player: Player): String {
            val color = if (user.canClaimReward(reward)) {
                ChatColor.GREEN
            } else {
                ChatColor.RED
            }

            return "$color${ChatColor.BOLD}${reward.name} ${ChatColor.GRAY}(${reward.cooldown.displayName}${ChatColor.GRAY})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}You are able to claim this reward")
            description.add("${ChatColor.GRAY}every ${ChatColor.AQUA}${reward.cooldown.getFormattedText()}${ChatColor.GRAY}.")
            description.add("")
            description.add("${ChatColor.GRAY}This reward contains the following:")

            for (line in reward.rewardsText) {
                description.add(" ${ChatColor.GRAY}${Constants.DOT_SYMBOL} $line")
            }

            description.add("")

            if (!reward.meetsRequirements(player)) {
                description.add("${ChatColor.RED}${ChatColor.BOLD}Requirements")

                for (requirement in reward.requirements) {
                    val color = if (requirement.test(player)) {
                        "${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}"
                    } else {
                        "${ChatColor.GRAY}"
                    }

                    description.add(" ${ChatColor.GRAY}${Constants.DOT_SYMBOL} $color${requirement.getText()}")
                }
            } else {
                if (user.canClaimReward(reward)) {
                    description.add("${ChatColor.YELLOW}Click to claim this free reward!")
                } else {
                    val remainingTime = TimeUtil.formatIntoAbbreviatedString((user.getRemainingRewardCooldown(reward) / 1000.0).toInt())

                    description.add("${ChatColor.RED}You can't claim this reward for")
                    description.add("${ChatColor.RED}another ${ChatColor.AQUA}$remainingTime${ChatColor.RED}!")
                }
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return if (user.canClaimReward(reward)) {
                Material.STORAGE_MINECART
            } else {
                Material.MINECART
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (!user.canClaimReward(reward)) {
                    val remainingTime = TimeUtil.formatIntoAbbreviatedString((user.getRemainingRewardCooldown(reward) / 1000.0).toInt())
                    player.sendMessage("${ChatColor.RED}You must wait another ${ChatColor.AQUA}$remainingTime${ChatColor.RED} before being able to claim that reward again.")
                    return
                }

                if (!reward.meetsRequirements(player)) {
                    player.sendMessage("${ChatColor.RED}You don't meet the requirements to claim that reward.")
                    return
                }

                reward.execute(player)
                user.markRewardAsClaimed(reward)
            }
        }
    }

    companion object {
        private val BORDER_SLOTS = listOf(0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53)
        private val REWARD_SLOTS = arrayListOf<Int>().also {
            it.addAll(11..15)
            it.addAll(20..24)
            it.addAll(29..33)
            it.addAll(38..42)
        }
    }

}