/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user

import com.google.gson.annotations.JsonAdapter
import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.achievement.Achievement
import net.evilblock.prisonaio.module.battlepass.progress.BattlePass
import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.progression.QuestProgression
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.rank.RanksModule
import net.evilblock.prisonaio.module.rank.event.PlayerRankupEvent
import net.evilblock.prisonaio.module.rank.serialize.RankReferenceSerializer
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.prisonaio.module.user.activity.type.CompletedAchievementActivity
import net.evilblock.prisonaio.module.user.perk.UserPerks
import net.evilblock.prisonaio.module.user.profile.ProfileComment
import net.evilblock.prisonaio.module.user.statistic.UserStatistics
import net.evilblock.prisonaio.module.user.serialize.UserClaimedRewardsSerializer
import net.evilblock.prisonaio.module.user.serialize.UserQuestProgressionSerializer
import net.evilblock.prisonaio.module.user.setting.UserSetting
import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import java.util.*

class User(val uuid: UUID) {

    /**
     * Used to determine if we need to save this user's data to the database the next time our plugin data is saved.
     */
    @Transient
    internal var requiresSave: Boolean = false

    /**
     * Used to assign permissions granted by the user's [currentRank] and [currentPrestige].
     */
    @Transient
    var attachment: PermissionAttachment? = null

    /**
     * The user's current rank.
     */
    @JsonAdapter(value = RankReferenceSerializer::class)
    private var currentRank: Rank = RankHandler.getStartingRank()

    /**
     * The user's current prestige.
     */
    private var currentPrestige: Int = 0

    /**
     * The user's current tokens balance.
     */
    private var tokensBalance: Long = 0L

    /**
     * The first time the player was seen on the server.
     */
    internal var firstSeen: Long = System.currentTimeMillis()

    /**
     * This user's perks.
     */
    val perks: UserPerks = UserPerks(this)

    /**
     * This user's statistics.
     */
    val statistics: UserStatistics = UserStatistics(this)

    /**
     * This user's settings.
     */
    private val settings: MutableMap<UserSetting, UserSettingOption> = EnumMap(UserSetting::class.java)

    /**
     * The comments left on this user's profile.
     */
    private val profileComments: MutableList<ProfileComment> = arrayListOf()

    /**
     * The user's completed achievements.
     */
    private val achievements: MutableMap<String, CompletedAchievementActivity> = hashMapOf()

    /**
     * The user's Battle-Pass progress.
     */
    var battlePassData: BattlePass = BattlePass(this)

    /**
     * The user's quest progressions.
     */
    @JsonAdapter(UserQuestProgressionSerializer::class)
    private val questProgression: MutableMap<Quest<*>, QuestProgression> = hashMapOf()

    /**
     * The timestamps of when the user last claimed Delivery Man rewards.
     */
    @JsonAdapter(UserClaimedRewardsSerializer::class)
    private val claimedRewards: MutableMap<DeliveryManReward, Long> = hashMapOf()

    fun init() {
        perks.user = this
        statistics.user = this

        if (battlePassData == null) {
            battlePassData = BattlePass(this)
        }

        battlePassData.user = this
    }

    /**
     * Gets the user's username.
     */
    fun getUsername(): String {
        return Cubed.instance.uuidCache.name(uuid)
    }

    fun getPlayer(): Player? {
        return Bukkit.getPlayer(uuid)
    }

    /**
     * If this user's data has been updated and needs to be saved.
     */
    fun requiresSave(): Boolean {
        if (requiresSave) {
            return true
        }

        for (progress in questProgression.values) {
            if (progress.requiresSave) {
                return true
            }
        }

        return false
    }

    /**
     * Gets the user's current rank.
     */
    fun getCurrentRank(): Rank {
        return currentRank
    }

    /**
     * Gets the user's current prestige.
     */
    fun getCurrentPrestige(): Int {
        return currentPrestige
    }

    /**
     * Updates the user's setting option for the given [setting].
     */
    fun updateSettingOption(setting: UserSetting, value: UserSettingOption) {
        settings[setting] = value
        requiresSave = true
    }

