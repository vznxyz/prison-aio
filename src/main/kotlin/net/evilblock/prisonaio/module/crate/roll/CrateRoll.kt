package net.evilblock.prisonaio.module.crate.roll

import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import net.evilblock.prisonaio.module.crate.placed.PlacedCrate
import net.evilblock.prisonaio.module.crate.roll.hologram.CrateRollHologram
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class CrateRoll(player: Player, val placedCrate: PlacedCrate) {

    val rolledBy: UUID = player.uniqueId
    private val hologram: CrateRollHologram = CrateRollHologram(this)

    private val showcase = arrayListOf<CrateReward>()
    private val winnings = arrayListOf<CrateReward>()

    private var lastTick: Long = System.currentTimeMillis()
    private var stages: LinkedList<CrateRollStage> = LinkedList<CrateRollStage>()
    private var stage: Int = 0
    private var currentShowcaseReward: Int = 0
    private var finished: Boolean = false

    init {
        calculateRoll()

        hologram.initializeData()
        hologram.spawn(player)

        updateChestTileEntity(player = player, open = true)

        stages.add(CrateRollStage(stageLength = 1000L, onFinish = {
            showcaseCurrentReward()
        }))

        for (i in 0..4) {
            stages.add(CrateRollStage(stageLength = 200L, onFinish = {
                showcaseCurrentReward()
            }))
        }

        for (i in 0..2) {
            stages.add(CrateRollStage(stageLength = 500L, onFinish = {
                showcaseCurrentReward()
            }))
        }

        stages.add(CrateRollStage(stageLength = 1000L, onFinish = {
            showcaseCurrentReward()
        }))

        stages.add(CrateRollStage(stageLength = 1000L, onFinish = {
            showcaseFinalReward()
        }))

        stages.add(CrateRollStage(stageLength = 3000L, onFinish = {}))
    }

    fun tick() {
        val currentStage = stages[stage]
        if (!currentStage.started) {
            currentStage.start()
        }

        if (System.currentTimeMillis() - currentStage.startedAt >= currentStage.stageLength) {
            currentStage.finish()

            if (stage + 1 < stages.size) {
                stage++
            } else {
                finish(Bukkit.getPlayer(rolledBy))
            }
        }

        lastTick = System.currentTimeMillis()
    }

    fun canTick(): Boolean {
        return !finished && System.currentTimeMillis() - lastTick >= 100L
    }

    fun isFinished(): Boolean {
        return finished
    }

    fun finish(player: Player) {
        finished = true

        hologram.destroy(player)

        updateChestTileEntity(player = player, open = false)

        for (reward in winnings) {
            reward.execute(player)
        }
    }

    private fun calculateRoll() {
        if (!placedCrate.crate.isSetup()) {
            throw IllegalStateException("Cannot calculate roll if crate has no rewards or no rewards with a chance above 0.0 (ID: ${placedCrate.crate.getRawName()})")
        }

        val showcase = arrayListOf<CrateReward>()

        w@ while (showcase.size < 10) {
            for (reward in placedCrate.crate.rewards.shuffled()) {
                if (Chance.percent(reward.chance)) {
                    showcase.add(reward)
                }
            }
        }

        val winnings = arrayListOf<CrateReward>()
        var amountOfRewards = placedCrate.crate.rewardsRange.first
        val remainingAmount = placedCrate.crate.rewardsRange.last - placedCrate.crate.rewardsRange.first
        var extraRewardChance = 10.0
        for (i in 0 until remainingAmount) {
            if (Chance.percent(extraRewardChance)) {
                amountOfRewards++
                extraRewardChance /= 10.0
            } else {
                break
            }
        }

        amountOfRewards = amountOfRewards.coerceAtMost(placedCrate.crate.rewardsRange.last)

        w@ while (winnings.size < amountOfRewards) {
            for (reward in showcase.shuffled()) {
                if (Chance.percent(reward.chance)) {
                    winnings.add(reward)

                    if (winnings.size >= amountOfRewards) {
                        break@w
                    }
                }
            }
        }

        this.showcase.clear()
        this.showcase.addAll(showcase)

        this.winnings.clear()
        this.winnings.addAll(winnings)
    }

    private fun showcaseCurrentReward() {
        hologram.updateLines(listOf(showcase[currentShowcaseReward++].name))
    }

    private fun showcaseFinalReward() {
        hologram.updateLines(listOf("${ChatColor.MAGIC}-${ChatColor.RESET} ${winnings.last().name} ${ChatColor.RESET}${ChatColor.MAGIC}-"))
    }

    private fun updateChestTileEntity(player: Player, open: Boolean) {
        MinecraftProtocol.send(player, MinecraftProtocol.newBlockActionPacket(placedCrate.location.block, 1, if (open) 1 else 0))
    }

}