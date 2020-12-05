/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.CubedOptions
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.serialize.AbstractTypeSerializer
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.generator.EmptyChunkGenerator
import net.evilblock.prisonaio.listener.PrematureLoadListeners
import net.evilblock.prisonaio.module.battlepass.BattlePassModule
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.gang.GangModule
import net.evilblock.prisonaio.module.chat.ChatModule
import net.evilblock.prisonaio.module.combat.CombatModule
import net.evilblock.prisonaio.module.exchange.GrandExchangeModule
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorsModule
import net.evilblock.prisonaio.module.tool.ToolsModule
import net.evilblock.prisonaio.module.system.SystemModule
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.mine.Mine
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
import net.evilblock.prisonaio.module.shop.ShopsModule
import net.evilblock.prisonaio.module.storage.StorageModule
import net.evilblock.prisonaio.module.tool.enchant.config.formula.PriceFormulaType
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import net.evilblock.prisonaio.service.ServicesThread
import org.bukkit.generator.ChunkGenerator

class PrisonAIO : PluginFramework() {

    companion object {
        @JvmStatic lateinit var instance: PrisonAIO
    }

    var loaded: Boolean = false
    var running: Boolean = false

    val enabledModules: MutableList<PluginModule> = arrayListOf()

    override fun onEnable() {
        instance = this

        server.pluginManager.registerEvents(PrematureLoadListeners, this)

        Cubed.instance.configureOptions(CubedOptions(requireRedis = true, requireMongo = true))

        // register this plugins gson type adapters
        Cubed.instance.useGsonBuilderThenRebuild { builder ->
            builder.registerTypeAdapter(QuestProgress::class.java, QuestProgress.Serializer())
            builder.registerTypeAdapter(BlockType::class.java, BlockType.Serializer())
            builder.registerTypeAdapter(DeliveryManRewardRequirement::class.java, DeliveryManRewardRequirement.Serializer())
            builder.registerTypeAdapter(UserSettingOption::class.java, AbstractTypeSerializer<UserSettingOption>())
            builder.registerTypeAdapter(Challenge::class.java, AbstractTypeSerializer<Challenge>())
            builder.registerTypeAdapter(Mine::class.java, AbstractTypeSerializer<Mine>())
            builder.registerTypeAdapter(PriceFormulaType.PriceFormula::class.java, AbstractTypeSerializer<PriceFormulaType.PriceFormula>())
            builder.registerTypeAdapter(Generator::class.java, AbstractTypeSerializer<Generator>())
        }

        enabledModules.addAll(arrayListOf(
            SystemModule,
            StorageModule,
            RegionsModule,
            MechanicsModule,
            ToolsModule,
            RanksModule,
//            AchievementsModule,
            QuestsModule,
            ShopsModule,
            MinesModule,
            GangModule,
            PrivateMinesModule,
            BattlePassModule,
            GrandExchangeModule,
            UsersModule,
            ChatModule,
            RewardsModule,
            MinigamesModule,
            LeaderboardsModule,
            CombatModule,
            GeneratorsModule
        ))

        super.onEnable()

        Tasks.delayed(20L) {
            loaded = true

            ServicesThread().start()
        }
    }

    override fun onDisable() {
        super.onDisable()
    }

    override fun getModules(): List<PluginModule> {
        return enabledModules
    }

    override fun getDefaultWorldGenerator(worldName: String?, id: String?): ChunkGenerator {
        return EmptyChunkGenerator()
    }

}