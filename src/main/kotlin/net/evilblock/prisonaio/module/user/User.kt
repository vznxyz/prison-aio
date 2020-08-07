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
import net.evilblock.prisonaio.module.battlepass.BattlePassProgress
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

    @Transient internal var requiresSave: Boolean = false
    @Transient internal var cacheExpiry: Long? = null
    @Transient var attachment: PermissionAttachment? = null

    internal var firstSeen: Long = System.currentTimeMillis()

    @JsonAdapter(value = RankReferenceSerializer::class)
    private var rank: Rank = RankHandler.getStartingRank()

    private var prestige: Int = 0
    private var prestigeTokens: Int = 0

    private var tokenBalance: Long = 0L

    val perks: UserPerks = UserPerks(this)
    val statistics: UserStatistics = UserStatistics(this)

    private val settings: MutableMap<UserSetting, UserSettingOption> = EnumMap(UserSetting::class.java)

    private val profileComments: MutableList<ProfileComment> = arrayListOf()
    private val achievements: MutableMap<String, CompletedAchievementActivity> = hashMapOf()

    var battlePassProgress: BattlePassProgress = BattlePassProgress(this)

    @JsonAdapter(UserQuestProgressionSerializer::class)
    private val questProgression: MutableMap<Quest<*>, QuestProgression> = hashMapOf()

    @JsonAdapter(UserClaimedRewardsSerializer::class)
    private val claimedRewards: MutableMap<DeliveryManReward, Long> = hashMapOf()

    fun init() {
        perks.user = this
        statistics.user = this

        // fix null rank
        if (rank == null) {
            rank = RankHandler.getStartingRank()
        }

        // fix null battle pass
        if (battlePassProgress == null) {
            battlePassProgress = BattlePassProgress(this)
        }
        battlePassProgress.user = this
    }

    /**
     * Gets the user's username.
     */
    fun getUsername(): String {
        return Cubed.instance.uuidCache.name(uuid)
    }

    /**
     * Tries to fetch the bukkit [Player] representing this user.
     */
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
    fun getRank(): Rank {
        return rank
    }

    /**
     * Updates the user's [User.rank] to the given [rank].
     */
    fun updateRank(rank: Rank) {
        this.rank = rank
        requiresSave = true
    }

    /**
     * Attempts to purchase any rankups the user can afford.
     */
    fun purchaseMaxRankups(player: Player, manual: Boolean = false) {
        val previousRank = rank

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

            val rankPrice = rank.getPrice(prestige)
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

                updateRank(rank)
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
        player.sendMessage(" ${ChatColor.GRAY}Congratulations on your rankups from ${previousRank.displayName} ${ChatColor.GRAY}to ${rank.displayName}${ChatColor.GRAY}!")

        val formattedMoneySpent = NumberUtils.format(purchasedRanks.map { it.getPrice(prestige) }.sum())
        player.sendMessage(" ${ChatColor.GRAY}The rankups cost ${ChatColor.GREEN}$${ChatColor.YELLOW}$formattedMoneySpent${ChatColor.GRAY}.")

        player.sendMessage("")
    }

    /**
     * Gets the user's prestige.
     */
    fun getPrestige(): Int {
        return prestige
    }

    /**
     * Updates the user's [prestige] to the given [prestige].
     */
    fun updatePrestige(prestige: Int) {
        this.prestige = prestige
        requiresSave = true
    }

    /**
     * Gets the user's [prestigeTokens].
     */
    fun getPrestigeTokens(): Int {
        return prestigeTokens
    }

    /**
     * If the user's [prestigeTokens] is more than or equal to the given [amount].
     */
    fun hasPrestigeTokens(amount: Int): Boolean {
        return prestigeTokens >= amount
    }

    /**
     * Updates the user's [prestigeTokens] to the given [newTokens].
     */
    fun updatePrestigeTokens(newTokens: Int) {
        prestigeTokens = newTokens.coerceAtLeast(0)
        requiresSave = true
    }

    /**
     * Adds the given [amount] to the user's [prestigeTokens].
     */
    fun addPrestigeTokens(amount: Int) {
        assert(amount > 0) { "Amount must be more than 0" }
        updatePrestigeTokens(prestigeTokens + amount)
    }

    /**
     * Subtracts the given [amount] from the users [prestigeTokens].
     */
    fun subtractPrestigeTokens(amount: Int) {
        assert(amount > 0) { "Amount must be more than 0" }
        assert(prestigeTokens - amount > 0) { "Can't subtract tokens to make balance negative" }
        updatePrestigeTokens(prestigeTokens - amount)
    }

    /**
     * Gets the amount of blocks the user is required to mine before being able to enter the next prestige.
     */
    fun getPrestigeRequirement(): Int {
        return RanksModule.getPrestigeBlocksMinedRequirementBase() + ((prestige + 1) * RanksModule.getPrestigeBlocksMinedRequirementModifier())
    }

    /**
     * Gets the user's current money balance.
     */
    fun getMoneyBalance(): Double {
        return VaultHook.getBalance(uuid)
    }

    /**
     * Gets the user's current [tokenBalance].
     */
    fun getTokenBalance(): Long {
        return tokenBalance
    }

    /**
     * If the user's balance is more than or equal to the given [amount].
     */
    fun hasTokenBalance(amount: Long): Boolean {
        return tokenBalance >= amount
    }

    /**
     * Updates the user's token balance to the given [newBalance].
     */
    fun updateTokenBalance(newBalance: Long) {
        tokenBalance = if (newBalance < 0) { 0 } else { newBalance }
        requiresSave = true
    }

    /**
     * Adds the given [amount] to the user's token balance.
     */
    fun addTokensBalance(amount: Long) {
        assert(amount > 0) { "Amount must be more than 0" }
        updateTokenBalance(tokenBalance + amount)
    }

    /**
     * Subtracts the given [amount] from the users [tokenBalance].
     */
    fun subtractTokensBalance(amount: Long) {
        assert(amount > 0) { "Amount must be more than 0" }
        assert(tokenBalance - amount > 0) { "Can't subtract tokens to make balance negative" }
        updateTokenBalance(tokenBalance - amount)
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
    }/**
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
     * Applies the permissions granted by the user's [rank] and [prestige].
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

        for (permission in rank.getCompoundedPermissions()) {
            if (permission.startsWith("-")) {
                attachment!!.setPermission(permission.substring(1), false)
            } else {
                attachment!!.setPermission(permission, true)
            }
        }
    }

}