package net.evilblock.prisonaio.module.enchant

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.enchant.command.*
import net.evilblock.prisonaio.module.enchant.command.parameter.AbstractEnchantParameterType
import net.evilblock.prisonaio.module.enchant.salvage.SalvagePreventionHandler
import net.evilblock.prisonaio.module.enchant.salvage.command.SalvagePreventionCommand
import org.bukkit.event.Listener

object EnchantsModule : PluginModule() {

    override fun getName(): String {
        return "Enchants"
    }

    override fun getConfigFileName(): String {
        return "enchants"
    }

    override fun onEnable() {
        SalvagePreventionHandler.initialLoad()
    }

    override fun onDisable() {
        SalvagePreventionHandler.saveData()
    }

    override fun onAutoSave() {
        SalvagePreventionHandler.saveData()
    }

    override fun getListeners(): List<Listener> {
        return listOf(EnchantsManager)
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            BookCommand.javaClass,
            EnchantCommand.javaClass,
            PickaxeCommand.javaClass,
            RemoveEnchantCommand.javaClass,
            ToggleEnchantsMessagesCommand.javaClass,
            SalvagePreventionCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            AbstractEnchant::class.java to AbstractEnchantParameterType
        )
    }

}