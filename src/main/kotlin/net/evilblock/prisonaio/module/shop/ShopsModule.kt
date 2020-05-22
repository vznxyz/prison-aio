package net.evilblock.prisonaio.module.shop

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.shop.command.*
import net.evilblock.prisonaio.module.shop.command.parameter.ShopParameterType
import net.evilblock.prisonaio.module.shop.command.parameter.ShopReceiptParameterType
import net.evilblock.prisonaio.module.shop.listener.ShopReceiptListeners
import net.evilblock.prisonaio.module.shop.receipt.ShopReceipt
import net.evilblock.prisonaio.module.shop.receipt.task.ShopReceiptExpireTask
import org.bukkit.event.Listener

object ShopsModule : PluginModule() {

    override fun getName(): String {
        return "Shops"
    }

    override fun getConfigFileName(): String {
        return "shops"
    }

    override fun onEnable() {
        ShopHandler.initialLoad()

        getPlugin().server.scheduler.runTaskTimerAsynchronously(getPlugin(), ShopReceiptExpireTask, 2L, 2L)
    }

    override fun onAutoSave() {
        ShopHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            SellAllCommand.javaClass,
            ShopEditorCommand.javaClass,
            ShopReceiptCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Shop::class.java to ShopParameterType,
            ShopReceipt::class.java to ShopReceiptParameterType
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            ShopReceiptListeners
        )
    }

}