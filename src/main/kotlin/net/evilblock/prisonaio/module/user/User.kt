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
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.achievement.Achievement
import net.evilblock.prisonaio.module.battlepass.BattlePassProgress
import net.evilblock.prisonaio.module.quest.Quest
import net.evilblock.prisonaio.module.quest.QuestHandler
import net.evilblock.prisonaio.module.quest.progress.QuestProgress
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.rank.RanksModule
import net.evilblock.prisonaio.module.rank.event.PlayerRankupEvent
import net.evilblock.prisonaio.module.rank.serialize.RankReferenceSerializer
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.prisonaio.module.theme.ThemesModule
import net.evilblock.prisonaio.module.theme.user.ThemeUserData
import net.evilblock.prisonaio.module.user.activity.type.CompletedAchievementActivity
import net.evilblock.prisonaio.module.user.auction.UserActionHouseData
import net.evilblock.prisonaio.module.user.news.News
import net.evilblock.prisonaio.module.user.perk.UserPerks
import net.evilblock.prisonaio.module.user.profile.ProfileComment
import net.evilblock.prisonaio.module.user.statistic.UserStatistics
import net.evilblock.prisonaio.module.user.serialize.UserClaimedRewardsSerializer
import net.evilblock.prisonaio.module.user.serialize.UserQuestProgressSerializer
import net.evilblock.prisonaio.module.user.serialize.UserReadNewsPostsSerializer
import net.evilblock.prisonaio.module.user.setting.UserSettings
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class User(val uuid: UUID) {

    @Transient internal var requiresSave: Boolean = false
    @Transient internal var cacheExpiry: Long? = null
    @Transient var attachment: PermissionAttachment? = null

    internal var firstSeen: Long = System.currentTimeMillis()

    internal var nicknameColors: MutableSet<ChatColor> = hashSetOf()
    internal var nicknameColor: ChatColor? = null
    internal var nicknameStyle: ChatColor? = null

    @JsonAdapter(value = RankReferenceSerializer::class)
    private var rank: Rank = RankHandler.getStartingRank()

    private var prestige: Int = 0
    private var prestigeTokens: Int = 0

    private var moneyBalance: BigDecimal = BigDecimal(0.0)
    private var tokenBalance: BigInteger = BigInteger("0")

    var perks: UserPerks = UserPerks(this)
    var statistics: UserStatistics = UserStatistics(this)
    var settings: UserSettings = UserSettings(this)
    var auctionHouseData: UserActionHouseData = UserActionHouseData(this)
    var battlePassProgress: BattlePassProgress = BattlePassProgress(this)

    private val profileComments: MutableList<ProfileComment> = arrayListOf()
    private val achievements: MutableMap<String, CompletedAchievementActivity> = hashMapOf()

    @JsonAdapter(UserQuestProgressSerializer::class)
    private val questProgress: MutableMap<Quest, QuestProgress> = hashMapOf()

    @JsonAdapter(UserClaimedRewardsSerializer::class)
    private val claimedRewards: MutableMap<DeliveryManReward, Long> = hashMapOf()

    @JsonAdapter(UserReadNewsPostsSerializer::class)
    internal var readNews: MutableSet<News> = hashSetOf()

    var themeUserData: ThemeUserData? = null

    fun init() {
        perks.user = this
        statistics.user = this
        settings.user = this
        auctionHouseData.user = this
        battlePassProgress.user = this

        if (ThemesModule.isEnabled() && ThemesModule.isThemeEnabled() && ThemesModule.getTheme().hasUserDataImplementation()) {
            if (themeUserData == null) {
                themeUserData = ThemesModule.getTheme().createUserData(this)
            }

            if (themeUserData != null) {
                themeUserData!!.user = this
            }
        }
    }

    /**
     * Gets the user's username.
     */
    fun getUsername(): String {
        return Cubed.instance.uuidCache.name(uuid)
    }

    fun hasNicknameColor(color: ChatColor): Boolean {
        return nicknameColors.contains(color)
    }

    fun hasNicknameColor(color: ChatColor, player: Player): Boolean {
        return nicknameColors.contains(color)
                || player.hasPermission("prisonaio.users.nickname.color.*")
                || player.hasPermission("prisonaio.users.nickname.color.${color.name.toLowerCase()}")
    }

    fun getFormattedUsername(player: Player): String {
        if (nicknameColor == null && nicknameStyle == null) {
            return player.displayName
        }

        val builder = StringBuilder()

        if (nicknameColor != null) {
            builder.append(nicknameColor)
        } else {
            builder.append(ChatColor.getLastColors(player.displayName))
        }

        if (nicknameStyle != null) {
            builder.append(nicknameStyle)
        }

        return builder.append(player.name).toString()
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

        for (progress in questProgress.values) {
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

        val nextRank = RankHandler.getNextRank(previousRank)
        if (nextRank == null) {
            if (manual) {
                player.sendMessage("${ChatColor.RED}You have achieved max rank and cannot rankup anymore. Try /prestige!")
            }
            return
        }

        val purchasedRanks = arrayListOf<Rank>()
        for (rank in RankHandler.getSortedRanks()) {
            if (previousRank.sortOrder >= rank.sortOrder) {
                continue
            }

            val rankPrice = rank.getPrice(prestige)
            if (!hasMoneyBalance(rankPrice)) {
                break
            }

            val playerRankupEvent = PlayerRankupEvent(player, previousRank, rank)
            Bukkit.getServer().pluginManager.callEvent(playerRankupEvent)

            if (playerRankupEvent.isCancelled) {
                return
            }

            subtractMoneyBalance(rankPrice)

            updateRank(rank)
            rank.executeCommands(player)

            purchasedRanks.add(rank)
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
    fun getMoneyBalance(): BigDecimal {
        return moneyBalance
    }

    /**
     * If the user's [moneyBalance] is more than or equal to the given [amount].
     */
    fun hasMoneyBalance(amount: BigDecimal): Boolean {
        return moneyBalance >= amount
    }

    /**
     * If the user's [moneyBalance] is more than or equal to the given [amount].
     */
    fun hasMoneyBalance(amount: Double): Boolean {
        return hasMoneyBalance(BigDecimal(amount))
    }

    /**
     * Updates the user's [moneyBalance] to the given [newBalance].
     */
    fun updateMoneyBalance(newBalance: BigDecimal) {
        moneyBalance = newBalance.coerceAtLeast(UserHandler.MINIMUM_MONEY_BALANCE)
        requiresSave = true
    }

    /**
     * Adds the given [amount] to the user's [moneyBalance].
     */
    fun addMoneyBalance(amount: BigDecimal) {
        assert(amount > UserHandler.MINIMUM_MONEY_BALANCE) { "Amount must be more than 0" }
        updateMoneyBalance(moneyBalance + amount)
    }

    /**
     * Adds the given [amount] to the user's [moneyBalance].
     */
    fun addMoneyBalance(amount: Double) {
        addMoneyBalance(BigDecimal(amount))
    }

    /**
     * Subtracts the given [amount] from the users [moneyBalance].
     */
    fun subtractMoneyBalance(amount: BigDecimal) {
        assert(amount > UserHandler.MINIMUM_MONEY_BALANCE) { "Amount must be more than 0" }
        assert(moneyBalance - amount > UserHandler.MINIMUM_MONEY_BALANCE) { "Can't subtract money to make balance negative" }
        updateMoneyBalance(moneyBalance - amount)
    }

    /**
     * Subtracts the given [amount] from the users [moneyBalance].
     */
    fun subtractMoneyBalance(amount: Double) {
        subtractMoneyBalance(BigDecimal(amount))
    }

    /**
     * Gets the user's current [tokenBalance].
     */
    fun getTokenBalance(): BigInteger {
        return tokenBalance
    }

    /**
     * If the user's [tokenBalance] is more than or equal to the given [amount].
     */
    fun hasTokenBalance(amount: BigInteger): Boolean {
        return tokenBalance >= amount
    }

    /**
     * If the user's [tokenBalance] is more than or equal to the given [amount].
     */
    fun hasTokenBalance(amount: Long): Boolean {
        return hasTokenBalance(BigInteger(amount.toString()))
    }

    /**
     * Updates the user's [tokenBalance] to the given [newBalance].
     */
    fun updateTokenBalance(newBalance: BigInteger) {
        tokenBalance = newBalance.coerceAtLeast(UserHandler.MINIMUM_TOKEN_BALANCE)
        requiresSave = true
    }

    /**
     * Adds the given [amount] to the user's [tokenBalance].
     */
    fun addTokensBalance(amount: BigInteger) {
        assert(amount > UserHandler.MINIMUM_TOKEN_BALANCE) { "Amount must be more than 0" }
        updateTokenBalance(tokenBalance + amount)
    }

    /**
     * Adds the given [amount] to the user's [tokenBalance].
     */
    fun addTokensBalance(amount: Long) {
        addTokensBalance(BigInteger(amount.toString()))
    }

    /**
     * Subtracts the given [amount] from the users [tokenBalance].
     */
    fun subtractTokensBalance(amount: BigInteger) {
        assert(amount > UserHandler.MINIMUM_TOKEN_BALANCE) { "Amount must be more than 0" }
        assert(tokenBalance - amount > UserHandler.MINIMUM_TOKEN_BALANCE) { "Can't subtract tokens to make balance negative" }
        updateTokenBalance(tokenBalance - amount)
    }

    /**
     * Subtracts the given [amount] from the users [tokenBalance].
     */
    fun subtractTokensBalance(amount: Long) {
        subtractTokensBalance(BigInteger(amount.toString()))
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
        if (player != null) {
            FancyMessage("${ChatColor.YELLOW}A comment has been left on your profile by ${Cubed.instance.uuidCache.name(comment.creator)}! ")
                .then("${ChatColor.GREEN}${ChatColor.BOLD}[VIEW]")
                .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to view the comment."))
                .command("/profile ${player.name} comments")
                .send(player)
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
     * Gets all of the user's progress for all quests.
     */
    fun getAllQuestsProgress(): List<QuestProgress> {
        val list = arrayListOf<QuestProgress>()
        for (quest in QuestHandler.getQuests()) {
            list.add(getQuestProgress(quest))
        }
        return list
    }

    /**
     * Gets the user's progress for the given [quest].
     */
    fun getQuestProgress(quest: Quest): QuestProgress {
        questProgress.putIfAbsent(quest, quest.startProgress())
        return questProgress[quest]!!
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
     * Gets the progress milliseconds until the user can claim the given [reward].
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

    fun hasReadNewsPost(news: News): Boolean {
        return readNews.contains(news)
    }

    fun markNewsPostAsRead(news: News) {
        readNews.add(news)
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