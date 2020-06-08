package net.evilblock.prisonaio.module.battlepass.challenge.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditChallengeMenu(private val challenge: Challenge) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Challenge - ${challenge.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[1] = EditNameButton()
        buttons[3] = EditRewardExperienceButton()

        for (i in 0..8) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
            }
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                ChallengeEditorMenu().openMenu(player)
            }
        }
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "The name is how you want the reward to appear in chat and menu text.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit name")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText("${ChatColor.GREEN}Please input a new name for the challenge.")
                    .acceptInput { player, input ->
                        challenge.name = ChatColor.translateAlternateColorCodes('&', input)
                        TierHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully updated the name of the challenge.")

                        openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class EditRewardExperienceButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Reward Experience"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Current Reward: ${ChatColor.GREEN}+${challenge.rewardXp} XP")
            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "The experience that is rewarded to the player when they complete this challenge.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit reward experience")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.EXP_BOTTLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt { numberInput ->
                    assert(numberInput > 0) { "Number must be above 0" }
                    challenge.rewardXp = numberInput
                    TierHandler.saveData()

                    openMenu(player)
                }.start(player)
            }
        }
    }

}