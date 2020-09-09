/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.CubedOptions
import net.evilblock.cubed.command.CommandHandler
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.serialize.AbstractTypeSerializer
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.generator.EmptyChunkGenerator
import net.evilblock.prisonaio.command.*
import net.evilblock.prisonaio.listener.PrematureLoadListeners
import net.evilblock.prisonaio.module.battlepass.BattlePassModule
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.gang.GangModule
import net.evilblock.prisonaio.module.chat.ChatModule
import net.evilblock.prisonaio.module.combat.CombatModule
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import net.evilblock.prisonaio.module.environment.EnvironmentModule
import net.evilblock.prisonaio.module.environment.wizard.command.RunWizardCommand
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.mine.MinesModule
import net.evilblock.prisonaio.module.mine.block.BlockType
import net.evilblock.prisonaio.module.minigame.MinigamesModule
import net.evilblock.prisonaio.module.privatemine.PrivateMinesModule
import net.evilblock.prisonaio.module.quest.QuestsModule
import net.evilblock.prisonaio.module.quest.progress.QuestProgress
import net.evilblock.prisonaio.module.rank.RanksModule
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirement
import net.evilblock.prisonaio.module.scoreboard.ScoreboardModule
import net.evilblock.prisonaio.module.shop.ShopsModule
import net.evilblock.prisonaio.module.storage.StorageModule
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import net.evilblock.prisonaio.util.economy.EconomyProvider
import net.milkbowl.vault.economy.Economy
import org.bukkit.Location
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.ServicePriority

class PrisonAIO : PluginFramework() {

    var fullyLoaded = false
    val enabledModules: MutableList<PluginModule> = arrayListOf()

    override fun onEnable() {
        instance = this

        server.pluginManager.registerEvents(PrematureLoadListeners, this)
        server.servicesManager.register(Economy::class.java, EconomyProvider(), this, ServicePriority.Lowest)

        Cubed.instance.configureOptions(CubedOptions(requireRedis = true, requireMongo = true))

        // register this plugins gson type adapters
        Cubed.instance.useGsonBuilderThenRebuild { builder ->
            builder.registerTypeAdapter(QuestProgress::class.java, QuestProgress.Serializer)
            builder.registerTypeAdapter(BlockType::class.java, BlockType.Serializer)
            builder.registerTypeAdapter(DeliveryManRewardRequirement::class.java, DeliveryManRewardRequirement.Serializer)
            builder.registerTypeAdapter(UserSettingOption::class.java, AbstractTypeSerializer<UserSettingOption>())
            builder.registerTypeAdapter(Challenge::class.java, AbstractTypeSerializer<Challenge>())
        }

        enabledModules.addAll(arrayListOf(
            EnvironmentModule,
            StorageModule,
            RegionsModule,
            MechanicsModule,
            EnchantsModule,
            RanksModule,
//            AchievementsModule,
            QuestsModule,
            ShopsModule,
            MinesModule,
            GangModule,
            PrivateMinesModule,
            BattlePassModule,
            UsersModule,
            ScoreboardModule,
            ChatModule,
            RewardsModule,
            MinigamesModule,
            LeaderboardsModule,
            CombatModule
        ))

        super.onEnable()

        loadCommands()

        Tasks.delayed(1L) {
            fullyLoaded = true
        }
    }

    override fun getModules(): List<PluginModule> {
        return enabledModules
    }

    fun getSpawnLocation(): Location {
        return server.worlds[0].spawnLocation
    }

    private fun loadCommands() {
        CommandHandler.registerClass(RunWizardCommand::class.java)
        CommandHandler.registerClass(HotFixCommands::class.java)
        CommandHandler.registerClass(GKitzCommand::class.java)
        CommandHandler.registerClass(ReloadCommand::class.java)
        CommandHandler.registerClass(SaveCommand::class.java)
        CommandHandler.registerClass(HealthCommand::class.java)
    }

    override fun getDefaultWorldGenerator(worldName: String?, id: String?): ChunkGenerator {
        return EmptyChunkGenerator()
    }

    companion object {
        @JvmStatic lateinit var instance: PrisonAIO
    }

}