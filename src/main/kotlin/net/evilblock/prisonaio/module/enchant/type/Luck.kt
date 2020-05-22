package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.mechanic.region.Region
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrate
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

object Luck : AbstractEnchant("luck", "Luck", 4) {

    override val iconColor: Color
        get() = Color.LIME

    override val textColor: ChatColor
        get() = ChatColor.GREEN

    override val menuDisplay: Material
        get() = Material.RABBIT_FOOT

    override fun getCost(level: Int): Long {
        return (6000 + (level - 1) * 1200).toLong()
    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        if (!Chance.percent(level * 10)) {
            return
        }

        if (region.getBreakableRegion()?.contains(event.block) == false) {
            return
        }

        if (MineCrateHandler.isOnCooldown(event.player)) {
            return
        }

        for (rewardSet in MineCrateHandler.getRewardSets().filter { it.worlds.contains(event.block.world.name) }.shuffled()) {
            if (Chance.percent(rewardSet.chance + (level * 5))) {
                MineCrateHandler.resetCooldown(event.player)

                // wait a tick before updating the block
                PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                    val mineCrate = MineCrate(event.block.location, event.player.uniqueId, rewardSet)
                    MineCrateHandler.trackSpawnedCrate(mineCrate)
                }, 1L)

                event.player.sendMessage("${RewardsModule.getChatPrefix()}You just found a MineCrate! ${ChatColor.GRAY}(${ChatColor.YELLOW}Luck Boosted${ChatColor.GRAY})")

                return
            }
        }
    }

}