    /**
     * Gets the user's setting option for the given [setting].
     */
    fun getSettingOption(setting: UserSetting): UserSettingOption {
        if (!settings.containsKey(setting)) {
            settings[setting] = setting.newDefaultOption()
        }
        return settings[setting]!!
    }

    /**
     * Gets a copy of the user's [profileComments].
     */
    fun getProfileComments(): List<ProfileComment> {
        return profileComments.toList()
    }

    /**
     * If the given [player] has posted a comment on this user's profile.
     */
    fun hasPostedProfileComment(player: UUID): Boolean {
        return profileComments.firstOrNull { it.creator == player } != null
    }

    /**
     * Adds the given [comment] to the user's profile.
     */
    fun addProfileComment(comment: ProfileComment) {
        profileComments.add(comment)
        requiresSave = true

        val player = getPlayer()
        if (player != null && player.isOnline) {
            FancyMessage("${ChatColor.YELLOW}A comment has been left on your profile by ${Cubed.instance.uuidCache.name(comment.creator)}! ")
                .then("${ChatColor.GREEN}${ChatColor.BOLD}[VIEW]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to view the comment."))
                .command("/profile ${player.name} comments")
        }
    }

    /**
     * Removes the given [comment] from the user's profile.
     */
    fun removeProfileComment(comment: ProfileComment) {
        profileComments.remove(comment)
        requiresSave = true
    }

    /**
     * If the user has completed the given [achievement].
     */
    fun hasCompletedAchievement(achievement: Achievement): Boolean {
        return achievements.containsKey(achievement.id)
    }

    /**
     * Marks an achievement as completed for this user.
     */
    fun markAchievementCompleted(achievement: Achievement) {
        achievements[achievement.id] = CompletedAchievementActivity(achievementId = achievement.id)
    }

    /**
     * Gets all of the user's progression data for every quest.
     */
    fun getQuestProgressions(): List<QuestProgression> {
        val list = arrayListOf<QuestProgression>()
        for (quest in QuestHandler.getQuests()) {
            list.add(getQuestProgression(quest))
        }
        return list
    }

    /**
     * Gets the user's progression data for the given [quest].
     */
    fun getQuestProgression(quest: Quest<*>): QuestProgression {
        questProgression.putIfAbsent(quest, quest.startProgress())
        return questProgression[quest]!!
    }

    /**
     * If the user can claim the given [reward].
     */
    fun canClaimReward(reward: DeliveryManReward): Boolean {
        if (!claimedRewards.containsKey(reward)) {
            return true
        }

        return System.currentTimeMillis() - claimedRewards[reward]!! >= reward.cooldown.timeDuration
    }

    /**
     * Gets the remaining milliseconds until the user can claim the given [reward].
     */
    fun getRemainingRewardCooldown(reward: DeliveryManReward): Long {
        return (claimedRewards[reward]!! + reward.cooldown.timeDuration) - System.currentTimeMillis()
    }

    /**
     * Marks the given [reward] as claimed.
     */
    fun markRewardAsClaimed(reward: DeliveryManReward) {
        claimedRewards[reward] = System.currentTimeMillis()
        requiresSave = true
    }

    /**
     * Updates the user's [currentRank] to the given [rank].
     */
    fun updateCurrentRank(rank: Rank) {
        currentRank = rank
        requiresSave = true
    }

    fun purchaseMaxRankups(player: Player, manual: Boolean = false) {
        val previousRank = currentRank

        val optionalNextRank = RankHandler.getNextRank(previousRank)
        if (!optionalNextRank.isPresent) {
            if (manual) {
                player.sendMessage("${ChatColor.RED}You have achieved max rank and cannot rankup anymore. Try /prestige!")
            }
            return
        }

        var balance = getMoneyBalance()

        val purchasedRanks = arrayListOf<Rank>()
        for (rank in RankHandler.getSortedRanks()) {
            if (previousRank.sortOrder >= rank.sortOrder) {
                continue
            }

            val rankPrice = rank.getPrice(currentPrestige)
            if (rankPrice > balance) {
                break
            }

            val playerRankupEvent = PlayerRankupEvent(player, previousRank, rank)
            Bukkit.getServer().pluginManager.callEvent(playerRankupEvent)

            if (playerRankupEvent.isCancelled) {
                return
            }

            VaultHook.useEconomy { economy ->
                val response = economy.withdrawPlayer(player, rankPrice.toDouble())
                if (!response.transactionSuccess()) {
                    return@useEconomy
                }

                balance -= rankPrice

                updateCurrentRank(rank)
                rank.executeCommands(player)

                purchasedRanks.add(rank)
            }
        }

        applyPermissions(player)

        if (purchasedRanks.isEmpty()) {
            if (manual) {
                player.sendMessage("")
                player.sendMessage(" ${ChatColor.RED}${ChatColor.BOLD}Cannot Afford Rankup")
                player.sendMessage(" ${ChatColor.GRAY}You don't have enough money to purchase any rankups.")
                player.sendMessage("")
            }
            return
        }

        player.sendMessage("")
        player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Rankups Purchased${ if (!manual) "${ChatColor.GRAY}(Auto Rankup)" else "" }")
        player.sendMessage(" ${ChatColor.GRAY}Congratulations on your rankups from ${previousRank.displayName} ${ChatColor.GRAY}to ${currentRank.displayName}${ChatColor.GRAY}!")

        val formattedMoneySpent = NumberUtils.format(purchasedRanks.map { it.getPrice(currentPrestige) }.sum())
        player.sendMessage(" ${ChatColor.GRAY}The rankups cost ${ChatColor.GREEN}$${ChatColor.YELLOW}$formattedMoneySpent${ChatColor.GRAY}.")

        player.sendMessage("")
    }

    /**
     * Updates the user's [currentPrestige] to the given [prestige].
     *
     * If the user's [prestigeReqNotifsSent] is more than or equal to the given [prestige], then that
     * field will be also be updated depending on if the user has met the new prestige requirement.
     */
    fun updateCurrentPrestige(prestige: Int) {
        currentPrestige = prestige
        requiresSave = true
    }

    /**
     * Gets the amount of blocks the user is required to mine before being able to enter the next prestige.
     */
    fun getPrestigeRequirement(): Int {
        return RanksModule.getPrestigeBlocksMinedRequirementBase() + ((currentPrestige + 1) * RanksModule.getPrestigeBlocksMinedRequirementModifier())
    }

    /**
     * Gets the user's current money balance.
     */
    fun getMoneyBalance(): Double {
        return VaultHook.getBalance(uuid)
    }

    /**
     * Gets the user's current [tokensBalance].
     */
    fun getTokensBalance(): Long {
        return tokensBalance
    }

    /**
     * If the user's balance is more than or equal to the given [amount].
     */
    fun hasTokensBalance(amount: Long): Boolean {
        return tokensBalance >= amount
    }

    /**
     * Updates the user's tokens balance to the given [newBalance].
     */
    fun updateTokensBalance(newBalance: Long) {
        tokensBalance = if (newBalance < 0) { 0 } else { newBalance }
        requiresSave = true
    }

    fun addTokensBalance(amount: Long) {
        assert(amount > 0) { "Amount must be more than 0" }
        updateTokensBalance(tokensBalance + amount)
    }

    fun subtractTokensBalance(amount: Long) {
        assert(amount > 0) { "Amount must be more than 0" }
        updateTokensBalance(tokensBalance - amount)
    }

    /**
     * Applies the permissions granted by the user's [currentRank] and [currentPrestige].
     */
    fun applyPermissions(player: Player) {
        if (attachment == null) {
            attachment = player.addAttachment(PrisonAIO.instance)
        } else {
            val permissions = attachment!!.permissions.keys.toList()
            for (permission in permissions) {
                attachment!!.unsetPermission(permission)
            }
        }

        for (permission in currentRank.getCompoundedPermissions()) {
            if (permission.startsWith("-")) {
                attachment!!.setPermission(permission.substring(1), false)
            } else {
                attachment!!.setPermission(permission, true)
            }
        }
    }

}