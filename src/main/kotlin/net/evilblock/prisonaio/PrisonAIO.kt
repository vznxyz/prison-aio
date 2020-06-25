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
import net.evilblock.cubed.logging.ErrorHandler
import net.evilblock.cubed.serialize.AbstractTypeSerializer
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.generator.EmptyChunkGenerator
import net.evilblock.prisonaio.command.GKitzCommand
import net.evilblock.prisonaio.command.ReloadCommand
import net.evilblock.prisonaio.command.SaveCommand
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.battlepass.BattlePassModule
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.cell.CellsModule
import net.evilblock.prisonaio.module.chat.ChatModule
import net.evilblock.prisonaio.module.combat.CombatModule
import net.evilblock.prisonaio.module.crate.CratesModule
import net.evilblock.prisonaio.module.crate.reward.CrateReward
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
import net.evilblock.prisonaio.module.quest.progression.QuestProgression
import net.evilblock.prisonaio.module.rank.RanksModule
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirement
import net.evilblock.prisonaio.module.scoreboard.ScoreboardModule
import net.evilblock.prisonaio.module.shop.ShopsModule
import net.evilblock.prisonaio.module.storage.StorageModule
import net.evilblock.prisonaio.module.user.UsersModule
import net.evilblock.prisonaio.module.user.setting.UserSettingOption
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.java.JavaPlugin

class PrisonAIO : JavaPlugin() {

    val enabledModules = arrayListOf<PluginModule>()

    override fun onEnable() {
        instance = this

        Cubed.instance.configureOptions(CubedOptions(requireRedis = true, requireMongo = true))

        // register this plugins gson type adapters
        Cubed.instance.useGsonBuilderThenRebuild { builder ->
            builder.registerTypeAdapter(QuestProgression::class.java, QuestProgression.Serializer)
            builder.registerTypeAdapter(BlockType::class.java, BlockType.Serializer)
            builder.registerTypeAdapter(CrateReward::class.java, CrateReward.Serializer)
            builder.registerTypeAdapter(DeliveryManRewardRequirement::class.java, DeliveryManRewardRequirement.Serializer)
            builder.registerTypeAdapter(UserSettingOption::class.java, AbstractTypeSerializer<UserSettingOption>())
            builder.registerTypeAdapter(Challenge::class.java, AbstractTypeSerializer<Challenge>())
        }

        loadModules()
        loadTasks()
        loadCommands()
    }

    override fun onDisable() {
        enabledModules.forEach { module ->
            logger.info("Disabling ${module.getName()} module...")

            try {
                module.onDisable()
                logger.info("Disabled ${module.getName()} module!")
            } catch (e: Exception) {
                logger.severe("Failed to disable ${module.getName()} module:")
                e.printStackTrace()
            }
        }
    }

    private fun loadModules() {
        val modulesList = listOf(
            EnvironmentModule,
            StorageModule,
            RegionsModule,
            MechanicsModule,
            RewardsModule,
            EnchantsModule,
            RanksModule,
            CratesModule,
//            AchievementsModule,
            QuestsModule,
            ShopsModule,
            MinesModule,
            CellsModule,
            PrivateMinesModule,
            BattlePassModule,
            UsersModule,
            ScoreboardModule,
            ChatModule,
            MinigamesModule,
            LeaderboardsModule,
            CombatModule
        )

        modulesList.filter { !it.requiresLateLoad() }.forEach { module ->
            logger.info("Loading ${module.getName()} module...")

            try {
                module.onEnable()
                module.getCommands().forEach { command -> CommandHandler.registerClass(command) }
                module.getCommandParameterTypes().forEach { parameterEntry -> CommandHandler.registerParameterType(parameterEntry.key, parameterEntry.value) }
                module.getListeners().forEach { listener -> server.pluginManager.registerEvents(listener, this) }

                logger.info("Enabled ${module.getName()} module!")

                enabledModules.add(module)
            } catch (e: Exception) {
                logger.severe("Failed to enable ${module.getName()} module:")
                e.printStackTrace()
            }
        }

        Tasks.delayed(1L) {
            modulesList.filter { it.requiresLateLoad() }.forEach { module ->
                logger.info("Loading ${module.getName()} module... (late load)")

                try {
                    module.onEnable()
                    module.getCommands().forEach { command -> CommandHandler.registerClass(command) }
                    module.getCommandParameterTypes().forEach { parameterEntry -> CommandHandler.registerParameterType(parameterEntry.key, parameterEntry.value) }
                    module.getListeners().forEach { listener -> server.pluginManager.registerEvents(listener, this) }

                    logger.info("Enabled ${module.getName()} module (late load)!")
                } catch (e: Exception) {
                    logger.severe("Failed to enable ${module.getName()} module (late load):")
                    e.printStackTrace()
                }
            }
        }
    }

    fun saveModules() {
        systemLog("Saving data...")

        val startAt = System.currentTimeMillis()

        enabledModules.forEach { module ->
            try {
                module.onAutoSave()
            } catch (exception: Exception) {
                ErrorHandler.generateErrorLog(
                    errorType = "saveModule",
                    event = mapOf("ModuleName" to module.getName()),
                    exception = exception
                )

                systemLog("${ChatColor.RED}Failed to save module ${module.getName()}!")
            }
        }

        val endAt = System.currentTimeMillis()

        systemLog("Finished saving data in ${endAt - startAt}ms!")
    }

    private fun loadTasks() {
        Tasks.asyncTimer(20L * 60L * 3L, 20L * 60L * 3L) {
            saveModules()
        }
    }

    private fun loadCommands() {
        CommandHandler.registerClass(RunWizardCommand::class.java)
        CommandHandler.registerClass(GKitzCommand::class.java)
        CommandHandler.registerClass(ReloadCommand::class.java)
        CommandHandler.registerClass(SaveCommand::class.java)
    }

    fun systemLog(vararg messages: String) {
        for (message in messages) {
            logger.info(message)
        }

        for (player in Bukkit.getOnlinePlayers()) {
            if (player.isOp || player.hasPermission(Permissions.SYSTEM_ADMIN)) {
                for (message in messages) {
                    player.sendMessage("$SYSTEM_PREFIX ${ChatColor.GRAY}$message")
                }
            }
        }
    }

    override fun getDefaultWorldGenerator(worldName: String?, id: String?): ChunkGenerator {
        return EmptyChunkGenerator()
    }

    companion object {
        @JvmStatic lateinit var instance: PrisonAIO

        private val SYSTEM_PREFIX = "${ChatColor.DARK_RED}${ChatColor.BOLD}[SYSTEM]"
    }

}